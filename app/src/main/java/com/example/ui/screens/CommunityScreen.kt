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
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun CommunityScreen(
    users: List<UserProfile>,
    onSelectUser: (UserProfile) -> Unit,
    onProposeTrade: (UserProfile) -> Unit,
    onReferUser: (UserProfile) -> Unit
) {
    var searchFilter by remember { mutableStateOf("") }

    val filteredUsers = users.filter { u ->
        searchFilter.isBlank() ||
                u.displayName.contains(searchFilter, ignoreCase = true) ||
                u.headline.contains(searchFilter, ignoreCase = true) ||
                u.bioRegion.contains(searchFilter, ignoreCase = true) ||
                u.bio.contains(searchFilter, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Community introduction banner
        Surface(
            color = SageMuted,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Diversity3, contentDescription = null, tint = ForestGreen)
                    Text(
                        text = "Sovereign Community Web",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ForestDeep
                    )
                }
                Text(
                    text = "A collaborative peer-to-peer directory. Every neighbor controls their own data, sets their sovereign terms, and earns an honest community reputation.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ForestDeep
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchFilter,
            onValueChange = { searchFilter = it },
            placeholder = { Text("Search by name, craft, bio-region, or specialty...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ForestGreen) },
            trailingIcon = {
                if (searchFilter.isNotEmpty()) {
                    IconButton(onClick = { searchFilter = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("community_search_input")
        )

        // Member Profiles List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredUsers, key = { it.id }) { user ->
                CommunityProfileCard(
                    user = user,
                    onClick = { onSelectUser(user) },
                    onProposeTrade = { onProposeTrade(user) },
                    onReferUser = { onReferUser(user) }
                )
            }
        }
    }
}

@Composable
fun CommunityProfileCard(
    user: UserProfile,
    onClick: () -> Unit,
    onProposeTrade: () -> Unit,
    onReferUser: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("community_card_${user.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Profile header with avatar, name, bio-region & rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserAvatar(
                        name = user.displayName,
                        colorHex = user.avatarColorHex,
                        size = 52
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = user.displayName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (user.isLocalNodeUser) {
                                Surface(
                                    color = ForestGreen,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "You",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EarthCream,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${user.handle} • ${user.bioRegion}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Community Trust Score
                Column(horizontalAlignment = Alignment.End) {
                    StarRatingRow(rating = user.ratingAverage, reviewCount = user.reviewCount)
                    Text(
                        text = "${user.tradesCompleted} verified trades",
                        style = MaterialTheme.typography.labelSmall,
                        color = ForestLight
                    )
                }
            }

            // Headline
            Text(
                text = user.headline,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = ForestDeep
            )

            // Bio preview
            Text(
                text = user.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Earth Stewardship Pledge Preview
            EarthPledgeChip(
                text = "Earth Pledge: \"${user.earthPledge}\"",
                modifier = Modifier.fillMaxWidth()
            )

            // Member Sovereign Terms
            Surface(
                color = AmberSoft,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = TerracottaDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Terms: ${user.termsAndConditions}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TerracottaDark,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Node Address Fingerprint
            Text(
                text = "P2P Node: ${user.nodeAddress}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            // Actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("View Skills & Reviews", style = MaterialTheme.typography.labelMedium)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!user.isLocalNodeUser) {
                        IconButton(
                            onClick = onReferUser,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SageMuted)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Introduce",
                                tint = ForestGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Button(
                            onClick = onProposeTrade,
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Trade", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
