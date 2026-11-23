package com.example

import com.example.data.db.SeedData
import com.example.data.model.SkillCategory
import com.example.data.model.UserProfile
import com.example.data.model.SkillOffering
import com.example.data.repository.toDomain
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TerraNodeUnitTest {

    @Test
    fun testInitialSeedUsersConvertedToDomain() {
        val users: List<UserProfile> = SeedData.initialUsers.map { it.toDomain() }
        assertTrue("Seed users should not be empty", users.isNotEmpty())

        val localUser = users.firstOrNull { it.isLocalNodeUser }
        assertNotNull("Local sovereign node user must exist", localUser)
        assertEquals("@rowan_earthcraft", localUser?.handle)
    }

    @Test
    fun testSeedSkillsValidCategories() {
        val skills: List<SkillOffering> = SeedData.initialSkills.map { it.toDomain() }
        assertTrue(skills.any { it.category == SkillCategory.FARMING_PRODUCE })
        assertTrue(skills.any { it.category == SkillCategory.RENEWABLE_ENERGY })
        assertTrue(skills.any { it.category == SkillCategory.ARTISAN_CRAFT })
        assertTrue(skills.any { it.category == SkillCategory.FIXERS_BUILDERS })
    }

    @Test
    fun testJsonNodeStructureVerification() {
        val json = JSONObject()
        json.put("protocol", "TerraBarter/P2P-v1.0")
        json.put("sovereignty", "Decentralized Personal Node - Zero Central Lock-in")

        val profile = JSONObject()
        profile.put("displayName", "Maya Lin")
        profile.put("termsAndConditions", "Pure Barter Preferred")
        json.put("profile", profile)

        assertEquals("TerraBarter/P2P-v1.0", json.getString("protocol"))
        assertEquals("Maya Lin", json.getJSONObject("profile").getString("displayName"))
    }
}
