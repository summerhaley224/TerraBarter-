package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun MarketplaceScreen(
    skills: List<SkillOffering>,
    wishlists: List<WishlistItem>,
    searchQuery: String,
    selectedCategory: SkillCategory?,
    onlyPureBarter: Boolean,
    onSearchChange: (String) -> Unit,
    onSelectCategory: (SkillCategory?) -> Unit,
    onTogglePureBarter: (Boolean) -> Unit,
    onSelectSkill: (SkillOffering) -> Unit,
    onProposeTrade: (UserProfile) -> Unit,
    onAddSkillClick: () -> Unit,
    onAddWishlistClick: () -> Unit,
    allUsers: List<UserProfile>
) {
    var marketMode by remember { mutableStateOf(0) } // 0: Offerings (Skills/Goods), 1: Wishlist (Needs)

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (marketMode == 0) onAddSkillClick() else onAddWishlistClick()
                },
                icon = {
                    Icon(
                        imageVector = if (marketMode == 0) Icons.Default.Add else Icons.Default.PlaylistAdd,
                        contentDescription = null
                    )
                },
                text = {
                    Text(if (marketMode == 0) "Offer Skill" else "Post Need")
                },
                containerColor = ForestGreen,
                contentColor = EarthCream,
                modifier = Modifier.testTag("marketplace_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search produce, skills, repairs, soap, solar...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = ForestGreen)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("marketplace_search_input")
            )

            // Market Toggle (Skills vs Wishlist)
            TabRow(
                selectedTabIndex = marketMode,
                containerColor = Color.Transparent,
                divider = {},
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Tab(
                    selected = marketMode == 0,
                    onClick = { marketMode = 0 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Offered Goods & Skills", fontWeight = if (marketMode == 0) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
                Tab(
                    selected = marketMode == 1,
                    onClick = { marketMode = 1 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Community Wishlist & Needs", fontWeight = if (marketMode == 1) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
            }

            // Category filters & Pure barter filter
            CategoryFilterRow(
                selectedCategory = selectedCategory,
                onSelectCategory = onSelectCategory
            )

            if (marketMode == 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pure Barter Exchanges Only",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FilterChip(
                        selected = onlyPureBarter,
                        onClick = { onTogglePureBarter(!onlyPureBarter) },
                        label = { Text("No Cash Trade") },
                        leadingIcon = {
                            if (onlyPureBarter) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        }
                    )
                }
            }

            // Content list
            if (marketMode == 0) {
                // Filtered skills
                val filteredSkills = skills.filter { s ->
                    val matchesQuery = searchQuery.isBlank() ||
                            s.title.contains(searchQuery, ignoreCase = true) ||
                            s.description.contains(searchQuery, ignoreCase = true) ||
                            s.authorName.contains(searchQuery, ignoreCase = true) ||
                            s.tags.any { it.contains(searchQuery, ignoreCase = true) }
                    val matchesCategory = selectedCategory == null || s.category == selectedCategory
                    val matchesBarter = !onlyPureBarter || s.isPureBarter
                    matchesQuery && matchesCategory && matchesBarter
                }

                if (filteredSkills.isEmpty()) {
                    EmptyStateView("No offerings match your search. Try adjusting filters or offer a new skill!")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredSkills, key = { it.id }) { skill ->
                            val authorUser = allUsers.firstOrNull { it.id == skill.userId }
                            SkillOfferingCard(
                                skill = skill,
                                authorUser = authorUser,
                                onClick = { onSelectSkill(skill) },
                                onProposeTrade = {
                                    authorUser?.let { onProposeTrade(it) }
                                }
                            )
                        }
                    }
                }
            } else {
                // Filtered wishlist
                val filteredWishlist = wishlists.filter { w ->
                    val matchesQuery = searchQuery.isBlank() ||
                            w.title.contains(searchQuery, ignoreCase = true) ||
                            w.authorName.contains(searchQuery, ignoreCase = true) ||
                            w.willExchangeFor.contains(searchQuery, ignoreCase = true)
                    val matchesCategory = selectedCategory == null || w.category == selectedCategory
                    matchesQuery && matchesCategory
                }

                if (filteredWishlist.isEmpty()) {
                    EmptyStateView("No community wishlist items found. Post a need so neighbors can barter with you!")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredWishlist, key = { it.id }) { wish ->
                            val authorUser = allUsers.firstOrNull { it.id == wish.userId }
                            WishlistItemCard(
                                item = wish,
                                authorUser = authorUser,
                                onProposeTrade = {
                                    authorUser?.let { onProposeTrade(it) }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SkillOfferingCard(
    skill: SkillOffering,
    authorUser: UserProfile?,
    onClick: () -> Unit,
    onProposeTrade: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("skill_card_${skill.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Author row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UserAvatar(
                        name = skill.authorName,
                        colorHex = authorUser?.avatarColorHex ?: 0xFF2E6B34,
                        size = 38
                    )
                    Column {
                        Text(
                            text = skill.authorName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${skill.authorHandle} • ${skill.bioRegion}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (skill.isPureBarter) {
                    PureBarterBadge()
                }
            }

            // Title & category icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SageMuted),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = skill.category.getIcon(),
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = skill.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ForestDeep,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Description
            Text(
                text = skill.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Trade Preference & Value
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Wants in trade: ${skill.tradePreference}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = TerracottaDark
                    )
                    Text(
                        text = "Valuation: ${skill.estimatedValue}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tags and Action button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    skill.tags.take(3).forEach { tag ->
                        Surface(
                            color = SageMuted,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelSmall,
                                color = ForestDeep,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = onProposeTrade,
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("card_propose_barter_btn")
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Propose Barter", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun WishlistItemCard(
    item: WishlistItem,
    authorUser: UserProfile?,
    onProposeTrade: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UserAvatar(
                        name = item.authorName,
                        colorHex = authorUser?.avatarColorHex ?: 0xFF4A8C52,
                        size = 36
                    )
                    Column {
                        Text(item.authorName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(item.authorHandle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    color = if (item.urgency == "High Need") AmberSoft else SageMuted,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.urgency,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (item.urgency == "High Need") TerracottaDark else ForestDeep,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = Terracotta)
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ForestDeep
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                    Column {
                        Text("Will exchange in return:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(item.willExchangeFor, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = ForestDeep)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onProposeTrade,
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("I Can Provide This!", color = EarthCream)
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.NaturePeople,
            contentDescription = null,
            tint = SageGreen,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
