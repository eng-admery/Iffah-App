package com.example.iffah.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iffah.ui.theme.PrimaryGreen
import com.example.iffah.ui.theme.SecondaryGreen

// كيان الأذكار (تأكد من وجوده في مشروعك بهذا الشكل أو عدل الكود ليتطابق مع كيانك)
data class Dhikr(
    val text: String,
    val source: String,
    val category: String // "صباح", "مساء", "ثبات"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdhkarScreen(modifier: Modifier = Modifier) {
    // قائمة الأذكار (يمكنك لاحقاً نقلها لملف مستقل أو قاعدة بيانات)
    val allAdhkar = remember {
        listOf(
            Dhikr("أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ", "مسلم", "صباح"),
            Dhikr("اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ النُّشُورُ", "الترمذي", "صباح"),
            Dhikr("اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ", "البخاري", "ثبات"),
            Dhikr("حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ", "أبو داود", "ثبات"),
            Dhikr("أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ", "مسلم", "مساء"),
            Dhikr("اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ", "الترمذي", "مساء")
            // ... أضف باقي الـ 24 ذكر هنا بنفس الطريقة
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
            color = PrimaryGreen,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // أزرار الفلترة (التصنيفات)
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategory),
            edgePadding = 0.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = PrimaryGreen,
            divider = {} // إزالة الخط السفلي الافتراضي
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
                    selectedContentColor = PrimaryGreen,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder(enabled = true).copy(
            width = 1.dp,
            brush = androidx.compose.ui.graphics.SolidColor(SecondaryGreen.copy(alpha = 0.2f))
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
                color = MaterialTheme.colorScheme.onSurface
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
                    color = PrimaryGreen.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = dhikr.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                // المصدر
                Text(
                    text = "رواه ${dhikr.source}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}