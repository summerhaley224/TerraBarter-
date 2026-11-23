package com.example.data.repository

import com.example.data.db.*
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class TerraRepository(private val dao: TerraDao) {

    // USERS
    val allUsersFlow: Flow<List<UserProfile>> = dao.getAllUsers().map { userEntities ->
        userEntities.map { it.toDomain() }
    }

    val localUserFlow: Flow<UserProfile?> = dao.getLocalUserFlow().map { it?.toDomain() }

    suspend fun getUserProfileWithDetails(userId: String): UserProfile? = withContext(Dispatchers.IO) {
        val userEntity = dao.getUserById(userId) ?: return@withContext null
        val skills = dao.getSkillsForUserDirect(userId).map { it.toDomain() }
        val wishlist = dao.getWishlistForUserDirect(userId).map { it.toDomain() }
        userEntity.toDomain().copy(skills = skills, wishlist = wishlist)
    }

    suspend fun updateLocalProfile(
        displayName: String,
        bioRegion: String,
        headline: String,
        bio: String,
        earthPledge: String,
        termsAndConditions: String
    ) = withContext(Dispatchers.IO) {
        val local = dao.getLocalUser() ?: return@withContext
        val updated = local.copy(
            displayName = displayName,
            bioRegion = bioRegion,
            headline = headline,
            bio = bio,
            earthPledge = earthPledge,
            termsAndConditions = termsAndConditions
        )
        dao.updateUser(updated)
    }

    // SKILLS
    val allSkillsFlow: Flow<List<SkillOffering>> = dao.getAllSkills().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getSkillsForUser(userId: String): Flow<List<SkillOffering>> {
        return dao.getSkillsForUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addSkillOffering(
        title: String,
        category: SkillCategory,
        description: String,
        tradePreference: String,
        isPureBarter: Boolean,
        estimatedValue: String,
        tags: List<String>
    ) = withContext(Dispatchers.IO) {
        val local = dao.getLocalUser() ?: return@withContext
        val newSkill = SkillEntity(
            id = "skill_" + UUID.randomUUID().toString().take(8),
            userId = local.id,
            authorName = local.displayName,
            authorHandle = local.handle,
            bioRegion = local.bioRegion,
            title = title,
            categoryName = category.name,
            description = description,
            tradePreference = tradePreference,
            isPureBarter = isPureBarter,
            estimatedValue = estimatedValue,
            tagsJoined = tags.joinToString(",")
        )
        dao.insertSkill(newSkill)
    }

    suspend fun deleteSkill(skillId: String) = withContext(Dispatchers.IO) {
        dao.deleteSkill(skillId)
    }

    // WISHLIST
    val allWishlistFlow: Flow<List<WishlistItem>> = dao.getAllWishlistItems().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getWishlistForUser(userId: String): Flow<List<WishlistItem>> {
        return dao.getWishlistForUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addWishlistItem(
        title: String,
        category: SkillCategory,
        urgency: String,
        willExchangeFor: String
    ) = withContext(Dispatchers.IO) {
        val local = dao.getLocalUser() ?: return@withContext
        val item = WishlistEntity(
            id = "wish_" + UUID.randomUUID().toString().take(8),
            userId = local.id,
            authorName = local.displayName,
            authorHandle = local.handle,
            title = title,
            categoryName = category.name,
            urgency = urgency,
            willExchangeFor = willExchangeFor
        )
        dao.insertWishlist(item)
    }

    suspend fun deleteWishlistItem(itemId: String) = withContext(Dispatchers.IO) {
        dao.deleteWishlist(itemId)
    }

    // TRADES
    val allTradesFlow: Flow<List<BarterTrade>> = dao.getAllTrades().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun proposeTrade(
        receiverId: String,
        receiverName: String,
        offeredTitle: String,
        requestedTitle: String,
        termsAgreed: String
    ) = withContext(Dispatchers.IO) {
        val local = dao.getLocalUser() ?: return@withContext
        val trade = TradeEntity(
            id = "trade_" + UUID.randomUUID().toString().take(8),
            initiatorId = local.id,
            initiatorName = local.displayName,
            receiverId = receiverId,
            receiverName = receiverName,
            offeredItemTitle = offeredTitle,
            requestedItemTitle = requestedTitle,
            termsAgreed = termsAgreed,
            statusName = TradeStatus.PROPOSED.name,
            timestamp = System.currentTimeMillis()
        )
        dao.insertTrade(trade)
    }

    suspend fun updateTradeStatus(tradeId: String, status: TradeStatus) = withContext(Dispatchers.IO) {
        val trade = dao.getTradeById(tradeId) ?: return@withContext
        val updated = trade.copy(statusName = status.name)
        dao.updateTrade(updated)

        if (status == TradeStatus.COMPLETED) {
            // Update trades completed count for both participants
            updateTradesCount(trade.initiatorId)
            updateTradesCount(trade.receiverId)
        }
    }

    suspend fun submitTradeReview(
        tradeId: String,
        rating: Int,
        comment: String,
        stewardshipBadge: String
    ) = withContext(Dispatchers.IO) {
        val trade = dao.getTradeById(tradeId) ?: return@withContext
        val local = dao.getLocalUser() ?: return@withContext

        // Determine who is being rated
        val targetUserId = if (trade.initiatorId == local.id) trade.receiverId else trade.initiatorId

        // Update trade with review
        val updatedTrade = trade.copy(
            statusName = TradeStatus.REVIEWED.name,
            ratingScore = rating,
            reviewComment = comment,
            stewardshipBadge = stewardshipBadge
        )
        dao.updateTrade(updatedTrade)

        // Insert honest review entity
        val review = ReviewEntity(
            id = "rev_" + UUID.randomUUID().toString().take(8),
            tradeId = tradeId,
            reviewerId = local.id,
            reviewerName = local.displayName,
            targetUserId = targetUserId,
            rating = rating,
            comment = comment,
            stewardshipRating = stewardshipBadge,
            verifiedTrade = true,
            timestamp = System.currentTimeMillis()
        )
        dao.insertReview(review)

        // Recalculate target user average rating
        recalculateUserRating(targetUserId)
    }

    private suspend fun updateTradesCount(userId: String) {
        val user = dao.getUserById(userId) ?: return
        dao.updateUser(user.copy(tradesCompleted = user.tradesCompleted + 1))
    }

    private suspend fun recalculateUserRating(userId: String) {
        val user = dao.getUserById(userId) ?: return
        val currentAvg = user.ratingAverage
        val currentCount = user.reviewCount
        // Weighted updated average
        val newCount = currentCount + 1
        val newAvg = ((currentAvg * currentCount) + 5f) / newCount
        dao.updateUser(user.copy(ratingAverage = "%.2f".format(newAvg).toFloat(), reviewCount = newCount))
    }

    // REVIEWS
    fun getReviewsForUser(userId: String): Flow<List<HonestReview>> {
        return dao.getReviewsForUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // REFERRALS
    val allReferralsFlow: Flow<List<PeerReferral>> = dao.getAllReferrals().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun createReferral(
        introducedPersonId: String,
        introducedPersonName: String,
        seekerPersonId: String,
        seekerPersonName: String,
        skillFocus: String,
        introNote: String
    ) = withContext(Dispatchers.IO) {
        val local = dao.getLocalUser() ?: return@withContext
        val referral = ReferralEntity(
            id = "ref_" + UUID.randomUUID().toString().take(8),
            referrerId = local.id,
            referrerName = local.displayName,
            introducedPersonId = introducedPersonId,
            introducedPersonName = introducedPersonName,
            seekerPersonId = seekerPersonId,
            seekerPersonName = seekerPersonName,
            serviceOrSkillFocus = skillFocus,
            introNote = introNote,
            timestamp = System.currentTimeMillis(),
            isContacted = false
        )
        dao.insertReferral(referral)
    }

    suspend fun markReferralContacted(referralId: String, contacted: Boolean) = withContext(Dispatchers.IO) {
        dao.updateReferralContacted(referralId, contacted)
    }

    // NODE SOVEREIGNTY & EXPORT/IMPORT
    val nodeInfoFlow: Flow<NodeIdentity?> = dao.getNodeInfoFlow().map { it?.toDomain() }

    suspend fun toggleMeshSync(syncing: Boolean) = withContext(Dispatchers.IO) {
        val node = dao.getNodeInfo() ?: return@withContext
        val peers = if (syncing) 8 else 0
        dao.updateNodeMeshStatus(node.nodeId, syncing, peers)
    }

    suspend fun exportNodeJson(): String = withContext(Dispatchers.IO) {
        val local = dao.getLocalUser()
        val skills = local?.let { dao.getSkillsForUserDirect(it.id) } ?: emptyList()
        val wishlist = local?.let { dao.getWishlistForUserDirect(it.id) } ?: emptyList()
        val node = dao.getNodeInfo()

        val json = JSONObject()
        json.put("protocol", "TerraBarter/P2P-v1.0")
        json.put("exportedAt", System.currentTimeMillis())
        json.put("sovereignty", "Decentralized Personal Node - Zero Central Lock-in")

        val nodeObj = JSONObject()
        nodeObj.put("nodeId", node?.nodeId ?: "node_unknown")
        nodeObj.put("nodeAlias", node?.nodeAlias ?: "MyNode")
        nodeObj.put("publicKey", node?.publicKeyFingerprint ?: "")
        json.put("nodeIdentity", nodeObj)

        local?.let {
            val userObj = JSONObject()
            userObj.put("id", it.id)
            userObj.put("handle", it.handle)
            userObj.put("displayName", it.displayName)
            userObj.put("bioRegion", it.bioRegion)
            userObj.put("headline", it.headline)
            userObj.put("bio", it.bio)
            userObj.put("earthPledge", it.earthPledge)
            userObj.put("termsAndConditions", it.termsAndConditions)
            userObj.put("ratingAverage", it.ratingAverage.toDouble())
            userObj.put("tradesCompleted", it.tradesCompleted)
            json.put("profile", userObj)
        }

        val skillsArray = JSONArray()
        skills.forEach { s ->
            val sObj = JSONObject()
            sObj.put("title", s.title)
            sObj.put("category", s.categoryName)
            sObj.put("description", s.description)
            sObj.put("tradePreference", s.tradePreference)
            sObj.put("isPureBarter", s.isPureBarter)
            sObj.put("estimatedValue", s.estimatedValue)
            sObj.put("tags", s.tagsJoined)
            skillsArray.put(sObj)
        }
        json.put("skillsOffered", skillsArray)

        val wishArray = JSONArray()
        wishlist.forEach { w ->
            val wObj = JSONObject()
            wObj.put("title", w.title)
            wObj.put("category", w.categoryName)
            wObj.put("urgency", w.urgency)
            wObj.put("willExchangeFor", w.willExchangeFor)
            wishArray.put(wObj)
        }
        json.put("wishlistNeeds", wishArray)

        json.toString(2)
    }

    suspend fun importNodeJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject(jsonString)
            if (json.has("profile")) {
                val p = json.getJSONObject("profile")
                val local = dao.getLocalUser() ?: return@withContext false
                val updated = local.copy(
                    displayName = p.optString("displayName", local.displayName),
                    bioRegion = p.optString("bioRegion", local.bioRegion),
                    headline = p.optString("headline", local.headline),
                    bio = p.optString("bio", local.bio),
                    earthPledge = p.optString("earthPledge", local.earthPledge),
                    termsAndConditions = p.optString("termsAndConditions", local.termsAndConditions)
                )
                dao.updateUser(updated)
            }

            if (json.has("skillsOffered")) {
                val array = json.getJSONArray("skillsOffered")
                val local = dao.getLocalUser() ?: return@withContext false
                for (i in 0 until array.length()) {
                    val s = array.getJSONObject(i)
                    val skill = SkillEntity(
                        id = "skill_" + UUID.randomUUID().toString().take(8),
                        userId = local.id,
                        authorName = local.displayName,
                        authorHandle = local.handle,
                        bioRegion = local.bioRegion,
                        title = s.optString("title", "Imported Skill"),
                        categoryName = s.optString("category", SkillCategory.FARMING_PRODUCE.name),
                        description = s.optString("description", ""),
                        tradePreference = s.optString("tradePreference", "Pure Barter"),
                        isPureBarter = s.optBoolean("isPureBarter", true),
                        estimatedValue = s.optString("estimatedValue", "Barter"),
                        tagsJoined = s.optString("tags", "Imported")
                    )
                    dao.insertSkill(skill)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

// Converters Extension Functions
fun UserEntity.toDomain() = UserProfile(
    id = id,
    handle = handle,
    displayName = displayName,
    nodeAddress = nodeAddress,
    bioRegion = bioRegion,
    headline = headline,
    bio = bio,
    earthPledge = earthPledge,
    termsAndConditions = termsAndConditions,
    ratingAverage = ratingAverage,
    reviewCount = reviewCount,
    tradesCompleted = tradesCompleted,
    isLocalNodeUser = isLocalNodeUser,
    avatarColorHex = avatarColorHex,
    joinedYear = joinedYear
)

fun SkillEntity.toDomain() = SkillOffering(
    id = id,
    userId = userId,
    authorName = authorName,
    authorHandle = authorHandle,
    bioRegion = bioRegion,
    title = title,
    category = try { SkillCategory.valueOf(categoryName) } catch (e: Exception) { SkillCategory.FARMING_PRODUCE },
    description = description,
    tradePreference = tradePreference,
    isPureBarter = isPureBarter,
    estimatedValue = estimatedValue,
    tags = if (tagsJoined.isNotBlank()) tagsJoined.split(",") else emptyList()
)

fun WishlistEntity.toDomain() = WishlistItem(
    id = id,
    userId = userId,
    authorName = authorName,
    authorHandle = authorHandle,
    title = title,
    category = try { SkillCategory.valueOf(categoryName) } catch (e: Exception) { SkillCategory.FARMING_PRODUCE },
    urgency = urgency,
    willExchangeFor = willExchangeFor
)

fun TradeEntity.toDomain() = BarterTrade(
    id = id,
    initiatorId = initiatorId,
    initiatorName = initiatorName,
    receiverId = receiverId,
    receiverName = receiverName,
    offeredItemTitle = offeredItemTitle,
    requestedItemTitle = requestedItemTitle,
    termsAgreed = termsAgreed,
    status = try { TradeStatus.valueOf(statusName) } catch (e: Exception) { TradeStatus.PROPOSED },
    timestamp = timestamp,
    ratingScore = ratingScore,
    reviewComment = reviewComment,
    stewardshipBadge = stewardshipBadge
)

fun ReferralEntity.toDomain() = PeerReferral(
    id = id,
    referrerId = referrerId,
    referrerName = referrerName,
    introducedPersonId = introducedPersonId,
    introducedPersonName = introducedPersonName,
    seekerPersonId = seekerPersonId,
    seekerPersonName = seekerPersonName,
    serviceOrSkillFocus = serviceOrSkillFocus,
    introNote = introNote,
    timestamp = timestamp,
    isContacted = isContacted
)

fun ReviewEntity.toDomain() = HonestReview(
    id = id,
    tradeId = tradeId,
    reviewerId = reviewerId,
    reviewerName = reviewerName,
    targetUserId = targetUserId,
    rating = rating,
    comment = comment,
    stewardshipRating = stewardshipRating,
    verifiedTrade = verifiedTrade,
    timestamp = timestamp
)

fun NodeEntity.toDomain() = NodeIdentity(
    nodeId = nodeId,
    nodeAlias = nodeAlias,
    publicKeyFingerprint = publicKeyFingerprint,
    localPort = localPort,
    isMeshSyncing = isMeshSyncing,
    connectedPeersCount = connectedPeersCount,
    storageUsageKb = storageUsageKb,
    earthStewardshipVerified = earthStewardshipVerified
)
