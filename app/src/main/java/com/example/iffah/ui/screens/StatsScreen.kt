package com.example.iffah.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.iffah.data.RelapseEntity
import com.example.iffah.data.SolutionsRepository
import com.example.iffah.ui.components.*
import com.example.iffah.IffahViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    relapses: List<RelapseEntity>,
    streakDays: Int,
    onDeleteAllRelapses: () -> Unit
) {
    // Trigger counts
    val triggerCounts = relapses
        .groupingBy { it.trigger }
        .eachCount()

    // Total relapses
    val totalRelapses = relapses.size

    // Best streak (calculate from relapses)
    val bestStreak = remember(relapses, streakDays) {
        if (relapses.isEmpty()) {
            streakDays
        } else {
            val sorted = relapses.sortedByDescending { it.timestamp }
            var best = 0L
            var current = if (sorted.size < relapses.size) {
                // streak is current ongoing
                streakDays.toLong()
            } else {
                0L
            }
            best = maxOf(best, current)

            for (i in 0 until sorted.size - 1) {
                val diff = (sorted[i].timestamp - sorted[i + 1].timestamp) / (1000 * 60 * 60 * 24)
                if (diff > best) best = diff
            }
            best.toInt()
        }
    }

    // State for solutions dialog
    var selectedTrigger by remember { mutableStateOf<String?>(null) }
    val selectedSolution = selectedTrigger?.let { SolutionsRepository.getSolution(it) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الإحصائيات") },
                actions = {
                    if (relapses.isNotEmpty()) {
                        IconButton(onClick = onDeleteAllRelapses) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف الكل")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Summary Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = "الانتكاسات",
                        value = "$totalRelapses",
                        subtitle = "مرة إجمالاً",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "أفضل سلسلة",
                        value = "$bestStreak",
                        subtitle = "يوماً متتالياً",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Trigger Bar Chart - clickable
            item {
                TriggerBarChart(
                    triggerCounts = triggerCounts,
                    onTriggerClick = { trigger ->
                        selectedTrigger = trigger
                    }
                )
            }

            // Recent relapses header
            item {
                Text(
                    text = "آخر الانتكاسات",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Relapse list
            if (relapses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "الحمد لله، لا توجد انتكاسات مسجلة",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(relapses.take(10)) { relapse ->
                    RelapseItem(relapse = relapse)
                }
            }
        }
    }

    // Solutions Dialog
    selectedSolution?.let { solution ->
        TriggerSolutionsDialog(
            solution = solution,
            onDismiss = { selectedTrigger = null }
        )
    }
}