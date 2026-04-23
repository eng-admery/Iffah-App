package com.example.iffah.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// كيان الأذكار
data class Dhikr(
    val text: String,
    val source: String,
    val category: String // "صباح", "مساء", "ثبات"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdhkarScreen(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme

    // قائمة الأذكار
    val allAdhkar = remember {
        listOf(
            Dhikr("أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ", "مسلم", "صباح"),
            Dhikr("اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ النُّشُورُ", "الترمذي", "صباح"),
            Dhikr("اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ", "البخاري", "ثبات"),
            Dhikr("حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ", "أبو داود", "ثبات"),
            Dhikr("أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ", "مسلم", "مساء"),
            Dhikr("اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ", "الترمذي", "مساء")
        )
    }

    var selectedCategory by remember { mutableStateOf("الكل") }
    val categories = listOf("الكل", "صباح", "مساء", "ثبات")

    val filteredAdhkar = if (selectedCategory == "الكل") {
        allAdhkar
    } else {
        allAdhkar.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "حصن المسلم",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = colorScheme.primary, // لون ديناميكي
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // أزرار الفلترة (التصنيفات)
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategory),
            edgePadding = 0.dp,
            containerColor = colorScheme.surface, // لون ديناميكي
            contentColor = colorScheme.primary,
            divider = {}
        ) {
            categories.forEach { category ->
                Tab(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    text = {
                        Text(
                            text = category,
                            fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    selectedContentColor = colorScheme.primary, // لون ديناميكي
                    unselectedContentColor = colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // قائمة الأذكار
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(filteredAdhkar) { dhikr ->
                DhikrCard(dhikr = dhikr)
            }
        }
    }
}

@Composable
fun DhikrCard(dhikr: Dhikr) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f) // لون ديناميكي
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder(enabled = true).copy(
            width = 1.dp,
            brush = SolidColor(colorScheme.primary.copy(alpha = 0.2f)) // لون ديناميكي بدلاً من SecondaryGreen
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = dhikr.text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = colorScheme.onSurface // لون ديناميكي
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // شارة التصنيف
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colorScheme.primaryContainer // لون ديناميكي للخلفية
                ) {
                    Text(
                        text = dhikr.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onPrimaryContainer, // لون ديناميكي للنص
                        fontWeight = FontWeight.Bold
                    )
                }

                // المصدر
                Text(
                    text = "رواه ${dhikr.source}",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.onSurface.copy(alpha = 0.5f) // لون ديناميكي
                )
            }
        }
    }
}