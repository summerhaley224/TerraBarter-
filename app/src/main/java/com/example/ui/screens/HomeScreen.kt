package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.*
import com.example.ui.components.PureBarterBadge
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    localProfile: UserProfile?,
    skills: List<SkillOffering>,
    wishlists: List<WishlistItem>,
    trades: List<BarterTrade>,
    referrals: List<PeerReferral>,
    onNavigateToMarketplace: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToListing: (String) -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToTrades: () -> Unit,
    onNavigateToReferrals: () -> Unit
) {
    val activeTradesCount = trades.count { it.status == TradeStatus.PROPOSED || it.status == TradeStatus.ACCEPTED }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Sovereign Welcome Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_hero_card"),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ForestDeep, ForestGreen)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            UserAvatar(
                                name = localProfile?.displayName ?: "Steward",
                                colorHex = localProfile?.avatarColorHex ?: 0xFF4A8C52,
                                size = 50
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Welcome back, ${localProfile?.displayName ?: "Earth Steward"}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = EarthCream
                                )
                                Text(
                                    text = localProfile?.bioRegion ?: "Local Watershed Commons",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SageMuted
                                )
                            }
                        }

                        Text(
                            text = localProfile?.earthPledge?.ifBlank {
                                "Trade freely, honor the living land, and build local abundance without middlemen."
                            } ?: "Trade freely, honor the living land, and build local abundance without middlemen.",
                            style = MaterialTheme.typography.bodySmall,
                            color = EarthCream,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onNavigateToMarketplace,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EarthCream,
                                    contentColor = ForestDeep
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("home_to_marketplace_btn")
                            ) {
                                Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Marketplace", fontWeight = FontWeight.SemiBold)
                            }

                            OutlinedButton(
                                onClick = onNavigateToProfile,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EarthCream),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("home_to_profile_btn")
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("My Profile", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // 2. Community Economy Pulse
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HomeStatCard(
                    label = "Listings",
                    value = skills.size.toString(),
                    icon = Icons.Default.Storefront,
                    onClick = onNavigateToMarketplace,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_stat_listings")
                )
                HomeStatCard(
                    label = "Active Trades",
                    value = activeTradesCount.toString(),
                    icon = Icons.Default.Handshake,
                    onClick = onNavigateToTrades,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_stat_trades")
                )
                HomeStatCard(
                    label = "Referrals",
                    value = referrals.size.toString(),
                    icon = Icons.Default.ConnectWithoutContact,
                    onClick = onNavigateToReferrals,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_stat_referrals")
                )
            }
        }

        // 3. Quick Navigation Shortcuts
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    onClick = onNavigateToCommunity,
                    color = SageMuted,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("home_to_community_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Diversity3, contentDescription = null, tint = ForestDeep)
                        Column {
                            Text(
                                text = "Community Web",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = ForestDeep
                            )
                            Text(
                                text = "Peer profiles & terms",
                                style = MaterialTheme.typography.labelSmall,
                                color = ForestDeep
                            )
                        }
                    }
                }

                Surface(
                    onClick = onNavigateToReferrals,
                    color = AmberSoft,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("home_to_referrals_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.ConnectWithoutContact, contentDescription = null, tint = TerracottaDark)
                        Column {
                            Text(
                                text = "Peer Referrals",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TerracottaDark
                            )
                            Text(
                                text = "Warm introductions",
                                style = MaterialTheme.typography.labelSmall,
                                color = TerracottaDark
                            )
                        }
                    }
                }
            }
        }

        // 4. Featured Marketplace Listings
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Featured Marketplace Listings",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ForestDeep
                    )
                    Text(
                        text = "Tap any offering to inspect barter terms & steward profile",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(
                    onClick = onNavigateToMarketplace,
                    modifier = Modifier.testTag("home_see_all_listings_btn")
                ) {
                    Text("See All", color = ForestGreen, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        if (skills.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No marketplace listings yet. Head to Marketplace or My Profile to publish your first skill or harvest offering!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(skills.take(6), key = { it.id }) { skill ->
                        FeaturedListingCard(
                            skill = skill,
                            onClick = { onNavigateToListing(skill.id) }
                        )
                    }
                }
            }
        }

        // 5. Recent Community Wishlist Needs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Neighbors Seeking (${wishlists.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ForestDeep
                )
                TextButton(onClick = onNavigateToMarketplace) {
                    Text("Browse Needs", color = Terracotta)
                }
            }
        }

        items(wishlists.take(3), key = { it.id }) { wish ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onNavigateToMarketplace() },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = wish.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ForestDeep
                        )
                        Text(
                            text = "${wish.authorName} • Offers: ${wish.willExchangeFor}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Surface(
                        color = if (wish.urgency == "High Need") AmberSoft else SageMuted,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = wish.urgency,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = if (wish.urgency == "High Need") TerracottaDark else ForestDeep,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.minimumInteractiveComponentSize(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = ForestDeep
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FeaturedListingCard(
    skill: SkillOffering,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(260.dp)
            .testTag("home_featured_listing_${skill.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = SageMuted,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = skill.category.getIcon(),
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = skill.category.shortLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = ForestDeep
                        )
                    }
                }
                if (skill.isPureBarter) {
                    PureBarterBadge()
                }
            }

            Text(
                text = skill.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = ForestDeep,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = skill.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "By ${skill.authorName} • ${skill.estimatedValue}",
                style = MaterialTheme.typography.labelSmall,
                color = TerracottaDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
