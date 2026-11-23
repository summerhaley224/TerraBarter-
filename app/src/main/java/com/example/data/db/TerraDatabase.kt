package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.UserProfile
import com.example.data.local.UserProfileConverters
import com.example.data.local.UserProfileDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        SkillEntity::class,
        WishlistEntity::class,
        TradeEntity::class,
        ReferralEntity::class,
        ReviewEntity::class,
        NodeEntity::class,
        UserProfile::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(UserProfileConverters::class)
abstract class TerraDatabase : RoomDatabase() {
    abstract fun terraDao(): TerraDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: TerraDatabase? = null

        fun getDatabase(context: Context, coroutineScope: CoroutineScope): TerraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TerraDatabase::class.java,
                    "terra_barter_node.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate database asynchronously
                            coroutineScope.launch(Dispatchers.IO) {
                                val dao = getDatabase(context, coroutineScope).terraDao()
                                dao.insertUsers(SeedData.initialUsers)
                                dao.insertSkills(SeedData.initialSkills)
                                dao.insertWishlists(SeedData.initialWishlists)
                                dao.insertTrades(SeedData.initialTrades)
                                dao.insertReferrals(SeedData.initialReferrals)
                                dao.insertReviews(SeedData.initialReviews)
                                dao.insertNodeInfo(SeedData.initialNodeInfo)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
