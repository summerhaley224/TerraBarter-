package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val handle: String,
    val displayName: String,
    val nodeAddress: String,
    val bioRegion: String,
    val headline: String,
    val bio: String,
    val earthPledge: String,
    val termsAndConditions: String,
    val ratingAverage: Float,
    val reviewCount: Int,
    val tradesCompleted: Int,
    val isLocalNodeUser: Boolean,
    val avatarColorHex: Long,
    val joinedYear: String
)

@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val authorName: String,
    val authorHandle: String,
    val bioRegion: String,
    val title: String,
    val categoryName: String,
    val description: String,
    val tradePreference: String,
    val isPureBarter: Boolean,
    val estimatedValue: String,
    val tagsJoined: String // comma separated
)

@Entity(tableName = "wishlists")
data class WishlistEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val authorName: String,
    val authorHandle: String,
    val title: String,
    val categoryName: String,
    val urgency: String,
    val willExchangeFor: String
)

@Entity(tableName = "trades")
data class TradeEntity(
    @PrimaryKey val id: String,
    val initiatorId: String,
    val initiatorName: String,
    val receiverId: String,
    val receiverName: String,
    val offeredItemTitle: String,
    val requestedItemTitle: String,
    val termsAgreed: String,
    val statusName: String,
    val timestamp: Long,
    val ratingScore: Int? = null,
    val reviewComment: String? = null,
    val stewardshipBadge: String? = null
)

@Entity(tableName = "referrals")
data class ReferralEntity(
    @PrimaryKey val id: String,
    val referrerId: String,
    val referrerName: String,
    val introducedPersonId: String,
    val introducedPersonName: String,
    val seekerPersonId: String,
    val seekerPersonName: String,
    val serviceOrSkillFocus: String,
    val introNote: String,
    val timestamp: Long,
    val isContacted: Boolean
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val tradeId: String,
    val reviewerId: String,
    val reviewerName: String,
    val targetUserId: String,
    val rating: Int,
    val comment: String,
    val stewardshipRating: String,
    val verifiedTrade: Boolean,
    val timestamp: Long
)

@Entity(tableName = "node_info")
data class NodeEntity(
    @PrimaryKey val nodeId: String,
    val nodeAlias: String,
    val publicKeyFingerprint: String,
    val localPort: Int,
    val isMeshSyncing: Boolean,
    val connectedPeersCount: Int,
    val storageUsageKb: Long,
    val earthStewardshipVerified: Boolean
)
