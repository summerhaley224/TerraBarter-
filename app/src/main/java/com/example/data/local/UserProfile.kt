package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.util.UUID

@JsonClass(generateAdapter = true)
data class BarterItem(
    val id: String = UUID.randomUUID().toString().take(8),
    val title: String,
    val category: String = "",
    val description: String = "",
    val preferredTrade: String = "Open to reciprocal barter",
    val isPureBarter: Boolean = true,
    val estimatedValue: String = "Pure Barter"
)

@JsonClass(generateAdapter = true)
data class WishlistItem(
    val id: String = UUID.randomUUID().toString().take(8),
    val title: String,
    val category: String = "",
    val urgency: String = "Flexible",
    val willExchangeFor: String = ""
)

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "handle")
    val handle: String,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "bio_region")
    val bioRegion: String = "",
    @ColumnInfo(name = "node_address")
    val nodeAddress: String = "",
    @ColumnInfo(name = "headline")
    val headline: String = "",
    @ColumnInfo(name = "bio")
    val bio: String = "",
    @ColumnInfo(name = "earth_pledge")
    val earthPledge: String = "",
    @ColumnInfo(name = "terms_and_conditions")
    val termsAndConditions: String = "",
    @ColumnInfo(name = "skills")
    val skills: List<String> = emptyList(),
    @ColumnInfo(name = "barter_items")
    val barterItems: List<BarterItem> = emptyList(),
    @ColumnInfo(name = "wishlist")
    val wishlist: List<WishlistItem> = emptyList(),
    @ColumnInfo(name = "rating_average")
    val ratingAverage: Float = 5.0f,
    @ColumnInfo(name = "trades_completed")
    val tradesCompleted: Int = 0,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

class UserProfileConverters {
    private val moshi: Moshi = Moshi.Builder().build()

    private val stringListType = Types.newParameterizedType(List::class.java, String::class.java)
    private val stringListAdapter = moshi.adapter<List<String>>(stringListType)

    private val barterItemListType = Types.newParameterizedType(List::class.java, BarterItem::class.java)
    private val barterItemListAdapter = moshi.adapter<List<BarterItem>>(barterItemListType)

    private val wishlistItemListType = Types.newParameterizedType(List::class.java, WishlistItem::class.java)
    private val wishlistItemListAdapter = moshi.adapter<List<WishlistItem>>(wishlistItemListType)

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return stringListAdapter.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        return stringListAdapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun fromBarterItems(value: List<BarterItem>): String {
        return barterItemListAdapter.toJson(value)
    }

    @TypeConverter
    fun toBarterItems(value: String): List<BarterItem> {
        if (value.isBlank()) return emptyList()
        return barterItemListAdapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun fromWishlist(value: List<WishlistItem>): String {
        return wishlistItemListAdapter.toJson(value)
    }

    @TypeConverter
    fun toWishlist(value: String): List<WishlistItem> {
        if (value.isBlank()) return emptyList()
        return wishlistItemListAdapter.fromJson(value) ?: emptyList()
    }
}
