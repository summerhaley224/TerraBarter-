package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*

@Composable
fun MyNodeScreen(
    localProfile: UserProfile?,
    nodeIdentity: NodeIdentity?,
    mySkills: List<SkillOffering>,
    myWishlist: List<WishlistItem>,
    onEditProfileClick: () -> Unit,
    onAddSkillClick: () -> Unit,
    onDeleteSkillClick: (String) -> Unit,
    onAddWishlistClick: () -> Unit,
    onDeleteWishlistClick: (String) -> Unit,
    onExportNodeClick: () -> Unit,
    onImportNodeClick: () -> Unit,
    onToggleMeshSync: (Boolean) -> Unit,
    onSignOutClick: () -> Unit
) {
    if (localProfile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ForestGreen)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Sovereign Decentralized Node Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("node_status_card"),
                colors = CardDefaults.cardColors(containerColor = EarthDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = null,
                                tint = SageGreen,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Self-Sovereign Data Node",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = EarthCream
                            )
                        }

                        // Mesh Toggle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (nodeIdentity?.isMeshSyncing == true) "Mesh Active" else "Offline",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (nodeIdentity?.isMeshSyncing == true) SageGreen else Color.Gray,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Switch(
                                checked = nodeIdentity?.isMeshSyncing == true,
                                onCheckedChange = onToggleMeshSync,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ForestGreen,
                                    checkedTrackColor = SageMuted
                                )
                            )
                        }
                    }

                    // Node Address
                    Surface(
                        color = Color(0xFF283425),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "P2P Endpoint: ${localProfile.nodeAddress}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = SageGreen
                            )
                            Text(
                                text = "Key Fingerprint: ${nodeIdentity?.publicKeyFingerprint ?: "SHA256:7f89b9a4c01..."}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color.LightGray
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Connected Local Peers: ${nodeIdentity?.connectedPeersCount ?: 8}",
                            style = MaterialTheme.typography.labelSmall,
                            color = EarthCream
                        )
                        Text(
                            text = "Storage: ${nodeIdentity?.storageUsageKb ?: 1420} KB (100% Owned)",
                            style = MaterialTheme.typography.labelSmall,
                            color = EarthCream
                        )
                    }

                    // Export & Import Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onExportNodeClick,
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            modifier = Modifier.weight(1f).testTag("export_node_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Export Node JSON", fontSize = 12.sp, color = EarthCream)
                        }

                        OutlinedButton(
                            onClick = onImportNodeClick,
                            modifier = Modifier.weight(1f).testTag("import_node_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Restore / Import", fontSize = 12.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = onSignOutClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("sign_out_btn"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EarthCream)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sign Out", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Sign Out of Google Session", fontSize = 12.sp)
                    }
                }
            }
        }

        // 2. Profile Details & Editing Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            UserAvatar(name = localProfile.displayName, colorHex = localProfile.avatarColorHex, size = 56)
                            Column {
                                Text(
                                    text = localProfile.displayName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${localProfile.handle} • ${localProfile.bioRegion}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = onEditProfileClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SageMuted)
                                .testTag("edit_profile_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = ForestGreen)
                        }
                    }

                    Text(
                        text = localProfile.headline,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = ForestDeep
                    )

                    Text(
                        text = localProfile.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Earth Stewardship Pledge
                    Surface(
                        color = SageMuted,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Spa, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "My Earth Stewardship Pledge",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = ForestGreen
                                )
                            }
                            Text(
                                text = localProfile.earthPledge,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ForestDeep
                            )
                        }
                    }

                    // Member Sovereign Terms
                    Surface(
                        color = AmberSoft,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Handshake, contentDescription = null, tint = TerracottaDark, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "My Sovereign Terms & Conditions",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TerracottaDark
                                )
                            }
                            Text(
                                text = localProfile.termsAndConditions,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TerracottaDark
                            )
                        }
                    }
                }
            }
        }

        // 3. My Offerings & Skills Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "My Goods & Skills Offered",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ForestDeep
                    )
                    Text(
                        text = "Products and services you provide to the local economy",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onAddSkillClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_offering_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Offering", fontSize = 12.sp)
                }
            }
        }

        if (mySkills.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "You haven't listed any offerings yet. Add a skill (repair, growing, teaching, energy) to start bartering with your neighbors!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(mySkills, key = { it.id }) { skill ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(skill.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text(skill.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                            Text("Trade: ${skill.tradePreference}", style = MaterialTheme.typography.labelSmall, color = TerracottaDark)
                        }
                        IconButton(onClick = { onDeleteSkillClick(skill.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Gray)
                        }
                    }
                }
            }
        }

        // 4. My Needs / Wishlist Section
        item {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "My Wishlist & Needs",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ForestDeep
                    )
                    Text(
                        text = "Focusing economy on real human value and mutual aid",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onAddWishlistClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_wishlist_btn")
                ) {
                    Icon(Icons.Default.PlaylistAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Need", fontSize = 12.sp)
                }
            }
        }

        if (myWishlist.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Your wishlist is empty. Let your neighbors know what tools, ingredients, or help you need!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(myWishlist, key = { it.id }) { wish ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(wish.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("Will exchange: ${wish.willExchangeFor}", style = MaterialTheme.typography.bodySmall, color = ForestGreen)
                            Text("Urgency: ${wish.urgency}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { onDeleteWishlistClick(wish.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
