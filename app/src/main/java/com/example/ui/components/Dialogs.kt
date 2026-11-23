package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun ProposeTradeDialog(
    targetUser: UserProfile,
    onDismiss: () -> Unit,
    onSubmit: (offered: String, requested: String, terms: String) -> Unit
) {
    var offeredItem by remember { mutableStateOf("") }
    var requestedItem by remember { mutableStateOf("") }
    var terms by remember { mutableStateOf("Direct peer trade, mutually agreed terms, zero corporate fees.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Propose Barter / Trade",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = ForestDeep
                )
                Text(
                    text = "With ${targetUser.displayName} (${targetUser.handle})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Member's Terms: \"${targetUser.termsAndConditions}\"",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                    color = TerracottaDark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AmberSoft)
                        .padding(8.dp)
                )

                OutlinedTextField(
                    value = offeredItem,
                    onValueChange = { offeredItem = it },
                    label = { Text("What will you provide / trade?") },
                    placeholder = { Text("e.g. 2 cords split firewood, solar repair, homemade sourdough") },
                    modifier = Modifier.fillMaxWidth().testTag("trade_offered_input"),
                    minLines = 2
                )

                OutlinedTextField(
                    value = requestedItem,
                    onValueChange = { requestedItem = it },
                    label = { Text("What are you requesting from them?") },
                    placeholder = { Text("e.g. Heirloom seed box, organic soap, bicycle transport") },
                    modifier = Modifier.fillMaxWidth().testTag("trade_requested_input"),
                    minLines = 2
                )

                OutlinedTextField(
                    value = terms,
                    onValueChange = { terms = it },
                    label = { Text("Agreed Terms & Conditions") },
                    modifier = Modifier.fillMaxWidth().testTag("trade_terms_input"),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (offeredItem.isNotBlank() && requestedItem.isNotBlank()) {
                        onSubmit(offeredItem, requestedItem, terms)
                    }
                },
                enabled = offeredItem.isNotBlank() && requestedItem.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier.testTag("submit_trade_button")
            ) {
                Text("Send Proposal", color = EarthCream)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RateTradeDialog(
    trade: BarterTrade,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String, badge: String) -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var selectedBadge by remember { mutableStateOf("Harm-Free Earth Trade") }

    val badges = listOf(
        "Harm-Free Earth Trade",
        "Deep Reverence for the Land",
        "Exceptional Craft & Integrity",
        "Punctual & Generous Spirit",
        "Circular & Waste-Free"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Honest Community Evaluation",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = ForestDeep
                )
                Text(
                    text = "Trade: ${trade.offeredItemTitle} ⇄ ${trade.requestedItemTitle}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Honest rating from actual local community member. No corporate algorithms or manipulated reviews.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Star rating selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star }) {
                            Icon(
                                imageVector = if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "$star Stars",
                                tint = StarGold,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Earth Stewardship & Community Recognition:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    badges.forEach { badge ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { selectedBadge = badge }
                                .background(if (selectedBadge == badge) SageMuted else MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = selectedBadge == badge,
                                onClick = { selectedBadge = badge }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = if (selectedBadge == badge) ForestDeep else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Your Honest Review & Experience") },
                    placeholder = { Text("Describe the trade, the quality of goods/skills, and their care for the local community...") },
                    modifier = Modifier.fillMaxWidth().testTag("trade_review_input"),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (comment.isNotBlank()) {
                        onSubmit(rating, comment, selectedBadge)
                    }
                },
                enabled = comment.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier.testTag("submit_rating_button")
            ) {
                Text("Publish Review", color = EarthCream)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CreateReferralDialog(
    allUsers: List<UserProfile>,
    preselectedUser: UserProfile?,
    onDismiss: () -> Unit,
    onSubmit: (targetId: String, targetName: String, seekerId: String, seekerName: String, focus: String, note: String) -> Unit
) {
    var introducedPerson by remember { mutableStateOf(preselectedUser ?: allUsers.firstOrNull { !it.isLocalNodeUser }) }
    var seekerPersonName by remember { mutableStateOf("A neighbor in need") }
    var skillFocus by remember { mutableStateOf("") }
    var introNote by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Peer Referral & Introduction",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = ForestDeep
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Help your neighbors find trustworthy local producers, fixers, and healers by providing a warm peer introduction.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Person with Skills / Goods to Introduce:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                // User selector
                introducedPerson?.let { current ->
                    Surface(
                        color = SageMuted,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            UserAvatar(name = current.displayName, colorHex = current.avatarColorHex, size = 36)
                            Column {
                                Text(current.displayName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                Text(current.headline, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = seekerPersonName,
                    onValueChange = { seekerPersonName = it },
                    label = { Text("Person Seeking Help (Neighbor / Friend)") },
                    placeholder = { Text("e.g. Alice Cooper (Cascadia Valley)") },
                    modifier = Modifier.fillMaxWidth().testTag("referral_seeker_input")
                )

                OutlinedTextField(
                    value = skillFocus,
                    onValueChange = { skillFocus = it },
                    label = { Text("Service, Skill or Product Needed") },
                    placeholder = { Text("e.g. Rainwater tank installation or sourdough starter") },
                    modifier = Modifier.fillMaxWidth().testTag("referral_focus_input")
                )

                OutlinedTextField(
                    value = introNote,
                    onValueChange = { introNote = it },
                    label = { Text("Short Introduction Note") },
                    placeholder = { Text("Tell them why they should connect and how their skills align!") },
                    modifier = Modifier.fillMaxWidth().testTag("referral_note_input"),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = introducedPerson ?: return@Button
                    if (skillFocus.isNotBlank() && introNote.isNotBlank()) {
                        onSubmit(target.id, target.displayName, "external_neighbor", seekerPersonName, skillFocus, introNote)
                    }
                },
                enabled = skillFocus.isNotBlank() && introNote.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier.testTag("submit_referral_button")
            ) {
                Text("Share Intro", color = EarthCream)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditProfileDialog(
    currentProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (displayName: String, bioRegion: String, headline: String, bio: String, earthPledge: String, terms: String) -> Unit
) {
    var displayName by remember { mutableStateOf(currentProfile.displayName) }
    var bioRegion by remember { mutableStateOf(currentProfile.bioRegion) }
    var headline by remember { mutableStateOf(currentProfile.headline) }
    var bio by remember { mutableStateOf(currentProfile.bio) }
    var earthPledge by remember { mutableStateOf(currentProfile.earthPledge) }
    var terms by remember { mutableStateOf(currentProfile.termsAndConditions) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Sovereign Profile",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = ForestDeep
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_name")
                )

                OutlinedTextField(
                    value = bioRegion,
                    onValueChange = { bioRegion = it },
                    label = { Text("Local Bio-Region / Watershed Node") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_region")
                )

                OutlinedTextField(
                    value = headline,
                    onValueChange = { headline = it },
                    label = { Text("Professional / Maker Headline") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_headline")
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("About Your Practice & Community Role") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_bio"),
                    minLines = 3
                )

                OutlinedTextField(
                    value = earthPledge,
                    onValueChange = { earthPledge = it },
                    label = { Text("Earth Stewardship Pledge") },
                    placeholder = { Text("How you obtain permission from the land and modify methods if nature indicates...") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_pledge"),
                    minLines = 3
                )

                OutlinedTextField(
                    value = terms,
                    onValueChange = { terms = it },
                    label = { Text("Your Terms & Conditions (Barter & Trade)") },
                    placeholder = { Text("Set your sovereign terms, exchange expectations, or fee scale...") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_terms"),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(displayName, bioRegion, headline, bio, earthPledge, terms)
                },
                enabled = displayName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text("Save Profile", color = EarthCream)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddSkillDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, category: SkillCategory, desc: String, tradePref: String, isPureBarter: Boolean, estValue: String, tags: List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(SkillCategory.FARMING_PRODUCE) }
    var description by remember { mutableStateOf("") }
    var tradePref by remember { mutableStateOf("Open to pure reciprocal barter or mutual trade") }
    var isPureBarter by remember { mutableStateOf(true) }
    var estValue by remember { mutableStateOf("Pure Barter") }
    var tagsText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Offer a Skill or Product",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = ForestDeep
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Offering Title") },
                    placeholder = { Text("e.g. Heirloom Seedling Flat or Solar Wiring Audit") },
                    modifier = Modifier.fillMaxWidth().testTag("add_skill_title")
                )

                Text("Category:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))

                // Category chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SkillCategory.values().take(3).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.shortLabel, fontSize = 11.sp) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SkillCategory.values().drop(3).take(3).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.shortLabel, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Earth Impact") },
                    modifier = Modifier.fillMaxWidth().testTag("add_skill_desc"),
                    minLines = 3
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pure Barter Only", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = isPureBarter, onCheckedChange = { isPureBarter = it })
                }

                OutlinedTextField(
                    value = tradePref,
                    onValueChange = { tradePref = it },
                    label = { Text("What you will trade for") },
                    modifier = Modifier.fillMaxWidth().testTag("add_skill_trade_pref")
                )

                OutlinedTextField(
                    value = estValue,
                    onValueChange = { estValue = it },
                    label = { Text("Estimated Value / Barter Equivalent") },
                    modifier = Modifier.fillMaxWidth().testTag("add_skill_val")
                )

                OutlinedTextField(
                    value = tagsText,
                    onValueChange = { tagsText = it },
                    label = { Text("Tags (comma separated)") },
                    placeholder = { Text("Organic, Solar, Repair, Handmade") },
                    modifier = Modifier.fillMaxWidth().testTag("add_skill_tags")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val tags = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        onAdd(title, selectedCategory, description, tradePref, isPureBarter, estValue, tags)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier.testTag("submit_new_skill_button")
            ) {
                Text("Publish Offering", color = EarthCream)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddWishlistDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, category: SkillCategory, urgency: String, willExchangeFor: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(SkillCategory.FARMING_PRODUCE) }
    var urgency by remember { mutableStateOf("Flexible") }
    var willExchangeFor by remember { mutableStateOf("") }

    val urgencies = listOf("High Need", "Flexible", "Ongoing")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Need to Wishlist",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = ForestDeep
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "A wish list of goods or skills you are seeking, refocusing trade on real human value rather than monetary wealth.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Item or Service Needed") },
                    placeholder = { Text("e.g. Clean 55-gal water barrels or help felling dead pine") },
                    modifier = Modifier.fillMaxWidth().testTag("add_wish_title")
                )

                Text("Urgency:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    urgencies.forEach { u ->
                        FilterChip(
                            selected = urgency == u,
                            onClick = { urgency = u },
                            label = { Text(u) }
                        )
                    }
                }

                OutlinedTextField(
                    value = willExchangeFor,
                    onValueChange = { willExchangeFor = it },
                    label = { Text("What will you offer in exchange?") },
                    placeholder = { Text("e.g. Fresh garden greens, tool sharpening, or carpentry") },
                    modifier = Modifier.fillMaxWidth().testTag("add_wish_exchange")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(title, selectedCategory, urgency, willExchangeFor)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier.testTag("submit_new_wish_button")
            ) {
                Text("Broadcast Need", color = EarthCream)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ExportNodeDialog(
    jsonContent: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.CloudDownload, contentDescription = null, tint = ForestGreen)
                Text(
                    text = "Export Sovereign Node",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = ForestDeep
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "You own 100% of your data. This complete JSON node package includes your sovereign cryptographic profile, skills, wishlist, and verified trades. No corporate server holds you hostage.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = jsonContent,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("TerraNode JSON", jsonContent)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Node JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Copy")
                }

                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "My TerraBarter Sovereign Node Backup")
                            putExtra(Intent.EXTRA_TEXT, jsonContent)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share or Save Node File"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Share Backup", color = EarthCream)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun ImportNodeDialog(
    onDismiss: () -> Unit,
    onImport: (json: String) -> Unit
) {
    var jsonText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = ForestGreen)
                Text(
                    text = "Import Sovereign Node",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = ForestDeep
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Paste a TerraBarter node JSON string to restore your profile, skills, and data on this device.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = jsonText,
                    onValueChange = { jsonText = it },
                    label = { Text("Paste Node JSON") },
                    modifier = Modifier.fillMaxWidth().testTag("import_node_input"),
                    minLines = 6
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (jsonText.isNotBlank()) {
                        onImport(jsonText)
                    }
                },
                enabled = jsonText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier.testTag("submit_import_button")
            ) {
                Text("Restore Node", color = EarthCream)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
