package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Diversity3
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MarketplaceListingScreen
import com.example.ui.screens.MarketplaceScreen
import com.example.ui.screens.MyNodeScreen
import com.example.ui.screens.ProfileDetailScreen
import com.example.ui.screens.ReferralsScreen
import com.example.ui.screens.TradesScreen
import com.example.ui.viewmodel.TerraViewModel
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object MarketplaceRoute

@Serializable
data class MarketplaceListingRoute(val offeringId: String)

@Serializable
data class ProfileRoute(val userId: String? = null)

@Serializable
data object CommunityRoute

@Serializable
data object TradesRoute

@Serializable
data object ReferralsRoute

enum class TopLevelDestination(
    val route: Any,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME(
        route = HomeRoute,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_home"
    ),
    MARKETPLACE(
        route = MarketplaceRoute,
        label = "Market",
        selectedIcon = Icons.Filled.Storefront,
        unselectedIcon = Icons.Outlined.Storefront,
        testTag = "nav_market"
    ),
    COMMUNITY(
        route = CommunityRoute,
        label = "Community",
        selectedIcon = Icons.Filled.Diversity3,
        unselectedIcon = Icons.Outlined.Diversity3,
        testTag = "nav_community"
    ),
    TRADES(
        route = TradesRoute,
        label = "Trades",
        selectedIcon = Icons.Filled.Handshake,
        unselectedIcon = Icons.Outlined.Handshake,
        testTag = "nav_trades"
    ),
    PROFILE(
        route = ProfileRoute(userId = null),
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "nav_profile"
    )
}

fun NavBackStackEntry?.isDetailScreen(currentUserId: String): Boolean {
    val dest = this?.destination ?: return false
    if (dest.hasRoute<MarketplaceListingRoute>()) return true
    if (dest.hasRoute<ProfileRoute>()) {
        val route = toRoute<ProfileRoute>()
        return route.userId != null && route.userId != currentUserId
    }
    return false
}

fun NavBackStackEntry?.isTopLevelRoute(
    destination: TopLevelDestination,
    currentUserId: String
): Boolean {
    val dest = this?.destination ?: return false
    return when (destination) {
        TopLevelDestination.HOME -> dest.hasRoute<HomeRoute>()
        TopLevelDestination.MARKETPLACE -> dest.hasRoute<MarketplaceRoute>()
        TopLevelDestination.COMMUNITY -> dest.hasRoute<CommunityRoute>()
        TopLevelDestination.TRADES -> dest.hasRoute<TradesRoute>()
        TopLevelDestination.PROFILE -> dest.hasRoute<ProfileRoute>() && !isDetailScreen(currentUserId)
    }
}

