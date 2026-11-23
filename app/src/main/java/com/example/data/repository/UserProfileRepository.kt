package com.example.data.repository

import com.example.data.local.BarterItem
import com.example.data.local.UserProfile
import com.example.data.local.UserProfileDao
import com.example.data.local.WishlistItem
import kotlinx.coroutines.flow.Flow

class UserProfileRepository(private val userProfileDao: UserProfileDao) {

    val allProfiles: Flow<List<UserProfile>> = userProfileDao.getAllProfiles()

    fun observeProfile(profileId: String): Flow<UserProfile?> =
        userProfileDao.observeProfileById(profileId)

    fun searchProfiles(query: String): Flow<List<UserProfile>> =
        userProfileDao.searchProfiles(query)

    suspend fun getProfile(profileId: String): UserProfile? =
        userProfileDao.getProfileById(profileId)

    suspend fun saveProfile(profile: UserProfile) {
        userProfileDao.upsertProfile(profile.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun saveProfiles(profiles: List<UserProfile>) {
        val now = System.currentTimeMillis()
        userProfileDao.upsertProfiles(profiles.map { it.copy(updatedAt = now) })
    }

    suspend fun updateProfile(profile: UserProfile) {
        userProfileDao.updateProfile(profile.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteProfile(profile: UserProfile) {
        userProfileDao.deleteProfile(profile)
    }

    suspend fun deleteProfileById(profileId: String) {
        userProfileDao.deleteProfileById(profileId)
    }

    suspend fun addSkill(profileId: String, skill: String) {
        val trimmed = skill.trim()
        if (trimmed.isEmpty()) return
        val existing = userProfileDao.getProfileById(profileId) ?: return
        if (existing.skills.any { it.equals(trimmed, ignoreCase = true) }) return
        userProfileDao.upsertProfile(
            existing.copy(
                skills = existing.skills + trimmed,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeSkill(profileId: String, skill: String) {
        val existing = userProfileDao.getProfileById(profileId) ?: return
        userProfileDao.upsertProfile(
            existing.copy(
                skills = existing.skills.filterNot { it == skill },
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun addBarterItem(profileId: String, item: BarterItem) {
        val existing = userProfileDao.getProfileById(profileId) ?: return
        val updatedList = existing.barterItems.filterNot { it.id == item.id } + item
        userProfileDao.upsertProfile(
            existing.copy(
                barterItems = updatedList,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeBarterItem(profileId: String, itemId: String) {
        val existing = userProfileDao.getProfileById(profileId) ?: return
        userProfileDao.upsertProfile(
            existing.copy(
                barterItems = existing.barterItems.filterNot { it.id == itemId },
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun addWishlistItem(profileId: String, item: WishlistItem) {
        val existing = userProfileDao.getProfileById(profileId) ?: return
        val updatedList = existing.wishlist.filterNot { it.id == item.id } + item
        userProfileDao.upsertProfile(
            existing.copy(
                wishlist = updatedList,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeWishlistItem(profileId: String, itemId: String) {
        val existing = userProfileDao.getProfileById(profileId) ?: return
        userProfileDao.upsertProfile(
            existing.copy(
                wishlist = existing.wishlist.filterNot { it.id == itemId },
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}
