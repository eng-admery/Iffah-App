package com.example.iffah.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iffah.data.RelapseEntity
import com.example.iffah.ui.components.RelapseItem
import com.example.iffah.ui.components.SummaryCard
import com.example.iffah.ui.components.TriggerBarChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    relapses: List<RelapseEntity>,
    onDeleteAllRelapses: () -> Unit
) {
    // معالجة البيانات (حساب الأرقام من القائمة)
    val triggerCounts = remember(relapses) {
        relapses.groupingBy { it.trigger }.eachCount()
    }
    val mostCommonTrigger = remember(triggerCounts) {
        triggerCounts.maxByOrNull { it.value }?.key
    }

    // الهيكل الأساسي للشاشة
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الإحصائيات", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B1B2F))
            )
        },
        containerColor = Color(0xFF0F0F1A)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {

            // 1. صف البطاقات (استدعاء من StatsComponents)
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryCard(
                        title = "إجمالي الانتكاسات",
                        value = relapses.size.toString(),
                        valueColor = Color(0xFFFF6B6B),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "أكثر محفز",
                        value = mostCommonTrigger ?: "لا يوجد",
                        valueColor = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 2. بطاقة الرسم البياني (استدعاء من TriggerBarChart)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B2F)),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "📊 توزيع المحفزات", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        TriggerBarChart(triggerCounts = triggerCounts)
                    }
                }
            }

            // 3. عنوان القائمة وزر الحذف
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "سجل الانتكاسات", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    if (relapses.isNotEmpty()) {
                        IconButton(onClick = onDeleteAllRelapses) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف الكل", tint = Color(0xFFFF6B6B))
                        }
                    }
                }
            }

            // 4. محتوى القائمة (استدعاء RelapseItem من StatsComponents)
            if (relapses.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
                        Text(text = "لا توجد انتكاسات — أحسنت! 🎉", color = Color(0xFF4CAF50), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            } else {
                items(relapses.reversed()) { relapse ->
                    RelapseItem(relapse = relapse)
                }
            }
        }
    }
}