fun NavHostController.navigateToTopLevel(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun TerraNavHost(
    navController: NavHostController,
    viewModel: TerraViewModel,
    currentUserId: String,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val localUser by viewModel.localUser.collectAsStateWithLifecycle()
    val allSkills by viewModel.allSkills.collectAsStateWithLifecycle()
    val allWishlists by viewModel.allWishlists.collectAsStateWithLifecycle()
    val allTrades by viewModel.allTrades.collectAsStateWithLifecycle()
    val allReferrals by viewModel.allReferrals.collectAsStateWithLifecycle()
    val nodeIdentity by viewModel.nodeIdentity.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            HomeScreen(
                localProfile = localUser,
                skills = allSkills,
                wishlists = allWishlists,
                trades = allTrades,
                referrals = allReferrals,
                onNavigateToMarketplace = { navController.navigateToTopLevel(MarketplaceRoute) },
                onNavigateToProfile = { navController.navigateToTopLevel(ProfileRoute(userId = null)) },
                onNavigateToListing = { offeringId ->
                    navController.navigate(MarketplaceListingRoute(offeringId))
                },
                onNavigateToCommunity = { navController.navigateToTopLevel(CommunityRoute) },
                onNavigateToTrades = { navController.navigateToTopLevel(TradesRoute) },
                onNavigateToReferrals = {
                    navController.navigate(ReferralsRoute) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<MarketplaceRoute> {
            MarketplaceScreen(
                skills = allSkills,
                wishlists = allWishlists,
                searchQuery = uiState.searchQuery,
                selectedCategory = uiState.selectedCategory,
                onlyPureBarter = uiState.onlyPureBarter,
                onSearchChange = { viewModel.setSearchQuery(it) },
                onSelectCategory = { viewModel.selectCategory(it) },
                onTogglePureBarter = { viewModel.togglePureBarter(it) },
                onSelectSkill = { skill ->
                    navController.navigate(MarketplaceListingRoute(skill.id))
                },
                onProposeTrade = { targetUser ->
                    viewModel.startTradeProposal(targetUser)
                },
                onAddSkillClick = { viewModel.openAddSkill() },
                onAddWishlistClick = { viewModel.openAddWishlist() },
                allUsers = allUsers
            )
        }

        composable<MarketplaceListingRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<MarketplaceListingRoute>()
            val listing = allSkills.firstOrNull { it.id == route.offeringId }
            val author = listing?.let { item ->
                allUsers.firstOrNull { it.id == item.userId }
            }
            val isOwnListing = listing?.userId == currentUserId || listing?.userId == localUser?.id

            MarketplaceListingScreen(
                listing = listing,
                author = author,
                isOwnListing = isOwnListing,
                onBack = { navController.navigateUp() },
                onViewAuthorProfile = { authorId ->
                    if (authorId == currentUserId || authorId == localUser?.id) {
                        navController.navigateToTopLevel(ProfileRoute(userId = null))
                    } else {
                        navController.navigate(ProfileRoute(userId = authorId))
                    }
                },
                onProposeTrade = { targetUser ->
                    viewModel.startTradeProposal(targetUser)
                },
                onReferAuthor = { targetUser ->
                    viewModel.startReferral(targetUser)
                },
                onDeleteOwnListing = { offeringId ->
                    viewModel.deleteSkill(offeringId)
                    navController.navigateUp()
                }
            )
        }

        composable<ProfileRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ProfileRoute>()
            val targetId = route.userId
            val isSelf = targetId == null || targetId == currentUserId || targetId == localUser?.id

            if (isSelf) {
                val mySkills = allSkills.filter { it.userId == currentUserId || it.userId == localUser?.id }
                val myWishlist = allWishlists.filter { it.userId == currentUserId || it.userId == localUser?.id }

                MyNodeScreen(
                    localProfile = localUser,
                    nodeIdentity = nodeIdentity,
                    mySkills = mySkills,
                    myWishlist = myWishlist,
                    onEditProfileClick = { viewModel.openEditProfile() },
                    onAddSkillClick = { viewModel.openAddSkill() },
                    onDeleteSkillClick = { viewModel.deleteSkill(it) },
                    onAddWishlistClick = { viewModel.openAddWishlist() },
                    onDeleteWishlistClick = { viewModel.deleteWishlistItem(it) },
                    onExportNodeClick = { viewModel.openExportNode() },
                    onImportNodeClick = { viewModel.openImportNode() },
                    onToggleMeshSync = { viewModel.toggleMeshSync(it) },
                    onSignOutClick = onSignOutClick
                )
            } else {
                val peerBase = allUsers.firstOrNull { it.id == targetId }
                val peerReviews by viewModel.observeReviewsForUser(targetId!!)
                    .collectAsStateWithLifecycle(initialValue = emptyList())

                if (peerBase != null) {
                    val enrichedPeer = peerBase.copy(
                        skills = allSkills.filter { it.userId == peerBase.id },
                        wishlist = allWishlists.filter { it.userId == peerBase.id }
                    )
                    ProfileDetailScreen(
                        profile = enrichedPeer,
                        reviews = peerReviews,
                        onBack = { navController.navigateUp() },
                        onProposeTrade = { viewModel.startTradeProposal(enrichedPeer) },
                        onReferUser = { viewModel.startReferral(enrichedPeer) }
                    )
                } else {
                    MyNodeScreen(
                        localProfile = localUser,
                        nodeIdentity = nodeIdentity,
                        mySkills = allSkills.filter { it.userId == currentUserId || it.userId == localUser?.id },
                        myWishlist = allWishlists.filter { it.userId == currentUserId || it.userId == localUser?.id },
                        onEditProfileClick = { viewModel.openEditProfile() },
                        onAddSkillClick = { viewModel.openAddSkill() },
                        onDeleteSkillClick = { viewModel.deleteSkill(it) },
                        onAddWishlistClick = { viewModel.openAddWishlist() },
                        onDeleteWishlistClick = { viewModel.deleteWishlistItem(it) },
                        onExportNodeClick = { viewModel.openExportNode() },
                        onImportNodeClick = { viewModel.openImportNode() },
                        onToggleMeshSync = { viewModel.toggleMeshSync(it) },
                        onSignOutClick = onSignOutClick
                    )
                }
            }
        }

        composable<CommunityRoute> {
            CommunityScreen(
                users = allUsers,
                onSelectUser = { selected ->
                    if (selected.isLocalNodeUser || selected.id == currentUserId) {
                        navController.navigateToTopLevel(ProfileRoute(userId = null))
                    } else {
                        navController.navigate(ProfileRoute(userId = selected.id))
                    }
                },
                onProposeTrade = { viewModel.startTradeProposal(it) },
                onReferUser = { viewModel.startReferral(it) }
            )
        }

        composable<TradesRoute> {
            TradesScreen(
                trades = allTrades,
                onAcceptTrade = { viewModel.acceptTrade(it) },
                onCompleteTrade = { viewModel.completeTrade(it) },
                onRateTrade = { viewModel.startRatingTrade(it) }
            )
        }

        composable<ReferralsRoute> {
            ReferralsScreen(
                referrals = allReferrals,
                onNewReferralClick = { viewModel.startReferral(null) },
                onToggleContacted = { id, contacted -> viewModel.markReferralContacted(id, contacted) }
            )
        }
    }
}
