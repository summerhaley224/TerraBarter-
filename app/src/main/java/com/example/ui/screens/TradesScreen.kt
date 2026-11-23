package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import com.example.data.model.BarterTrade
import com.example.data.model.TradeStatus
import com.example.ui.components.StarRatingRow
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TradesScreen(
    trades: List<BarterTrade>,
    onAcceptTrade: (String) -> Unit,
    onCompleteTrade: (String) -> Unit,
    onRateTrade: (BarterTrade) -> Unit
) {
    var selectedFilter by remember { mutableStateOf(0) } // 0: All, 1: Active, 2: Completed

    val activeTrades = trades.filter { it.status == TradeStatus.PROPOSED || it.status == TradeStatus.ACCEPTED }
    val completedTrades = trades.filter { it.status == TradeStatus.COMPLETED || it.status == TradeStatus.REVIEWED }

    val displayedTrades = when (selectedFilter) {
        1 -> activeTrades
        2 -> completedTrades
        else -> trades
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Explanatory Banner
        Surface(
            color = AmberSoft,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Handshake, contentDescription = null, tint = TerracottaDark, modifier = Modifier.size(24.dp))
                Column {
                    Text(
                        text = "Peer-to-Peer Barter Ledger",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TerracottaDark
                    )
                    Text(
                        text = "Direct trades evaluated and verified by local neighbors. Zero corporate meddling or extraction.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TerracottaDark
                    )
                }
            }
        }

        // Tab Row
        TabRow(
            selectedTabIndex = selectedFilter,
            containerColor = Color.Transparent,
            divider = {},
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Tab(
                selected = selectedFilter == 0,
                onClick = { selectedFilter = 0 },
                text = { Text("All Trades (${trades.size})") }
            )
            Tab(
                selected = selectedFilter == 1,
                onClick = { selectedFilter = 1 },
                text = { Text("Active (${activeTrades.size})") }
            )
            Tab(
                selected = selectedFilter == 2,
                onClick = { selectedFilter = 2 },
                text = { Text("Completed (${completedTrades.size})") }
            )
        }

        if (displayedTrades.isEmpty()) {
            EmptyStateView("No trades found in this category. Propose a barter exchange with a neighbor!")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayedTrades, key = { it.id }) { trade ->
                    TradeCard(
                        trade = trade,
                        onAccept = { onAcceptTrade(trade.id) },
                        onComplete = { onCompleteTrade(trade.id) },
                        onRate = { onRateTrade(trade) }
                    )
                }
            }
        }
    }
}

@Composable
fun TradeCard(
    trade: BarterTrade,
    onAccept: () -> Unit,
    onComplete: () -> Unit,
    onRate: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val dateString = remember(trade.timestamp) { dateFormat.format(Date(trade.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .testTag("trade_card_${trade.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with status & date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TradeStatusBadge(status = trade.status)
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (trade.ratingScore != null) {
                    StarRatingRow(rating = trade.ratingScore.toFloat())
                }
            }

            // Parties involved
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${trade.initiatorName} ⇄ ${trade.receiverName}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ForestDeep
                )
            }

            // Goods / Services Exchanged
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Offered: ${trade.offeredItemTitle}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = ForestDeep
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Terracotta, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Requested: ${trade.requestedItemTitle}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TerracottaDark
                        )
                    }
                }
            }

            // Terms
            Text(
                text = "Agreed Terms: \"${trade.termsAgreed}\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Review snippet if already reviewed
            if (!trade.reviewComment.isNullOrBlank()) {
                Surface(
                    color = SageMuted,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                            Text(
                                text = trade.stewardshipBadge ?: "Verified Community Review",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = ForestGreen
                            )
                        }
                        Text(
                            text = "\"${trade.reviewComment}\"",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = ForestDeep
                        )
                    }
                }
            }

            // Action Buttons based on status
            when (trade.status) {
                TradeStatus.PROPOSED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onAccept,
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Accept Barter Proposal")
                        }
                    }
                }
                TradeStatus.ACCEPTED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onComplete,
                            colors = ButtonDefaults.buttonColors(containerColor = ForestLight),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Mark Trade Completed")
                        }
                    }
                }
                TradeStatus.COMPLETED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onRate,
                            colors = ButtonDefaults.buttonColors(containerColor = StarGold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.RateReview, contentDescription = null, tint = EarthTextPrimary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Leave Honest Review", color = EarthTextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                TradeStatus.REVIEWED, TradeStatus.DECLINED -> {
                    // Fully finalized
                }
            }
        }
    }
}

@Composable
fun TradeStatusBadge(status: TradeStatus) {
    val (bgColor, textColor) = when (status) {
        TradeStatus.PROPOSED -> Pair(AmberSoft, TerracottaDark)
        TradeStatus.ACCEPTED -> Pair(SageMuted, ForestDeep)
        TradeStatus.COMPLETED -> Pair(SageGreen, EarthDark)
        TradeStatus.REVIEWED -> Pair(ForestDeep, EarthCream)
        TradeStatus.DECLINED -> Pair(Color.LightGray, Color.DarkGray)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
