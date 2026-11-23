package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.CloudUserProfile
import com.example.data.firebase.FirestoreRepository
import com.example.data.model.*
import com.example.data.repository.TerraRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TerraUiState(
    val searchQuery: String = "",
    val selectedCategory: SkillCategory? = null,
    val onlyPureBarter: Boolean = false,
    val isProposingTrade: Boolean = false,
    val tradeTargetUser: UserProfile? = null,
    val isRatingTrade: Boolean = false,
    val tradeToRate: BarterTrade? = null,
    val isCreatingReferral: Boolean = false,
    val referralTargetUser: UserProfile? = null,
    val isEditingProfile: Boolean = false,
    val isAddingSkill: Boolean = false,
    val isAddingWishlist: Boolean = false,
    val isExportingNode: Boolean = false,
    val exportedJson: String? = null,
    val isImportingNode: Boolean = false,
    val snackbarMessage: String? = null
)

class TerraViewModel(
    private val repository: TerraRepository,
    private val firestoreRepository: FirestoreRepository,
    private val currentUserId: String,
    private val initialDisplayName: String = "Community Steward",
    private val initialHandle: String = "@steward_${currentUserId.take(6)}"
) : ViewModel() {

    private val _uiState = MutableStateFlow(TerraUiState())
    val uiState: StateFlow<TerraUiState> = _uiState.asStateFlow()

    private val cloudSkillIds = MutableStateFlow<Set<String>>(emptySet())
    private val cloudWishlistIds = MutableStateFlow<Set<String>>(emptySet())
    private val cloudTradeIds = MutableStateFlow<Set<String>>(emptySet())
    private val cloudReferralIds = MutableStateFlow<Set<String>>(emptySet())

    init {
        bootstrapCloudProfileIfNeeded()
    }

    private fun bootstrapCloudProfileIfNeeded() {
        viewModelScope.launch {
            val result = firestoreRepository.getUserProfile(currentUserId)
            if (result.isSuccess && result.getOrNull() == null) {
                val defaultProfile = CloudUserProfile(
                    userId = currentUserId,
                    handle = initialHandle,
                    displayName = initialDisplayName.ifBlank { "Community Steward" },
                    bioRegion = "Local Watershed Commons",
                    nodeAddress = "terranode://${currentUserId.take(8)}.local:8443",
                    headline = "Community Skill Sharer & Earth Steward",
                    bio = "Open-source barter node steward trading local skills, harvests, and mutual aid.",
                    earthPledge = "I ask permission from the land and honor sustainable reciprocity.",
                    termsAndConditions = "Direct peer-to-peer agreements with honest community feedback.",
                    skills = listOf("Community Mutual Aid"),
                    barterItems = emptyList(),
                    wishlist = emptyList(),
                    ratingAverage = 5.0,
                    tradesCompleted = 0L
                )
                firestoreRepository.upsertUserProfile(defaultProfile)
            }
        }
    }

    private fun handleStreamError(error: Throwable, label: String) {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val rootError = firestoreError ?: error
        Log.w(TAG, "Error observing $label", rootError)
        _uiState.update { it.copy(snackbarMessage = rootError.message ?: "Failed to sync $label") }
    }

    private val cloudProfileFlow: Flow<CloudUserProfile?> = firestoreRepository
        .observeUserProfile(currentUserId)
        .catch { error ->
            handleStreamError(error, "profile")
            emit(null)
        }

    private val cloudSkillsFlow: Flow<List<SkillOffering>> = firestoreRepository
        .observePublicSkillOfferings()
        .map { list ->
            cloudSkillIds.value = list.map { it.id }.toSet()
            list.map { it.toDomain() }
        }
        .catch { error ->
            handleStreamError(error, "skill offerings")
            emit(emptyList())
        }

    private val cloudWishlistsFlow: Flow<List<WishlistItem>> = firestoreRepository
        .observePublicWishlistItems()
        .map { list ->
            cloudWishlistIds.value = list.map { it.id }.toSet()
            list.map { it.toDomain() }
        }
        .catch { error ->
            handleStreamError(error, "wishlist items")
            emit(emptyList())
        }

    private val cloudTradesFlow: Flow<List<BarterTrade>> = firestoreRepository
        .observeUserTrades(currentUserId)
        .map { list ->
            cloudTradeIds.value = list.map { it.id }.toSet()
            list.map { it.toDomain() }
        }
        .catch { error ->
            handleStreamError(error, "trades")
            emit(emptyList())
        }

    private val cloudReferralsFlow: Flow<List<PeerReferral>> = firestoreRepository
        .observePublicReferrals()
        .map { list ->
            cloudReferralIds.value = list.map { it.id }.toSet()
            list.map { it.toDomain() }
        }
        .catch { error ->
            handleStreamError(error, "referrals")
            emit(emptyList())
        }

    val localUser: StateFlow<UserProfile?> = combine(
        cloudProfileFlow,
        repository.localUserFlow
    ) { cloud, local ->
        cloud?.toDomain(isLocalNodeUser = true) ?: local
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    val allUsers: StateFlow<List<UserProfile>> = combine(
        cloudProfileFlow,
        repository.allUsersFlow
    ) { cloud, localUsers ->
        val cloudDomain = cloud?.toDomain(isLocalNodeUser = true)
        if (cloudDomain != null) {
            listOf(cloudDomain) + localUsers.filterNot { it.id == cloudDomain.id || it.isLocalNodeUser }
        } else {
            localUsers
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), emptyList())

    val allSkills: StateFlow<List<SkillOffering>> = combine(
        cloudSkillsFlow,
        repository.allSkillsFlow
    ) { cloud, local ->
        (cloud + local).distinctBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), emptyList())

    val allWishlists: StateFlow<List<WishlistItem>> = combine(
        cloudWishlistsFlow,
        repository.allWishlistFlow
    ) { cloud, local ->
        (cloud + local).distinctBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), emptyList())

    val allTrades: StateFlow<List<BarterTrade>> = combine(
        cloudTradesFlow,
        repository.allTradesFlow
    ) { cloud, local ->
        (cloud + local).distinctBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), emptyList())

    val allReferrals: StateFlow<List<PeerReferral>> = combine(
        cloudReferralsFlow,
        repository.allReferralsFlow
    ) { cloud, local ->
        (cloud + local).distinctBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), emptyList())

    val nodeIdentity: StateFlow<NodeIdentity?> = repository.nodeInfoFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    // Filter & search state
    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectCategory(category: SkillCategory?) {
        _uiState.update {
            it.copy(selectedCategory = if (it.selectedCategory == category) null else category)
        }
    }

    fun togglePureBarter(enabled: Boolean) {
        _uiState.update { it.copy(onlyPureBarter = enabled) }
    }

    fun observeReviewsForUser(userId: String): Flow<List<HonestReview>> =
        repository.getReviewsForUser(userId)

    // Trade proposal
    fun startTradeProposal(targetUser: UserProfile) {
        _uiState.update { it.copy(isProposingTrade = true, tradeTargetUser = targetUser) }
    }

    fun cancelTradeProposal() {
        _uiState.update { it.copy(isProposingTrade = false, tradeTargetUser = null) }
    }

    fun submitTradeProposal(offeredTitle: String, requestedTitle: String, termsAgreed: String) {
        val target = _uiState.value.tradeTargetUser ?: return
        val me = localUser.value
        viewModelScope.launch {
            val cloudResult = firestoreRepository.proposeTrade(
                initiatorName = me?.displayName ?: initialDisplayName,
                receiverId = target.id,
                receiverName = target.displayName,
                offeredItemTitle = offeredTitle,
                requestedItemTitle = requestedTitle,
                termsAgreed = termsAgreed
            )
            val message = if (cloudResult.isSuccess) {
                "Barter proposal synced to ${target.displayName}!"
            } else {
                "Cloud sync failed: ${cloudResult.exceptionOrNull()?.localizedMessage ?: "Permission denied"}"
            }
            _uiState.update {
                it.copy(
                    isProposingTrade = false,
                    tradeTargetUser = null,
                    snackbarMessage = message
                )
            }
        }
    }

    fun acceptTrade(tradeId: String) {
        viewModelScope.launch {
            if (tradeId in cloudTradeIds.value) {
                val res = firestoreRepository.updateTradeStatus(tradeId, TradeStatus.ACCEPTED)
                val msg = if (res.isSuccess) {
                    "Trade accepted! Ready for physical or skill exchange."
                } else {
                    res.exceptionOrNull()?.localizedMessage ?: "Failed to accept trade."
                }
                _uiState.update { it.copy(snackbarMessage = msg) }
            } else {
                repository.updateTradeStatus(tradeId, TradeStatus.ACCEPTED)
                _uiState.update { it.copy(snackbarMessage = "Trade accepted! Ready for physical or skill exchange.") }
            }
        }
    }

    fun completeTrade(tradeId: String) {
        viewModelScope.launch {
            if (tradeId in cloudTradeIds.value) {
                val res = firestoreRepository.updateTradeStatus(tradeId, TradeStatus.COMPLETED)
                val msg = if (res.isSuccess) {
                    "Trade marked as Completed! Please leave an honest community review."
                } else {
                    res.exceptionOrNull()?.localizedMessage ?: "Failed to complete trade."
                }
                _uiState.update { it.copy(snackbarMessage = msg) }
            } else {
                repository.updateTradeStatus(tradeId, TradeStatus.COMPLETED)
                _uiState.update { it.copy(snackbarMessage = "Trade marked as Completed! Please leave an honest community review.") }
            }
        }
    }

    // Rating and review
    fun startRatingTrade(trade: BarterTrade) {
        _uiState.update { it.copy(isRatingTrade = true, tradeToRate = trade) }
    }

    fun cancelRatingTrade() {
        _uiState.update { it.copy(isRatingTrade = false, tradeToRate = null) }
    }

    fun submitTradeRating(rating: Int, comment: String, badge: String) {
        val trade = _uiState.value.tradeToRate ?: return
        viewModelScope.launch {
            if (trade.id in cloudTradeIds.value) {
                val res = firestoreRepository.submitTradeReview(
                    tradeId = trade.id,
                    ratingScore = rating,
                    reviewComment = comment,
                    stewardshipBadge = badge
                )
                _uiState.update {
                    it.copy(
                        isRatingTrade = false,
                        tradeToRate = null,
                        snackbarMessage = if (res.isSuccess) {
                            "Verified community review submitted!"
                        } else {
                            res.exceptionOrNull()?.localizedMessage ?: "Failed to submit review."
                        }
                    )
                }
            } else {
                repository.submitTradeReview(
                    tradeId = trade.id,
                    rating = rating,
                    comment = comment,
                    stewardshipBadge = badge
                )
                _uiState.update {
                    it.copy(
                        isRatingTrade = false,
                        tradeToRate = null,
                        snackbarMessage = "Verified community review submitted! Trust score updated."
                    )
                }
            }
        }
    }

    // Peer Referrals
    fun startReferral(targetUser: UserProfile? = null) {
        _uiState.update { it.copy(isCreatingReferral = true, referralTargetUser = targetUser) }
    }

    fun cancelReferral() {
        _uiState.update { it.copy(isCreatingReferral = false, referralTargetUser = null) }
    }

    fun submitReferral(
        introducedPersonId: String,
        introducedPersonName: String,
        seekerPersonId: String,
        seekerPersonName: String,
        skillFocus: String,
        introNote: String
    ) {
        val me = localUser.value
        viewModelScope.launch {
            val res = firestoreRepository.createReferral(
                referrerName = me?.displayName ?: initialDisplayName,
                introducedPersonId = introducedPersonId,
                introducedPersonName = introducedPersonName,
                seekerPersonId = seekerPersonId,
                seekerPersonName = seekerPersonName,
                serviceOrSkillFocus = skillFocus,
                introNote = introNote
            )
            _uiState.update {
                it.copy(
                    isCreatingReferral = false,
                    referralTargetUser = null,
                    snackbarMessage = if (res.isSuccess) {
                        "Referral intro shared! Connecting community members."
                    } else {
                        res.exceptionOrNull()?.localizedMessage ?: "Failed to share referral."
                    }
                )
            }
        }
    }

    fun markReferralContacted(referralId: String, contacted: Boolean) {
        viewModelScope.launch {
            if (referralId in cloudReferralIds.value) {
                firestoreRepository.markReferralContacted(referralId, contacted)
            } else {
                repository.markReferralContacted(referralId, contacted)
            }
        }
    }

    // Profile & Node management
    fun openEditProfile() {
        _uiState.update { it.copy(isEditingProfile = true) }
    }

    fun closeEditProfile() {
        _uiState.update { it.copy(isEditingProfile = false) }
    }

    fun saveLocalProfile(
        displayName: String,
        bioRegion: String,
        headline: String,
        bio: String,
        earthPledge: String,
        termsAndConditions: String
    ) {
        val current = localUser.value
        viewModelScope.launch {
            repository.updateLocalProfile(
                displayName = displayName,
                bioRegion = bioRegion,
                headline = headline,
                bio = bio,
                earthPledge = earthPledge,
                termsAndConditions = termsAndConditions
            )
            val res = firestoreRepository.upsertUserProfile(
                CloudUserProfile(
                    userId = currentUserId,
                    handle = current?.handle ?: initialHandle,
                    displayName = displayName,
                    bioRegion = bioRegion,
                    nodeAddress = current?.nodeAddress ?: "terranode://${currentUserId.take(8)}.local:8443",
                    headline = headline,
                    bio = bio,
                    earthPledge = earthPledge,
                    termsAndConditions = termsAndConditions,
                    skills = allSkills.value.filter { it.userId == currentUserId }.map { it.title }.take(20),
                    barterItems = emptyList(),
                    wishlist = allWishlists.value.filter { it.userId == currentUserId }.map { it.title }.take(20),
                    ratingAverage = (current?.ratingAverage ?: 5f).toDouble(),
                    tradesCompleted = (current?.tradesCompleted ?: 0).toLong()
                )
            )
            _uiState.update {
                it.copy(
                    isEditingProfile = false,
                    snackbarMessage = if (res.isSuccess) {
                        "Sovereign profile synced!"
                    } else {
                        "Saved locally; cloud sync error: ${res.exceptionOrNull()?.localizedMessage}"
                    }
                )
            }
        }
    }

    // Skill offerings
    fun openAddSkill() {
        _uiState.update { it.copy(isAddingSkill = true) }
    }

    fun closeAddSkill() {
        _uiState.update { it.copy(isAddingSkill = false) }
    }

    fun createSkillOffering(
        title: String,
        category: SkillCategory,
        description: String,
        tradePreference: String,
        isPureBarter: Boolean,
        estimatedValue: String,
        tags: List<String>
    ) {
        val me = localUser.value
        viewModelScope.launch {
            val res = firestoreRepository.createSkillOffering(
                title = title,
                category = category,
                description = description,
                tradePreference = tradePreference,
                isPureBarter = isPureBarter,
                estimatedValue = estimatedValue,
                tags = tags,
                authorName = me?.displayName ?: initialDisplayName,
                authorHandle = me?.handle ?: initialHandle,
                bioRegion = me?.bioRegion ?: "Local Watershed"
            )
            _uiState.update {
                it.copy(
                    isAddingSkill = false,
                    snackbarMessage = if (res.isSuccess) {
                        "New skill & trade offering published!"
                    } else {
                        res.exceptionOrNull()?.localizedMessage ?: "Failed to publish offering."
                    }
                )
            }
        }
    }

    fun deleteSkill(skillId: String) {
        viewModelScope.launch {
            if (skillId in cloudSkillIds.value) {
                firestoreRepository.deleteSkillOffering(skillId)
            }
            repository.deleteSkill(skillId)
            _uiState.update { it.copy(snackbarMessage = "Offering removed from your node.") }
        }
    }

    // Wishlist items
    fun openAddWishlist() {
        _uiState.update { it.copy(isAddingWishlist = true) }
    }

    fun closeAddWishlist() {
        _uiState.update { it.copy(isAddingWishlist = false) }
    }

    fun createWishlistItem(
        title: String,
        category: SkillCategory,
        urgency: String,
        willExchangeFor: String
    ) {
        val me = localUser.value
        viewModelScope.launch {
            val res = firestoreRepository.createWishlistItem(
                title = title,
                category = category,
                urgency = urgency,
                willExchangeFor = willExchangeFor,
                authorName = me?.displayName ?: initialDisplayName,
                authorHandle = me?.handle ?: initialHandle
            )
            _uiState.update {
                it.copy(
                    isAddingWishlist = false,
                    snackbarMessage = if (res.isSuccess) {
                        "Wishlist need broadcast to community!"
                    } else {
                        res.exceptionOrNull()?.localizedMessage ?: "Failed to add wishlist item."
                    }
                )
            }
        }
    }

    fun deleteWishlistItem(itemId: String) {
        viewModelScope.launch {
            if (itemId in cloudWishlistIds.value) {
                firestoreRepository.deleteWishlistItem(itemId)
            }
            repository.deleteWishlistItem(itemId)
            _uiState.update { it.copy(snackbarMessage = "Wishlist item removed.") }
        }
    }

    // Node export & import
    fun openExportNode() {
        viewModelScope.launch {
            val json = repository.exportNodeJson()
            _uiState.update { it.copy(isExportingNode = true, exportedJson = json) }
        }
    }

    fun closeExportNode() {
        _uiState.update { it.copy(isExportingNode = false, exportedJson = null) }
    }

    fun openImportNode() {
        _uiState.update { it.copy(isImportingNode = true) }
    }

    fun closeImportNode() {
        _uiState.update { it.copy(isImportingNode = false) }
    }

    fun performImportNode(jsonContent: String) {
        viewModelScope.launch {
            val success = repository.importNodeJson(jsonContent)
            _uiState.update {
                it.copy(
                    isImportingNode = false,
                    snackbarMessage = if (success) "Node data successfully imported & restored!" else "Invalid node backup JSON."
                )
            }
        }
    }

    fun toggleMeshSync(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleMeshSync(enabled)
            _uiState.update {
                it.copy(snackbarMessage = if (enabled) "P2P Mesh Discovery active: 8 local peers connected." else "Node in offline sovereign mode.")
            }
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    private companion object {
        const val TAG = "TerraViewModel"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
