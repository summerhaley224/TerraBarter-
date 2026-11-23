package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.db.TerraDatabase
import com.example.data.firebase.FirestoreRepository
import com.example.data.repository.TerraRepository
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.signOut
import com.example.ui.components.*
import com.example.ui.navigation.ProfileRoute
import com.example.ui.navigation.TerraNavHost
import com.example.ui.navigation.TopLevelDestination
import com.example.ui.navigation.isDetailScreen
import com.example.ui.navigation.isTopLevelRoute
import com.example.ui.navigation.navigateToTopLevel
import com.example.ui.screens.*
import com.example.ui.theme.ForestDeep
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TerraViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }
    }
}

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser
    if (user == null) {
        AuthScreen(
            onAuthSuccess = { /* authStateFlow updates automatically */ }
        )
    } else {
        key(user.uid) {
            TerraApp(
                currentUserId = user.uid,
                userDisplayName = user.displayName ?: "Community Steward",
                userHandle = "@${(user.email?.substringBefore("@") ?: "steward_${user.uid.take(5)}").replace(Regex("[^a-zA-Z0-9_]"), "_")}"
            )
        }
    }
}

@Composable
fun TerraApp(
    currentUserId: String,
    userDisplayName: String = "Community Steward",
    userHandle: String = "@steward_${currentUserId.take(6)}",
    viewModel: TerraViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                val firestoreRepo = FirestoreRepository(db)
                val roomDb = TerraDatabase.getDatabase(app, CoroutineScope(Dispatchers.IO))
                val localRepo = TerraRepository(roomDb.terraDao())
                TerraViewModel(
                    repository = localRepo,
                    firestoreRepository = firestoreRepo,
                    currentUserId = currentUserId,
                    initialDisplayName = userDisplayName,
                    initialHandle = userHandle
                )
            }
        }
    )
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isDetailDestination = navBackStackEntry.isDetailScreen(currentUserId)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val localUser by viewModel.localUser.collectAsStateWithLifecycle()
    val nodeIdentity by viewModel.nodeIdentity.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (useNavigationRail && !isDetailDestination) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("side_nav_rail")
                ) {
                    Spacer(Modifier.height(12.dp))
                    TopLevelDestination.entries.forEach { destination ->
                        val selected = navBackStackEntry.isTopLevelRoute(destination, currentUserId)
                        NavigationRailItem(
                            selected = selected,
                            onClick = { navController.navigateToTopLevel(destination.route) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = destination.label
                                )
                            },
                            label = { Text(destination.label) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = ForestDeep,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag(destination.testTag)
                        )
                    }
                }
            }

            Scaffold(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    if (!isDetailDestination) {
                        TerraTopAppBar(
                            meshActive = nodeIdentity?.isMeshSyncing == true,
                            connectedPeers = nodeIdentity?.connectedPeersCount ?: 8,
                            onNodeBadgeClick = {
                                navController.navigateToTopLevel(ProfileRoute(userId = null))
                            }
                        )
                    }
                },
                bottomBar = {
                    if (!useNavigationRail && !isDetailDestination) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 4.dp,
                            modifier = Modifier.testTag("bottom_nav_bar")
                        ) {
                            TopLevelDestination.entries.forEach { destination ->
                                val selected = navBackStackEntry.isTopLevelRoute(destination, currentUserId)
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navController.navigateToTopLevel(destination.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                            contentDescription = destination.label
                                        )
                                    },
                                    label = { Text(destination.label) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ForestDeep,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.testTag(destination.testTag)
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                TerraNavHost(
                    navController = navController,
                    viewModel = viewModel,
                    currentUserId = currentUserId,
                    onSignOutClick = {
                        signOut(
                            credentialManager = credentialManager,
                            scope = coroutineScope
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }

    // Propose Trade Dialog
    if (uiState.isProposingTrade && uiState.tradeTargetUser != null) {
        ProposeTradeDialog(
            targetUser = uiState.tradeTargetUser!!,
            onDismiss = { viewModel.cancelTradeProposal() },
            onSubmit = { offered, requested, terms ->
                viewModel.submitTradeProposal(offered, requested, terms)
            }
        )
    }

    // Rate Trade Dialog
    if (uiState.isRatingTrade && uiState.tradeToRate != null) {
        RateTradeDialog(
            trade = uiState.tradeToRate!!,
            onDismiss = { viewModel.cancelRatingTrade() },
            onSubmit = { rating, comment, badge ->
                viewModel.submitTradeRating(rating, comment, badge)
            }
        )
    }

    // Create Referral Dialog
    if (uiState.isCreatingReferral) {
        CreateReferralDialog(
            allUsers = allUsers,
            preselectedUser = uiState.referralTargetUser,
            onDismiss = { viewModel.cancelReferral() },
            onSubmit = { targetId, targetName, seekerId, seekerName, focus, note ->
                viewModel.submitReferral(targetId, targetName, seekerId, seekerName, focus, note)
            }
        )
    }

    // Edit Profile Dialog
    if (uiState.isEditingProfile && localUser != null) {
        EditProfileDialog(
            currentProfile = localUser!!,
            onDismiss = { viewModel.closeEditProfile() },
            onSave = { displayName, bioRegion, headline, bio, earthPledge, terms ->
                viewModel.saveLocalProfile(displayName, bioRegion, headline, bio, earthPledge, terms)
            }
        )
    }

    // Add Skill Dialog
    if (uiState.isAddingSkill) {
        AddSkillDialog(
            onDismiss = { viewModel.closeAddSkill() },
            onAdd = { title, cat, desc, tradePref, isPureBarter, estVal, tags ->
                viewModel.createSkillOffering(title, cat, desc, tradePref, isPureBarter, estVal, tags)
            }
        )
    }

    // Add Wishlist Dialog
    if (uiState.isAddingWishlist) {
        AddWishlistDialog(
            onDismiss = { viewModel.closeAddWishlist() },
            onAdd = { title, cat, urgency, willExchangeFor ->
                viewModel.createWishlistItem(title, cat, urgency, willExchangeFor)
            }
        )
    }

    // Export Node Dialog
    if (uiState.isExportingNode && uiState.exportedJson != null) {
        ExportNodeDialog(
            jsonContent = uiState.exportedJson!!,
            onDismiss = { viewModel.closeExportNode() }
        )
    }

    // Import Node Dialog
    if (uiState.isImportingNode) {
        ImportNodeDialog(
            onDismiss = { viewModel.closeImportNode() },
            onImport = { json -> viewModel.performImportNode(json) }
        )
    }
}
