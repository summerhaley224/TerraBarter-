package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TerraDao {

    // USERS
    @Query("SELECT * FROM users ORDER BY ratingAverage DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE isLocalNodeUser = 1 LIMIT 1")
    fun getLocalUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isLocalNodeUser = 1 LIMIT 1")
    suspend fun getLocalUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    // SKILLS
    @Query("SELECT * FROM skills ORDER BY id DESC")
    fun getAllSkills(): Flow<List<SkillEntity>>

    @Query("SELECT * FROM skills WHERE userId = :userId")
    fun getSkillsForUser(userId: String): Flow<List<SkillEntity>>

    @Query("SELECT * FROM skills WHERE userId = :userId")
    suspend fun getSkillsForUserDirect(userId: String): List<SkillEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: SkillEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkills(skills: List<SkillEntity>)

    @Query("DELETE FROM skills WHERE id = :skillId")
    suspend fun deleteSkill(skillId: String)

    // WISHLISTS
    @Query("SELECT * FROM wishlists ORDER BY id DESC")
    fun getAllWishlistItems(): Flow<List<WishlistEntity>>

    @Query("SELECT * FROM wishlists WHERE userId = :userId")
    fun getWishlistForUser(userId: String): Flow<List<WishlistEntity>>

    @Query("SELECT * FROM wishlists WHERE userId = :userId")
    suspend fun getWishlistForUserDirect(userId: String): List<WishlistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlist(item: WishlistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlists(items: List<WishlistEntity>)

    @Query("DELETE FROM wishlists WHERE id = :itemId")
    suspend fun deleteWishlist(itemId: String)

    // TRADES
    @Query("SELECT * FROM trades ORDER BY timestamp DESC")
    fun getAllTrades(): Flow<List<TradeEntity>>

    @Query("SELECT * FROM trades WHERE id = :tradeId LIMIT 1")
    suspend fun getTradeById(tradeId: String): TradeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: TradeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrades(trades: List<TradeEntity>)

    @Update
    suspend fun updateTrade(trade: TradeEntity)

    // REFERRALS
    @Query("SELECT * FROM referrals ORDER BY timestamp DESC")
    fun getAllReferrals(): Flow<List<ReferralEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferral(referral: ReferralEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferrals(referrals: List<ReferralEntity>)

    @Query("UPDATE referrals SET isContacted = :contacted WHERE id = :referralId")
    suspend fun updateReferralContacted(referralId: String, contacted: Boolean)

    // REVIEWS
    @Query("SELECT * FROM reviews WHERE targetUserId = :userId ORDER BY timestamp DESC")
    fun getReviewsForUser(userId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY timestamp DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    // NODE INFO
    @Query("SELECT * FROM node_info LIMIT 1")
    fun getNodeInfoFlow(): Flow<NodeEntity?>

    @Query("SELECT * FROM node_info LIMIT 1")
    suspend fun getNodeInfo(): NodeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNodeInfo(node: NodeEntity)

    @Query("UPDATE node_info SET isMeshSyncing = :syncing, connectedPeersCount = :peers WHERE nodeId = :nodeId")
    suspend fun updateNodeMeshStatus(nodeId: String, syncing: Boolean, peers: Int)
}
