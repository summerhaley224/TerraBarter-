package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.SkillOffering
import com.example.data.model.UserProfile
import com.example.ui.components.PureBarterBadge
import com.example.ui.components.StarRatingRow
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceListingScreen(
    listing: SkillOffering?,
    author: UserProfile?,
    isOwnListing: Boolean,
    onBack: () -> Unit,
    onViewAuthorProfile: (String) -> Unit,
    onProposeTrade: (UserProfile) -> Unit,
    onReferAuthor: (UserProfile) -> Unit,
    onDeleteOwnListing: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = listing?.category?.shortLabel ?: "Marketplace Listing",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("listing_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (listing != null && !isOwnListing && author != null) {
                        IconButton(
                            onClick = { onReferAuthor(author) },
                            modifier = Modifier.testTag("listing_refer_action")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Introduce Neighbor", tint = ForestGreen)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            if (listing != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (isOwnListing) {
                            OutlinedButton(
                                onClick = { onViewAuthorProfile(listing.userId) },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("listing_manage_profile_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("My Profile")
                            }
                            Button(
                                onClick = { onDeleteOwnListing(listing.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("listing_delete_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Remove Listing", color = EarthCream)
                            }
                        } else if (author != null) {
                            OutlinedButton(
                                onClick = { onReferAuthor(author) },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("listing_introduce_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Introduce")
                            }
                            Button(
                                onClick = { onProposeTrade(author) },
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                modifier = Modifier
                                    .weight(1.4f)
                                    .heightIn(min = 48.dp)
                                    .testTag("listing_propose_barter_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Propose Barter", color = EarthCream)
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (listing == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = SageGreen,
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        text = "Listing not found or has been removed.",
                        style = MaterialTheme.typography.titleMedium,
                        color = ForestDeep
                    )
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Text("Back to Marketplace", color = EarthCream)
                    }
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("marketplace_listing_detail"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Listing Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SageMuted),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = listing.category.getIcon(),
                                        contentDescription = null,
                                        tint = ForestGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    text = listing.category.displayName,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = ForestGreen
                                )
                            }
                            if (listing.isPureBarter) {
                                PureBarterBadge()
                            }
                        }

                        Text(
                            text = listing.title,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = ForestDeep
                        )

                        if (listing.bioRegion.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Terracotta,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = listing.bioRegion,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = listing.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (listing.tags.isNotEmpty()) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listing.tags.forEach { tag ->
                                    Surface(color = SageMuted, shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            text = "#$tag",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ForestDeep,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Trade Preference & Valuation Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AmberSoft)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = TerracottaDark)
                            Text(
                                text = "Barter Terms & Exchange Preference",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TerracottaDark
                            )
                        }
                        Text(
                            text = listing.tradePreference.ifBlank { "Open to community barter proposals." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TerracottaDark
                        )
                        HorizontalDivider(color = TerracottaDark.copy(alpha = 0.2f))
                        Text(
                            text = "Estimated Community Value: ${listing.estimatedValue.ifBlank { "Direct Barter" }}",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = TerracottaDark
                        )
                    }
                }
            }

            // Steward / Author Card linking to Profile
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onViewAuthorProfile(listing.userId) }
                        .testTag("listing_author_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Offered by Steward",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                UserAvatar(
                                    name = author?.displayName ?: listing.authorName,
                                    colorHex = author?.avatarColorHex ?: 0xFF2E6B34,
                                    size = 48
                                )
                                Column {
                                    Text(
                                        text = author?.displayName ?: listing.authorName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = author?.handle ?: listing.authorHandle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (author != null) {
                                        StarRatingRow(rating = author.ratingAverage, reviewCount = author.reviewCount)
                                    }
                                }
                            }

                            OutlinedButton(
                                onClick = { onViewAuthorProfile(listing.userId) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("listing_view_profile_btn")
                            ) {
                                Text("View Profile")
                            }
                        }
                    }
                }
            }
        }
    }
}
