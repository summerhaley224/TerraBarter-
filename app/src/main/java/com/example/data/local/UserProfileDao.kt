package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profiles ORDER BY updated_at DESC")
    fun getAllProfiles(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles WHERE id = :profileId LIMIT 1")
    fun observeProfileById(profileId: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE id = :profileId LIMIT 1")
    suspend fun getProfileById(profileId: String): UserProfile?

    @Query(
        """
        SELECT * FROM user_profiles
        WHERE display_name LIKE '%' || :query || '%'
           OR handle LIKE '%' || :query || '%'
           OR bio_region LIKE '%' || :query || '%'
           OR skills LIKE '%' || :query || '%'
           OR barter_items LIKE '%' || :query || '%'
           OR wishlist LIKE '%' || :query || '%'
        ORDER BY updated_at DESC
        """
    )
    fun searchProfiles(query: String): Flow<List<UserProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: UserProfile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfiles(profiles: List<UserProfile>)

    @Update
    suspend fun updateProfile(profile: UserProfile)

    @Delete
    suspend fun deleteProfile(profile: UserProfile)

    @Query("DELETE FROM user_profiles WHERE id = :profileId")
    suspend fun deleteProfileById(profileId: String)
}
