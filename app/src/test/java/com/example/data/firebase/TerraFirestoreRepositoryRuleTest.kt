package com.example.data.firebase

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.SkillCategory
import com.example.data.model.TradeStatus
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class TerraFirestoreRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun upsertUserProfile_authenticatedOwner_createsAndUpdatesProfile() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repository = FirestoreRepository(firestore, auth)

        val profile = CloudUserProfile(
            userId = aliceUid,
            handle = "@alice_meadow",
            displayName = "Alice Meadow",
            bioRegion = "Cascadia",
            nodeAddress = "terranode://${aliceUid.take(6)}.local:8443",
            headline = "Seed Keeper & Solar Tinkerer",
            bio = "Growing heirloom crops in living soil.",
            earthPledge = "Always ask the land before taking.",
            termsAndConditions = "Direct barter preferred.",
            skills = listOf("Organic Farming", "Solar Repair"),
            barterItems = listOf("Heirloom Tomatoes"),
            wishlist = listOf("Rain Barrel"),
            ratingAverage = 5.0,
            tradesCompleted = 2L
        )

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) { repository.upsertUserProfile(profile) }
        assertTrue("Expected upsertUserProfile create to succeed: ${saveResult.exceptionOrNull()}", saveResult.isSuccess)

        val fetched = withTimeout(DEFAULT_TIMEOUT_MS) { repository.getUserProfile(aliceUid).getOrThrow() }
        assertNotNull(fetched)
        assertEquals("Alice Meadow", fetched?.displayName)
        assertEquals(aliceUid, fetched?.id)

        val updateResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.upsertUserProfile(profile.copy(headline = "Permaculture Mentor"))
        }
        assertTrue("Expected upsertUserProfile update to succeed: ${updateResult.exceptionOrNull()}", updateResult.isSuccess)
    }

    @Test
    fun getUserProfile_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = FirestoreRepository(firestore, auth)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.upsertUserProfile(
                CloudUserProfile(
                    userId = aliceUid,
                    handle = "@alice",
                    displayName = "Alice",
                    skills = listOf("Gardening")
                )
            ).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = FirestoreRepository(firestore, auth)
        val result = withTimeout(DEFAULT_TIMEOUT_MS) { bobRepo.getUserProfile(aliceUid) }
        assertTrue(result.isFailure)
        val firestoreEx = result.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull("Expected FirebaseFirestoreException, got ${result.exceptionOrNull()}", firestoreEx)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreEx?.code)
    }

    @Test
    fun createAndObserveSkillOffering_authenticatedUser_emitsTypedObjectWithPropertyNames() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = FirestoreRepository(firestore, auth)
        val uniqueId = "skill_${UUID.randomUUID().toString().replace("-", "").take(10)}"

        val createResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createSkillOffering(
                title = "Organic Mulch & Compost",
                category = SkillCategory.EARTH_STEWARDSHIP,
                description = "Rich forest compost for neighbors.",
                tradePreference = "Open to jars or seeds",
                isPureBarter = true,
                estimatedValue = "Barter only",
                tags = listOf("Soil", "Compost"),
                authorName = "Alice Meadow",
                authorHandle = "@alice_meadow",
                bioRegion = "Cascadia",
                offeringId = uniqueId
            )
        }
        assertTrue("Expected createSkillOffering to succeed: ${createResult.exceptionOrNull()}", createResult.isSuccess)

        val offerings = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observePublicSkillOfferings().first { list -> list.any { it.id == uniqueId } }
        }
        val created = offerings.first { it.id == uniqueId }
        assertEquals("Organic Mulch & Compost", created.title)
        assertTrue(created.isPureBarter)
        assertTrue(created.isPublic)
    }

    @Test
    fun observePublicSkillOfferings_unauthenticatedCaller_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = FirestoreRepository(firestore, auth)

        try {
            withTimeout(FLOW_TIMEOUT_MS) { repository.observePublicSkillOfferings().first() }
            fail("Expected PERMISSION_DENIED for unauthenticated caller")
        } catch (e: Exception) {
            val firestoreEx = e.unwrapFirestoreException()
            assertNotNull("Expected nested FirebaseFirestoreException, got $e", firestoreEx)
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreEx?.code)
        }
    }

    @Test
    fun barterTradeLifecycle_participantsCanProgressAndReview_thenLocked() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = FirestoreRepository(firestore, auth)
        val bobUid = signInTestUser(BOB_EMAIL)

        // Sign back in as Alice to propose trade to Bob
        signInTestUser(ALICE_EMAIL)
        val tradeId = "trade_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        val proposeResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.proposeTrade(
                initiatorName = "Alice",
                receiverId = bobUid,
                receiverName = "Bob",
                offeredItemTitle = "Heirloom Seed Box",
                requestedItemTitle = "Solar Lantern Checkup",
                termsAgreed = "Direct handoff at co-op",
                tradeId = tradeId
            )
        }
        assertTrue("Expected proposeTrade to succeed: ${proposeResult.exceptionOrNull()}", proposeResult.isSuccess)

        // Bob accepts and Alice completes & reviews
        signInTestUser(BOB_EMAIL)
        val bobRepo = FirestoreRepository(firestore, auth)
        val acceptRes = withTimeout(DEFAULT_TIMEOUT_MS) { bobRepo.updateTradeStatus(tradeId, TradeStatus.ACCEPTED) }
        assertTrue("Expected ACCEPTED transition to succeed: ${acceptRes.exceptionOrNull()}", acceptRes.isSuccess)

        signInTestUser(ALICE_EMAIL)
        val completeRes = withTimeout(DEFAULT_TIMEOUT_MS) { aliceRepo.updateTradeStatus(tradeId, TradeStatus.COMPLETED) }
        assertTrue("Expected COMPLETED transition to succeed: ${completeRes.exceptionOrNull()}", completeRes.isSuccess)

        val reviewRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.submitTradeReview(
                tradeId = tradeId,
                ratingScore = 5,
                reviewComment = "Zero waste and super helpful!",
                stewardshipBadge = "Harm-Free Earth Trade"
            )
        }
        assertTrue("Expected submitTradeReview to succeed: ${reviewRes.exceptionOrNull()}", reviewRes.isSuccess)

        val bobTrades = withTimeout(FLOW_TIMEOUT_MS) {
            aliceRepo.observeUserTrades(aliceUid).first { list -> list.any { it.id == tradeId && it.status == TradeStatus.REVIEWED.name } }
        }
        assertEquals(5, bobTrades.first { it.id == tradeId }.ratingScore)

        // Terminal state lock: subsequent transition must fail with PERMISSION_DENIED
        val lockedRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.updateTradeStatus(tradeId, TradeStatus.COMPLETED)
        }
        assertTrue(lockedRes.isFailure)
        assertEquals(
            FirebaseFirestoreException.Code.PERMISSION_DENIED,
            lockedRes.exceptionOrNull()?.unwrapFirestoreException()?.code
        )
    }

    private companion object {
        const val ALICE_EMAIL = "alice_terra@test.com"
        const val BOB_EMAIL = "bob_terra@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
