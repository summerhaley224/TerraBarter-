package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SkillCategory
import com.example.ui.theme.*

@Composable
fun TerraTopAppBar(
    meshActive: Boolean,
    connectedPeers: Int,
    onNodeBadgeClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("terra_top_bar"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ForestGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = "TerraBarter Logo",
                        tint = EarthCream,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "TerraBarter",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = ForestDeep
                    )
                    Text(
                        text = "Peer-to-Peer & Earth Aligned",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Sovereign Node Mesh Status Badge
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onNodeBadgeClick() }
                    .testTag("node_status_badge"),
                color = if (meshActive) SageMuted else EarthSurfaceVariant,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (meshActive) ForestGreen else Color.Gray)
                    )
                    Text(
                        text = if (meshActive) "$connectedPeers P2P Peers" else "Node Offline",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (meshActive) ForestDeep else Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun StarRatingRow(rating: Float, reviewCount: Int? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating",
            tint = StarGold,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "%.1f".format(rating),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        if (reviewCount != null) {
            Text(
                text = " ($reviewCount)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PureBarterBadge() {
    Surface(
        color = AmberSoft,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = null,
                tint = TerracottaDark,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "Pure Barter",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = TerracottaDark
            )
        }
    }
}

@Composable
fun EarthPledgeChip(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = SageMuted,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = "Earth Pledge",
                tint = ForestGreen,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = ForestDeep,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CategoryFilterRow(
    selectedCategory: SkillCategory?,
    onSelectCategory: (SkillCategory?) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = if (selectedCategory == null) 0 else selectedCategory.ordinal + 1,
        edgePadding = 16.dp,
        divider = {},
        indicator = {},
        containerColor = Color.Transparent,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onSelectCategory(null) },
            label = { Text("All Categories") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AllInclusive,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            },
            modifier = Modifier.padding(end = 8.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ForestGreen,
                selectedLabelColor = EarthCream
            )
        )

        SkillCategory.values().forEach { cat ->
            FilterChip(
                selected = selectedCategory == cat,
                onClick = { onSelectCategory(cat) },
                label = { Text(cat.shortLabel) },
                leadingIcon = {
                    Icon(
                        imageVector = cat.getIcon(),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.padding(end = 8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ForestGreen,
                    selectedLabelColor = EarthCream
                )
            )
        }
    }
}

@Composable
fun UserAvatar(
    name: String,
    colorHex: Long,
    size: Int = 44,
    modifier: Modifier = Modifier
) {
    val initials = name.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .ifEmpty { "P" }

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Color(colorHex)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
    }
}
