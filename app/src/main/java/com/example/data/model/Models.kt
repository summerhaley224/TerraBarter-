package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector

enum class SkillCategory(val displayName: String, val shortLabel: String) {
    FARMING_PRODUCE("Local Growers & Farmers", "Growers & Food"),
    ARTISAN_CRAFT("Artisans & Soap Makers", "Artisans & Goods"),
    FIXERS_BUILDERS("Fixers, Mechanics & Builders", "Fixers & Repairs"),
    HEALERS_TEACHERS("Teachers & Holistic Healers", "Healers & Teachers"),
    RENEWABLE_ENERGY("Renewable Energy & Solar", "Clean Energy"),
    PLUMBING_WATER("Sustainable Plumbers & Water", "Water & Plumbing"),
    TRANSPORT_DELIVERY("Community Drivers & Transport", "Transport & Hauling"),
    EARTH_STEWARDSHIP("Earth Ecology & Soil Care", "Earth Stewardship");

    fun getIcon(): ImageVector = when (this) {
        FARMING_PRODUCE -> Icons.Default.Grass
        ARTISAN_CRAFT -> Icons.Default.Palette
        FIXERS_BUILDERS -> Icons.Default.Build
        HEALERS_TEACHERS -> Icons.Default.WorkspacePremium
        RENEWABLE_ENERGY -> Icons.Default.ElectricBolt
        PLUMBING_WATER -> Icons.Default.WaterDrop
        TRANSPORT_DELIVERY -> Icons.Default.LocalShipping
        EARTH_STEWARDSHIP -> Icons.Default.Eco
    }
}

enum class TradeStatus(val label: String) {
    PROPOSED("Proposed"),
    ACCEPTED("Accepted / Active"),
    COMPLETED("Completed"),
    REVIEWED("Completed & Rated"),
    DECLINED("Declined")
}

data class UserProfile(
    val id: String,
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
    val joinedYear: String,
    val skills: List<SkillOffering> = emptyList(),
    val wishlist: List<WishlistItem> = emptyList()
)

data class SkillOffering(
    val id: String,
    val userId: String,
    val authorName: String,
    val authorHandle: String,
    val bioRegion: String,
    val title: String,
    val category: SkillCategory,
    val description: String,
    val tradePreference: String,
    val isPureBarter: Boolean,
    val estimatedValue: String,
    val tags: List<String> = emptyList()
)

data class WishlistItem(
    val id: String,
    val userId: String,
    val authorName: String,
    val authorHandle: String,
    val title: String,
    val category: SkillCategory,
    val urgency: String, // "High Need", "Flexible", "Ongoing"
    val willExchangeFor: String
)

data class BarterTrade(
    val id: String,
    val initiatorId: String,
    val initiatorName: String,
    val receiverId: String,
    val receiverName: String,
    val offeredItemTitle: String,
    val requestedItemTitle: String,
    val termsAgreed: String,
    val status: TradeStatus,
    val timestamp: Long,
    val ratingScore: Int? = null,
    val reviewComment: String? = null,
    val stewardshipBadge: String? = null
)

data class PeerReferral(
    val id: String,
    val referrerId: String,
    val referrerName: String,
    val introducedPersonId: String,
    val introducedPersonName: String,
    val seekerPersonId: String,
    val seekerPersonName: String,
    val serviceOrSkillFocus: String,
    val introNote: String,
    val timestamp: Long,
    val isContacted: Boolean = false
)

data class HonestReview(
    val id: String,
    val tradeId: String,
    val reviewerId: String,
    val reviewerName: String,
    val targetUserId: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val stewardshipRating: String, // "Very Mindful of Earth & Resources", "Excellent Craftsmanship", etc.
    val verifiedTrade: Boolean,
    val timestamp: Long
)

data class NodeIdentity(
    val nodeId: String,
    val nodeAlias: String,
    val publicKeyFingerprint: String,
    val localPort: Int,
    val isMeshSyncing: Boolean,
    val connectedPeersCount: Int,
    val storageUsageKb: Long,
    val earthStewardshipVerified: Boolean
)

data class FullNodeBackup(
    val exportTimestamp: Long,
    val nodeAddress: String,
    val profile: UserProfile,
    val skills: List<SkillOffering>,
    val wishlist: List<WishlistItem>,
    val trades: List<BarterTrade>,
    val referrals: List<PeerReferral>,
    val reviews: List<HonestReview>
)
