package com.example.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.data.model.BarterTrade
import com.example.data.model.PeerReferral
import com.example.data.model.SkillCategory
import com.example.data.model.SkillOffering
import com.example.data.model.TradeStatus
import com.example.data.model.UserProfile
import com.example.data.model.WishlistItem
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MarketplaceListingScreen
import com.example.ui.screens.MarketplaceScreen
import com.example.ui.screens.MyNodeScreen
import com.example.ui.screens.ProfileDetailScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TerraNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val localUser = UserProfile(
        id = "user_local",
        handle = "@steward_local",
        displayName = "Rowan Meadow",
        nodeAddress = "terranode://local.8443",
        bioRegion = "Cascadia",
        headline = "Seed Keeper",
        bio = "Regenerative soil grower.",
        earthPledge = "Ask permission from the land.",
        termsAndConditions = "Direct barter preferred.",
        ratingAverage = 5.0f,
        reviewCount = 4,
        tradesCompleted = 4,
        isLocalNodeUser = true,
        avatarColorHex = 0xFF2E6B34,
        joinedYear = "2024"
    )

    private val peerUser = UserProfile(
        id = "user_peer",
        handle = "@maya_solar",
        displayName = "Maya Lin",
        nodeAddress = "terranode://maya.8443",
        bioRegion = "Willamette Valley",
        headline = "Solar Microgrid Fixer",
        bio = "Off-grid inverter repair.",
        earthPledge = "Clean energy without extraction.",
        termsAndConditions = "Swap for produce or tools.",
        ratingAverage = 4.9f,
        reviewCount = 6,
        tradesCompleted = 6,
        isLocalNodeUser = false,
        avatarColorHex = 0xFFBA5D2C,
        joinedYear = "2023"
    )

    private val sampleListing = SkillOffering(
        id = "skill_solar_1",
        userId = peerUser.id,
        authorName = peerUser.displayName,
        authorHandle = peerUser.handle,
        bioRegion = peerUser.bioRegion,
        title = "Off-Grid Solar Panel Diagnostics",
        category = SkillCategory.RENEWABLE_ENERGY,
        description = "Inspection and repair of 12V/24V solar charge setups.",
        tradePreference = "Heirloom produce or carpentry help",
        isPureBarter = true,
        estimatedValue = "Pure Barter",
        tags = listOf("Solar", "Repair")
    )

    private val sampleWish = WishlistItem(
        id = "wish_1",
        userId = peerUser.id,
        authorName = peerUser.displayName,
        authorHandle = peerUser.handle,
        title = "55-Gal Rainwater Barrel",
        category = SkillCategory.PLUMBING_WATER,
        urgency = "High Need",
        willExchangeFor = "2 hours of solar troubleshooting"
    )

    @Test
    fun navGraph_switchesBetweenHomeMarketplaceListingAndProfile() {
        lateinit var navController: NavHostController

        composeTestRule.setContent {
            MyApplicationTheme {
                navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = HomeRoute
                ) {
                    composable<HomeRoute> {
                        HomeScreen(
                            localProfile = localUser,
                            skills = listOf(sampleListing),
                            wishlists = listOf(sampleWish),
                            trades = listOf(
                                BarterTrade(
                                    id = "trade_1",
                                    initiatorId = localUser.id,
                                    initiatorName = localUser.displayName,
                                    receiverId = peerUser.id,
                                    receiverName = peerUser.displayName,
                                    offeredItemTitle = "Seed Crate",
                                    requestedItemTitle = "Solar Checkup",
                                    termsAgreed = "Mutual aid",
                                    status = TradeStatus.PROPOSED,
                                    timestamp = 0L
                                )
                            ),
                            referrals = emptyList<PeerReferral>(),
                            onNavigateToMarketplace = { navController.navigateToTopLevel(MarketplaceRoute) },
                            onNavigateToProfile = { navController.navigateToTopLevel(ProfileRoute(userId = null)) },
                            onNavigateToListing = { id -> navController.navigate(MarketplaceListingRoute(id)) },
                            onNavigateToCommunity = {},
                            onNavigateToTrades = {},
                            onNavigateToReferrals = {}
                        )
                    }

                    composable<MarketplaceRoute> {
                        MarketplaceScreen(
                            skills = listOf(sampleListing),
                            wishlists = listOf(sampleWish),
                            searchQuery = "",
                            selectedCategory = null,
                            onlyPureBarter = false,
                            onSearchChange = {},
                            onSelectCategory = {},
                            onTogglePureBarter = {},
                            onSelectSkill = { skill ->
                                navController.navigate(MarketplaceListingRoute(skill.id))
                            },
                            onProposeTrade = {},
                            onAddSkillClick = {},
                            onAddWishlistClick = {},
                            allUsers = listOf(localUser, peerUser)
                        )
                    }

                    composable<MarketplaceListingRoute> { entry ->
                        val route = entry.toRoute<MarketplaceListingRoute>()
                        val listing = if (route.offeringId == sampleListing.id) sampleListing else null
                        MarketplaceListingScreen(
                            listing = listing,
                            author = peerUser,
                            isOwnListing = false,
                            onBack = { navController.navigateUp() },
                            onViewAuthorProfile = { uid ->
                                navController.navigate(ProfileRoute(userId = uid))
                            },
                            onProposeTrade = {},
                            onReferAuthor = {},
                            onDeleteOwnListing = {}
                        )
                    }

                    composable<ProfileRoute> { entry ->
                        val route = entry.toRoute<ProfileRoute>()
                        if (route.userId == null || route.userId == localUser.id) {
                            MyNodeScreen(
                                localProfile = localUser,
                                nodeIdentity = null,
                                mySkills = emptyList(),
                                myWishlist = emptyList(),
                                onEditProfileClick = {},
                                onAddSkillClick = {},
                                onDeleteSkillClick = {},
                                onAddWishlistClick = {},
                                onDeleteWishlistClick = {},
                                onExportNodeClick = {},
                                onImportNodeClick = {},
                                onToggleMeshSync = {},
                                onSignOutClick = {}
                            )
                        } else {
                            ProfileDetailScreen(
                                profile = peerUser,
                                reviews = emptyList(),
                                onBack = { navController.navigateUp() },
                                onProposeTrade = {},
                                onReferUser = {}
                            )
                        }
                    }
                }
            }
        }

        // 1. Starts on Home screen
        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()

        // 2. Navigate Home -> My Profile
        composeTestRule.onNodeWithTag("home_to_profile_btn").performClick()
        composeTestRule.onNodeWithTag("node_status_card").assertIsDisplayed()

        // 3. Return to Home, then navigate Home -> Marketplace
        composeTestRule.runOnUiThread { navController.navigateToTopLevel(HomeRoute) }
        composeTestRule.onNodeWithTag("home_to_marketplace_btn").performClick()
        composeTestRule.onNodeWithTag("marketplace_search_input").assertIsDisplayed()

        // 4. Navigate Marketplace -> Marketplace Listing detail
        composeTestRule.onNodeWithTag("skill_card_skill_solar_1").performClick()
        composeTestRule.onNodeWithTag("marketplace_listing_detail").assertIsDisplayed()

        // 5. Navigate Listing -> Author Profile
        composeTestRule.onNodeWithTag("marketplace_listing_detail")
            .performScrollToNode(hasTestTag("listing_view_profile_btn"))
        composeTestRule.onNodeWithTag("listing_view_profile_btn").performClick()
        composeTestRule.onNodeWithTag("profile_propose_trade_btn").assertIsDisplayed()

        // 6. Navigate Back -> Listing -> Marketplace
        composeTestRule.onNodeWithTag("back_button").performClick()
        composeTestRule.onNodeWithTag("marketplace_listing_detail").assertIsDisplayed()
        composeTestRule.onNodeWithTag("listing_back_button").performClick()
        composeTestRule.onNodeWithTag("marketplace_search_input").assertIsDisplayed()
    }
}
