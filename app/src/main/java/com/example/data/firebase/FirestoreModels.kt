package com.example.data.firebase

import android.util.Log
import com.example.data.model.BarterTrade
import com.example.data.model.PeerReferral
import com.example.data.model.SkillCategory
import com.example.data.model.SkillOffering
import com.example.data.model.TradeStatus
import com.example.data.model.UserProfile
import com.example.data.model.WishlistItem
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.coroutines.CancellationException
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(
    exception: Throwable,
    operationType: OperationType,
    path: String?
): String {
    val causes = generateSequence(exception) { it.cause }
    val rootError = causes.firstOrNull { it is FirebaseFirestoreException }
        ?: causes.firstOrNull { it !is CancellationException }
        ?: exception

    val currentUser = try {
        Firebase.auth.currentUser
    } catch (_: Exception) {
        null
    }

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", rootError.message ?: rootError.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}

@IgnoreExtraProperties
data class CloudUserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val handle: String = "",
    val displayName: String = "",
    val bioRegion: String = "",
    val nodeAddress: String = "",
    val headline: String = "",
    val bio: String = "",
    val earthPledge: String = "",
    val termsAndConditions: String = "",
    val skills: List<String> = emptyList(),
    val barterItems: List<String> = emptyList(),
    val wishlist: List<String> = emptyList(),
    val ratingAverage: Double = 5.0,
    val tradesCompleted: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toDomain(isLocalNodeUser: Boolean = true): UserProfile = UserProfile(
        id = userId.ifBlank { id },
        handle = handle,
        displayName = displayName,
        nodeAddress = nodeAddress.ifBlank { "terranode://${userId.take(8)}.local:8443" },
        bioRegion = bioRegion,
        headline = headline,
        bio = bio,
        earthPledge = earthPledge,
        termsAndConditions = termsAndConditions,
        ratingAverage = ratingAverage.toFloat(),
        reviewCount = tradesCompleted.toInt(),
        tradesCompleted = tradesCompleted.toInt(),
        isLocalNodeUser = isLocalNodeUser,
        avatarColorHex = 0xFF2D6A4FL,
        joinedYear = "2025"
    )
}

@IgnoreExtraProperties
data class CloudSkillOffering(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorName: String = "",
    val authorHandle: String = "",
    val bioRegion: String = "",
    val title: String = "",
    val category: String = SkillCategory.FARMING_PRODUCE.name,
    val description: String = "",
    val tradePreference: String = "",
    @get:PropertyName("isPureBarter")
    @set:PropertyName("isPureBarter")
    @field:PropertyName("isPureBarter")
    var isPureBarter: Boolean = true,
    val estimatedValue: String = "",
    val tags: List<String> = emptyList(),
    @get:PropertyName("isPublic")
    @set:PropertyName("isPublic")
    @field:PropertyName("isPublic")
    var isPublic: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toDomain(): SkillOffering = SkillOffering(
        id = id,
        userId = userId,
        authorName = authorName,
        authorHandle = authorHandle,
        bioRegion = bioRegion,
        title = title,
        category = try {
            SkillCategory.valueOf(category)
        } catch (_: Exception) {
            SkillCategory.FARMING_PRODUCE
        },
        description = description,
        tradePreference = tradePreference,
        isPureBarter = isPureBarter,
        estimatedValue = estimatedValue,
        tags = tags
    )
}

@IgnoreExtraProperties
data class CloudWishlistItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorName: String = "",
    val authorHandle: String = "",
    val title: String = "",
    val category: String = SkillCategory.FARMING_PRODUCE.name,
    val urgency: String = "Flexible",
    val willExchangeFor: String = "",
    @get:PropertyName("isPublic")
    @set:PropertyName("isPublic")
    @field:PropertyName("isPublic")
    var isPublic: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toDomain(): WishlistItem = WishlistItem(
        id = id,
        userId = userId,
        authorName = authorName,
        authorHandle = authorHandle,
        title = title,
        category = try {
            SkillCategory.valueOf(category)
        } catch (_: Exception) {
            SkillCategory.FARMING_PRODUCE
        },
        urgency = urgency,
        willExchangeFor = willExchangeFor
    )
}

@IgnoreExtraProperties
data class CloudBarterTrade(
    @DocumentId val id: String = "",
    val initiatorId: String = "",
    val initiatorName: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val participantIds: List<String> = emptyList(),
    val offeredItemTitle: String = "",
    val requestedItemTitle: String = "",
    val termsAgreed: String = "",
    val status: String = TradeStatus.PROPOSED.name,
    val ratingScore: Int? = null,
    val reviewComment: String? = null,
    val stewardshipBadge: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toDomain(): BarterTrade = BarterTrade(
        id = id,
        initiatorId = initiatorId,
        initiatorName = initiatorName,
        receiverId = receiverId,
        receiverName = receiverName,
        offeredItemTitle = offeredItemTitle,
        requestedItemTitle = requestedItemTitle,
        termsAgreed = termsAgreed,
        status = try {
            TradeStatus.valueOf(status)
        } catch (_: Exception) {
            TradeStatus.PROPOSED
        },
        timestamp = (updatedAt ?: createdAt)?.toDate()?.time ?: System.currentTimeMillis(),
        ratingScore = ratingScore,
        reviewComment = reviewComment,
        stewardshipBadge = stewardshipBadge
    )
}

@IgnoreExtraProperties
data class CloudPeerReferral(
    @DocumentId val id: String = "",
    val referrerId: String = "",
    val referrerName: String = "",
    val introducedPersonId: String = "",
    val introducedPersonName: String = "",
    val seekerPersonId: String = "",
    val seekerPersonName: String = "",
    val serviceOrSkillFocus: String = "",
    val introNote: String = "",
    @get:PropertyName("isContacted")
    @set:PropertyName("isContacted")
    @field:PropertyName("isContacted")
    var isContacted: Boolean = false,
    @get:PropertyName("isPublic")
    @set:PropertyName("isPublic")
    @field:PropertyName("isPublic")
    var isPublic: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toDomain(): PeerReferral = PeerReferral(
        id = id,
        referrerId = referrerId,
        referrerName = referrerName,
        introducedPersonId = introducedPersonId,
        introducedPersonName = introducedPersonName,
        seekerPersonId = seekerPersonId,
        seekerPersonName = seekerPersonName,
        serviceOrSkillFocus = serviceOrSkillFocus,
        introNote = introNote,
        timestamp = (createdAt ?: updatedAt)?.toDate()?.time ?: System.currentTimeMillis(),
        isContacted = isContacted
    )
}
