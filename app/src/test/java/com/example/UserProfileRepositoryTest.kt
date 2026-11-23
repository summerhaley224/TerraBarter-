package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.TerraDatabase
import com.example.data.local.BarterItem
import com.example.data.local.UserProfile
import com.example.data.local.WishlistItem
import com.example.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class UserProfileRepositoryTest {

    private lateinit var database: TerraDatabase
    private lateinit var repository: UserProfileRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TerraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = UserProfileRepository(database.userProfileDao())
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun saveAndRetrieveUserProfileWithSkillsBarterItemsAndWishlist() = runTest {
        val profile = UserProfile(
            id = "node_maya_01",
            handle = "@solargrower_maya",
            displayName = "Maya Lin",
            bioRegion = "Cascadia Bio-Region",
            nodeAddress = "node://terra.p2p.maya.3a91c",
            headline = "Heirloom Seed Keeper & Permaculture Grower",
            bio = "Cultivating a no-till perennial food forest.",
            earthPledge = "No synthetic pesticides; listen to soil rhythms.",
            termsAndConditions = "Direct barter preferred.",
            skills = listOf("Heirloom Seed Saving", "No-Till Permaculture", "Compost Tea Brewing"),
            barterItems = listOf(
                BarterItem(
                    id = "off_1",
                    title = "Weekly Heirloom Harvest Crate",
                    category = "Produce",
                    description = "Seasonal squash, greens, and culinary herbs",
                    preferredTrade = "Split firewood or tool sharpening",
                    isPureBarter = true,
                    estimatedValue = "Pure Barter"
                )
            ),
            wishlist = listOf(
                WishlistItem(
                    id = "wish_1",
                    title = "55-Gal Food Grade Barrels",
                    category = "Water",
                    urgency = "High Need",
                    willExchangeFor = "2 Crates of Heirloom Produce"
                )
            )
        )

        repository.saveProfile(profile)

        val loaded = repository.getProfile("node_maya_01")
        assertNotNull(loaded)
        assertEquals("Maya Lin", loaded?.displayName)
        assertEquals(3, loaded?.skills?.size)
        assertEquals("Weekly Heirloom Harvest Crate", loaded?.barterItems?.first()?.title)
        assertEquals("55-Gal Food Grade Barrels", loaded?.wishlist?.first()?.title)
    }

    @Test
    fun addAndRemoveSkillsBarterItemsAndWishlistItems() = runTest {
        val initial = UserProfile(
            id = "node_rowan_01",
            handle = "@rowan_earthcraft",
            displayName = "Rowan Meadow",
            skills = listOf("Cedar Carpentry")
        )
        repository.saveProfile(initial)

        repository.addSkill("node_rowan_01", "Tool Handle Repair")
        repository.addSkill("node_rowan_01", "cedar carpentry")
        repository.addBarterItem(
            "node_rowan_01",
            BarterItem(id = "off_cedar", title = "Raised Cedar Planter Bed")
        )
        repository.addWishlistItem(
            "node_rowan_01",
            WishlistItem(id = "wish_worms", title = "Red Wiggler Composting Worms", urgency = "High Need")
        )

        val updated = repository.observeProfile("node_rowan_01").first()
        assertNotNull(updated)
        assertEquals(listOf("Cedar Carpentry", "Tool Handle Repair"), updated?.skills)
        assertEquals(1, updated?.barterItems?.size)
        assertEquals(1, updated?.wishlist?.size)

        repository.removeSkill("node_rowan_01", "Cedar Carpentry")
        repository.removeBarterItem("node_rowan_01", "off_cedar")
        repository.removeWishlistItem("node_rowan_01", "wish_worms")

        val afterRemoval = repository.getProfile("node_rowan_01")
        assertEquals(listOf("Tool Handle Repair"), afterRemoval?.skills)
        assertTrue(afterRemoval?.barterItems?.isEmpty() == true)
        assertTrue(afterRemoval?.wishlist?.isEmpty() == true)
    }

    @Test
    fun searchAndDeleteProfiles() = runTest {
        repository.saveProfiles(
            listOf(
                UserProfile(
                    id = "p1",
                    handle = "@renewable_jasper",
                    displayName = "Jasper Vance",
                    skills = listOf("Off-Grid Solar Wiring")
                ),
                UserProfile(
                    id = "p2",
                    handle = "@herbal_elena",
                    displayName = "Elena Solis",
                    skills = listOf("Cold-Process Soap Making")
                )
            )
        )

        val solarMatches = repository.searchProfiles("Solar").first()
        assertEquals(1, solarMatches.size)
        assertEquals("Jasper Vance", solarMatches.first().displayName)

        repository.deleteProfileById("p1")
        assertNull(repository.getProfile("p1"))
        assertEquals(1, repository.allProfiles.first().size)
    }
}
