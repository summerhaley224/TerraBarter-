package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.PeerReferral
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReferralsScreen(
    referrals: List<PeerReferral>,
    onNewReferralClick: () -> Unit,
    onToggleContacted: (String, Boolean) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewReferralClick,
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Make Introduction") },
                containerColor = ForestGreen,
                contentColor = EarthCream,
                modifier = Modifier.testTag("new_referral_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Card
            Surface(
                color = SageMuted,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.ConnectWithoutContact, contentDescription = null, tint = ForestGreen)
                        Text(
                            text = "Peer Referral & Introductions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ForestDeep
                        )
                    }
                    Text(
                        text = "Know someone who needs fresh eggs, solar wiring, or healing herbs? Introduce two people directly with honest context, fostering real word-of-mouth community networks.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ForestDeep
                    )
                }
            }

            if (referrals.isEmpty()) {
                EmptyStateView("No peer referrals recorded yet. Be the bridge: introduce two neighbors who can barter and support each other!")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(referrals, key = { it.id }) { ref ->
                        ReferralCard(
                            referral = ref,
                            onToggleContacted = { onToggleContacted(ref.id, !ref.isContacted) },
                            onShareReferral = {
                                val shareText = """
                                    🌱 Peer Introduction from TerraBarter:
                                    ${ref.referrerName} is introducing ${ref.introducedPersonName} to ${ref.seekerPersonName}!
                                    Focus: ${ref.serviceOrSkillFocus}
                                    
                                    "${ref.introNote}"
                                    
                                    Decentralized, open-source peer community trade.
                                """.trimIndent()

                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Peer Introduction"))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReferralCard(
    referral: PeerReferral,
    onToggleContacted: () -> Unit,
    onShareReferral: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val dateString = remember(referral.timestamp) { dateFormat.format(Date(referral.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .testTag("referral_card_${referral.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top row: Referrer and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Introduced by ${referral.referrerName}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ForestDeep
                    )
                }
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Connection Box
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "${referral.seekerPersonName}  ⟷  ${referral.introducedPersonName}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ForestDeep
                    )
                    Text(
                        text = "Skill or Service Focus: ${referral.serviceOrSkillFocus}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TerracottaDark
                    )
                }
            }

            // Intro Note
            Surface(
                color = AmberSoft,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.FormatQuote, contentDescription = null, tint = TerracottaDark, modifier = Modifier.size(20.dp))
                    Text(
                        text = referral.introNote,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TerracottaDark
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            // Bottom actions: Contacted check and Native Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Checkbox(
                        checked = referral.isContacted,
                        onCheckedChange = { onToggleContacted() }
                    )
                    Text(
                        text = if (referral.isContacted) "Connection Made" else "Pending Intro",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = if (referral.isContacted) ForestGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedButton(
                    onClick = onShareReferral,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Share Intro", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
