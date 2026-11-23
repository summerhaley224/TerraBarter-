package com.example.data.firebase

import android.content.Context
import com.example.R
import com.example.data.model.SkillCategory
import com.example.data.model.TradeStatus
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        db = FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        auth = Firebase.auth
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before writing to Firestore.")
    }

    private fun sanitizeId(raw: String, prefix: String): String {
        val cleaned = raw.replace(Regex("[^a-zA-Z0-9_\\-]"), "")
        return if (cleaned.isNotBlank()) cleaned.take(128) else "${prefix}_${UUID.randomUUID().toString().replace("-", "").take(12)}"
    }

    // =========================================================================
    // 1. SOVEREIGN USER PROFILE (/users/{userId})
    // =========================================================================

    fun observeUserProfile(userId: String): Flow<CloudUserProfile?> = flow {
        val path = "$COL_USERS/$userId"
        emitAll(
            db.collection(COL_USERS).document(userId)
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            CloudUserProfile::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else {
                        null
                    }
                }
                .catch { error ->
                    logStreamErrorIfActive(error, OperationType.GET, path, userId)
                    throw error
                }
        )
    }

    suspend fun getUserProfile(userId: String): Result<CloudUserProfile?> {
        val docRef = db.collection(COL_USERS).document(userId)
        return try {
            val snapshot = docRef.get().await()
            val profile = if (snapshot.exists()) {
                snapshot.toObject(
                    CloudUserProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            } else {
                null
            }
            Result.success(profile)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun upsertUserProfile(profile: CloudUserProfile): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection(COL_USERS).document(uid)
        return try {
            val existing = docRef.get().await()
            if (existing.exists()) {
                val updates = mapOf(
                    "handle" to profile.handle.ifBlank { "@steward_${uid.take(6)}" },
                    "displayName" to profile.displayName.ifBlank { "Community Steward" },
                    "bioRegion" to profile.bioRegion,
                    "nodeAddress" to profile.nodeAddress,
                    "headline" to profile.headline,
                    "bio" to profile.bio,
                    "earthPledge" to profile.earthPledge,
                    "termsAndConditions" to profile.termsAndConditions,
                    "skills" to profile.skills.take(20),
                    "barterItems" to profile.barterItems.take(20),
                    "wishlist" to profile.wishlist.take(20),
                    "ratingAverage" to profile.ratingAverage.coerceIn(0.0, 5.0),
                    "tradesCompleted" to profile.tradesCompleted.coerceAtLeast(0L),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updates).await()
            } else {
                val payload = mapOf(
                    "userId" to uid,
                    "handle" to profile.handle.ifBlank { "@steward_${uid.take(6)}" },
                    "displayName" to profile.displayName.ifBlank { "Community Steward" },
                    "bioRegion" to profile.bioRegion,
                    "nodeAddress" to profile.nodeAddress,
                    "headline" to profile.headline,
                    "bio" to profile.bio,
                    "earthPledge" to profile.earthPledge,
                    "termsAndConditions" to profile.termsAndConditions,
                    "skills" to profile.skills.take(20),
                    "barterItems" to profile.barterItems.take(20),
                    "wishlist" to profile.wishlist.take(20),
                    "ratingAverage" to profile.ratingAverage.coerceIn(0.0, 5.0),
                    "tradesCompleted" to profile.tradesCompleted.coerceAtLeast(0L),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    // =========================================================================
    // 2. SKILL & BARTER OFFERINGS (/skill_offerings/{offeringId})
    // =========================================================================

    fun observePublicSkillOfferings(): Flow<List<CloudSkillOffering>> = flow {
        val activeUid = auth.currentUser?.uid
        emitAll(
            db.collection(COL_SKILL_OFFERINGS)
                .whereEqualTo("isPublic", true)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        CloudSkillOffering::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    logStreamErrorIfActive(error, OperationType.LIST, COL_SKILL_OFFERINGS, activeUid)
                    throw error
                }
        )
    }

    suspend fun createSkillOffering(
        title: String,
        category: SkillCategory,
        description: String,
        tradePreference: String,
        isPureBarter: Boolean,
        estimatedValue: String,
        tags: List<String>,
        authorName: String,
        authorHandle: String,
        bioRegion: String,
        offeringId: String = ""
    ): Result<String> {
        val uid = requireUserId()
        val id = sanitizeId(offeringId, "skill")
        val docRef = db.collection(COL_SKILL_OFFERINGS).document(id)
        val payload = mapOf(
            "userId" to uid,
            "authorName" to authorName.ifBlank { "Community Steward" },
            "authorHandle" to authorHandle.ifBlank { "@steward_${uid.take(6)}" },
            "bioRegion" to bioRegion,
            "title" to title.ifBlank { "Community Offering" },
            "category" to category.name,
            "description" to description,
            "tradePreference" to tradePreference,
            "isPureBarter" to isPureBarter,
            "estimatedValue" to estimatedValue,
            "tags" to tags.take(10),
            "isPublic" to true,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateSkillOfferingTitle(offeringId: String, newTitle: String): Result<Unit> {
        requireUserId()
        val docRef = db.collection(COL_SKILL_OFFERINGS).document(offeringId)
        return try {
            docRef.update(
                mapOf(
                    "title" to newTitle,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteSkillOffering(offeringId: String): Result<Unit> {
        requireUserId()
        val docRef = db.collection(COL_SKILL_OFFERINGS).document(offeringId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    // =========================================================================
    // 3. WISHLIST ITEMS (/wishlist_items/{itemId})
    // =========================================================================

    fun observePublicWishlistItems(): Flow<List<CloudWishlistItem>> = flow {
        val activeUid = auth.currentUser?.uid
        emitAll(
            db.collection(COL_WISHLIST_ITEMS)
                .whereEqualTo("isPublic", true)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        CloudWishlistItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    logStreamErrorIfActive(error, OperationType.LIST, COL_WISHLIST_ITEMS, activeUid)
                    throw error
                }
        )
    }

    suspend fun createWishlistItem(
        title: String,
        category: SkillCategory,
        urgency: String,
        willExchangeFor: String,
        authorName: String,
        authorHandle: String,
        itemId: String = ""
    ): Result<String> {
        val uid = requireUserId()
        val id = sanitizeId(itemId, "wish")
        val docRef = db.collection(COL_WISHLIST_ITEMS).document(id)
        val payload = mapOf(
            "userId" to uid,
            "authorName" to authorName.ifBlank { "Community Steward" },
            "authorHandle" to authorHandle.ifBlank { "@steward_${uid.take(6)}" },
            "title" to title.ifBlank { "Community Need" },
            "category" to category.name,
            "urgency" to urgency.ifBlank { "Flexible" },
            "willExchangeFor" to willExchangeFor,
            "isPublic" to true,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteWishlistItem(itemId: String): Result<Unit> {
        requireUserId()
        val docRef = db.collection(COL_WISHLIST_ITEMS).document(itemId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    // =========================================================================
    // 4. PEER-TO-PEER BARTER TRADES (/trades/{tradeId})
    // =========================================================================

    fun observeUserTrades(userId: String): Flow<List<CloudBarterTrade>> = flow {
        emitAll(
            db.collection(COL_TRADES)
                .whereArrayContains("participantIds", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        CloudBarterTrade::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    logStreamErrorIfActive(error, OperationType.LIST, COL_TRADES, userId)
                    throw error
                }
        )
    }

    suspend fun proposeTrade(
        initiatorName: String,
        receiverId: String,
        receiverName: String,
        offeredItemTitle: String,
        requestedItemTitle: String,
        termsAgreed: String,
        tradeId: String = ""
    ): Result<String> {
        val uid = requireUserId()
        val id = sanitizeId(tradeId, "trade")
        val docRef = db.collection(COL_TRADES).document(id)
        val payload = mapOf(
            "initiatorId" to uid,
            "initiatorName" to initiatorName.ifBlank { "Community Steward" },
            "receiverId" to receiverId,
            "receiverName" to receiverName.ifBlank { "Community Member" },
            "participantIds" to listOf(uid, receiverId),
            "offeredItemTitle" to offeredItemTitle,
            "requestedItemTitle" to requestedItemTitle,
            "termsAgreed" to termsAgreed,
            "status" to TradeStatus.PROPOSED.name,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateTradeStatus(tradeId: String, newStatus: TradeStatus): Result<Unit> {
        requireUserId()
        val docRef = db.collection(COL_TRADES).document(tradeId)
        return try {
            docRef.update(
                mapOf(
                    "status" to newStatus.name,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun submitTradeReview(
        tradeId: String,
        ratingScore: Int,
        reviewComment: String,
        stewardshipBadge: String
    ): Result<Unit> {
        requireUserId()
        val docRef = db.collection(COL_TRADES).document(tradeId)
        return try {
            docRef.update(
                mapOf(
                    "status" to TradeStatus.REVIEWED.name,
                    "ratingScore" to ratingScore.coerceIn(1, 5),
                    "reviewComment" to reviewComment,
                    "stewardshipBadge" to stewardshipBadge,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // =========================================================================
    // 5. PEER REFERRALS (/referrals/{referralId})
    // =========================================================================

    fun observePublicReferrals(): Flow<List<CloudPeerReferral>> = flow {
        val activeUid = auth.currentUser?.uid
        emitAll(
            db.collection(COL_REFERRALS)
                .whereEqualTo("isPublic", true)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        CloudPeerReferral::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    logStreamErrorIfActive(error, OperationType.LIST, COL_REFERRALS, activeUid)
                    throw error
                }
        )
    }

    suspend fun createReferral(
        referrerName: String,
        introducedPersonId: String,
        introducedPersonName: String,
        seekerPersonId: String,
        seekerPersonName: String,
        serviceOrSkillFocus: String,
        introNote: String,
        referralId: String = ""
    ): Result<String> {
        val uid = requireUserId()
        val id = sanitizeId(referralId, "ref")
        val docRef = db.collection(COL_REFERRALS).document(id)
        val payload = mapOf(
            "referrerId" to uid,
            "referrerName" to referrerName.ifBlank { "Community Steward" },
            "introducedPersonId" to introducedPersonId,
            "introducedPersonName" to introducedPersonName,
            "seekerPersonId" to seekerPersonId,
            "seekerPersonName" to seekerPersonName,
            "serviceOrSkillFocus" to serviceOrSkillFocus,
            "introNote" to introNote,
            "isContacted" to false,
            "isPublic" to true,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun markReferralContacted(referralId: String, contacted: Boolean): Result<Unit> {
        requireUserId()
        val docRef = db.collection(COL_REFERRALS).document(referralId)
        return try {
            docRef.update(
                mapOf(
                    "isContacted" to contacted,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    private fun logStreamErrorIfActive(
        error: Throwable,
        operationType: OperationType,
        path: String,
        expectedUid: String?
    ) {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) return
        val exceptionToLog = firestoreError ?: (error as? Exception) ?: return
        if (expectedUid != null && auth.currentUser?.uid == expectedUid) {
            handleFirestoreError(exceptionToLog, operationType, path)
        }
    }

    companion object {
        const val COL_USERS = "users"
        const val COL_SKILL_OFFERINGS = "skill_offerings"
        const val COL_WISHLIST_ITEMS = "wishlist_items"
        const val COL_TRADES = "trades"
        const val COL_REFERRALS = "referrals"
    }
}
