package com.example.iffah.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iffah.data.Achievement
import com.example.iffah.data.AchievementState
import com.example.iffah.data.SolutionsRepository
import com.example.iffah.data.TriggerSolution
import com.example.iffah.ui.components.TriggerSolutionsDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    streakDays: Int,
    achievements: List<AchievementState>,
    newlyUnlocked: Achievement?,
    onDismissNewAchievement: () -> Unit,
    onRelapse: (String) -> Unit,
    onNavigateToJournal: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    var showRelapseDialog by remember { mutableStateOf(false) }
    var selectedTrigger by remember { mutableStateOf("") }

    var selectedTriggerForSolutions by remember { mutableStateOf<String?>(null) }
    val selectedSolution: TriggerSolution? = selectedTriggerForSolutions?.let {
        SolutionsRepository.getSolution(it)
    }

    var showSosDialog by remember { mutableStateOf(false) }

    val animatedStreak = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(streakDays) {
        animatedStreak.animateTo(
            targetValue = streakDays.toFloat(),
            animationSpec = tween(durationMillis = 800)
        )
    }

    // تمت إزالة floatingActionButton من هنا تماماً

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            // الترحيب
            item {
                Text(
                    text = "الحمد لله على السلامة",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Text(
                    text = "استمر في الطريق، الله معك",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }

            // عداد الأيام الدائري
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(180.dp)
                        ) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.size(180.dp)) {
                                val strokeWidth = 12.dp.toPx()
                                drawArc(
                                    color = colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
                                    startAngle = -90f, sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                                val progress = (animatedStreak.value % 30) / 30f
                                drawArc(
                                    brush = Brush.linearGradient(listOf(colorScheme.primary, colorScheme.tertiary)),
                                    startAngle = -90f, sweepAngle = 360f * progress,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${animatedStreak.value.toInt()}", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold, color = colorScheme.onPrimaryContainer)
                                Text("يوم", style = MaterialTheme.typography.titleMedium, color = colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("سلسلة النقاء الحالية", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = colorScheme.onPrimaryContainer)
                        Text("كل يوم يمر هو إنجاز يستحق الاحتفال", style = MaterialTheme.typography.bodySmall, color = colorScheme.onPrimaryContainer.copy(alpha = 0.6f), textAlign = TextAlign.Center)
                    }
                }
            }

            // === الأزرار العادية بدل الفلوتينج ===
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showSosDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorScheme.error
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = colorScheme.error.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("SOS مساعدة", fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = { showRelapseDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("سجّل انتكاسة", color = colorScheme.onPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // زر اليوميات
            item {
                OutlinedCard(
                    onClick = onNavigateToJournal,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("يومياتي", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
                            Text("سجّل أفكارك ومشاعرك", style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant)
                        }
                        Text("→", fontSize = 20.sp, color = colorScheme.onSurfaceVariant)
                    }
                }
            }

            // الشارات
            item {
                Text("الشارات", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(achievements) { state -> AchievementCard(state) }
                }
            }

            // نص تحفيزي
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💡", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("تذكّر دائماً", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = colorScheme.onTertiaryContainer)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("\"إن العبد ليُحرم الرزق بالذنب يصيبه\"", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onTertiaryContainer.copy(alpha = 0.8f), textAlign = TextAlign.Center)
                        Text("— حديث نبوي شريف", style = MaterialTheme.typography.bodySmall, color = colorScheme.onTertiaryContainer.copy(alpha = 0.5f))
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // ===== الدايلوجات (بدون أي تغيير) =====
    if (showRelapseDialog) {
        AlertDialog(
            onDismissRequest = { showRelapseDialog = false },
            title = { Text("سجّل انتكاسة", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("اختر المحفز الرئيسي:", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    val triggers = listOf("ملل", "توتر", "سهر", "تصفح عشوائي")
                    val triggerIcons = mapOf("ملل" to "😴", "توتر" to "😰", "سهر" to "🌙", "تصفح عشوائي" to "📱")
                    triggers.forEach { trigger ->
                        val isSelected = selectedTrigger == trigger
                        Card(
                            onClick = { selectedTrigger = trigger },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) colorScheme.primaryContainer else colorScheme.surfaceVariant),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, colorScheme.primary) else null
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(triggerIcons[trigger] ?: "", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(trigger, style = MaterialTheme.typography.bodyLarge, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (selectedTrigger.isNotEmpty()) {
                        onRelapse(selectedTrigger)
                        showRelapseDialog = false
                        if (SolutionsRepository.getSolution(selectedTrigger) != null) selectedTriggerForSolutions = selectedTrigger
                        selectedTrigger = ""
                    }
                }, enabled = selectedTrigger.isNotEmpty()) { Text("سجّل", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showRelapseDialog = false; selectedTrigger = "" }) { Text("إلغاء") }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showSosDialog) {
        AlertDialog(
            onDismissRequest = { showSosDialog = false },
            title = { Text("🆘 تنفس واعٍ", fontWeight = FontWeight.Bold) },
            text = {
                var breathCount by remember { mutableIntStateOf(0) }
                val breathText = when {
                    breathCount == 0 -> "اضغط 'ابدأ' واتبع التعليمات"
                    breathCount <= 4 -> "شهيق... (4 ثوانٍ)"
                    breathCount <= 8 -> "احبس... (4 ثوانٍ)"
                    breathCount <= 12 -> "زفير... (6 ثوانٍ)"
                    breathCount <= 16 -> "شهيق... (4 ثوانٍ)"
                    breathCount <= 20 -> "احبس... (4 ثوانٍ)"
                    breathCount <= 24 -> "زفير... (6 ثوانٍ)"
                    else -> "تمام! الآن اقرأ الآية أدناه بتمعّن"
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(breathText, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                    if (breathCount > 0 && breathCount < 24) {
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(progress = { breathCount.toFloat() / 24f }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), strokeCap = StrokeCap.Round)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (breathCount < 24) {
                        Button(onClick = { breathCount++ }, shape = RoundedCornerShape(12.dp)) { Text(if (breathCount == 0) "ابدأ" else "التالي") }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("﴿ أَلَا بِذِكْرِ ٱللَّهِ تَطْمَئِنُّ ٱلْقُلُوبُ ﴾", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = colorScheme.primary, textAlign = TextAlign.Center)
                    Text("الرعد: 28", style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("قل: اللهم إني أعوذ بك من الشيطان الرجيم", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                }
            },
            confirmButton = { TextButton(onClick = { showSosDialog = false }) { Text("إغلاق", fontWeight = FontWeight.Bold) } },
            shape = RoundedCornerShape(20.dp)
        )
    }

    selectedSolution?.let { solution -> TriggerSolutionsDialog(solution = solution, onDismiss = { selectedTriggerForSolutions = null }) }

    newlyUnlocked?.let { achievement ->
        AlertDialog(
            onDismissRequest = onDismissNewAchievement,
            title = { Text("إنجاز جديد! 🎉", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(achievement.emoji, fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(achievement.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    if (achievement.subtitle.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(achievement.subtitle, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = colorScheme.onSurfaceVariant)
                    }
                }
            },
            confirmButton = { TextButton(onClick = onDismissNewAchievement) { Text("رائع!", fontWeight = FontWeight.Bold) } },
            shape = RoundedCornerShape(28.dp)
        )
    }
}

@Composable
private fun AchievementCard(state: AchievementState) {
    val colorScheme = MaterialTheme.colorScheme
    val achievement = state.achievement
    Card(
        modifier = Modifier.width(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (state.isUnlocked) colorScheme.primaryContainer else colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (state.isUnlocked) 4.dp else 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = if (state.isUnlocked) achievement.emoji else "🔒", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = achievement.title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = if (state.isUnlocked) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant.copy(alpha = 0.4f), textAlign = TextAlign.Center)
        }
    }
}