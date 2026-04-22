package com.example.iffah.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.iffah.data.Achievement
import com.example.iffah.data.AchievementState
import com.example.iffah.data.AchievementTier
import com.example.iffah.data.AchievementsList
import com.example.iffah.data.getCurrentTier

private val DarkBg = Color(0xFF0D1F0D)
private val CardBg = Color(0xFF142814)
private val CardBorder = Color(0xFF1E3A1E)
private val TextPrimary = Color(0xFFE8F5E9)
private val TextSecondary = Color(0xFF81C784)
private val TextMuted = Color(0xFF4A6A4A)
private val TrackColor = Color(0xFF1A2E1A)

@Composable
fun AchievementsScreen(
    streakDays: Int,
    achievements: List<AchievementState>,
    newlyUnlocked: Achievement?,
    onDismissNewAchievement: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (newlyUnlocked != null) {
        NewAchievementDialog(
            achievement = newlyUnlocked,
            onDismiss = onDismissNewAchievement
        )
    }

    val unlockedCount = achievements.count { it.isUnlocked }
    val currentTier = getCurrentTier(streakDays)
    val nextAchievement = achievements.firstOrNull { !it.isUnlocked }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp, top = 24.dp, bottom = 90.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ── الهيدر ──
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "إنجازاتك",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$unlockedCount من ${achievements.size} إنجاز",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(currentTier.colorHex).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = currentTier.label,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        color = Color(currentTier.colorHex),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // ── بطاقة الشريط الدائري ──
        item {
            CircularProgressCard(
                streakDays = streakDays,
                nextAchievement = nextAchievement,
                currentTier = currentTier
            )
        }

        // ── شارات محققة ──
        val unlockedList = achievements.filter { it.isUnlocked }
        if (unlockedList.isNotEmpty()) {
            item {
                Text(
                    text = "شاراتك المحققة",
                    color = Color(0xFFC8E6C9),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(unlockedList) { state ->
                        BadgeChip(state)
                    }
                }
            }
        }

        // ── قائمة المراحل ──
        item {
            Text(
                text = "مراحل الطريق",
                color = Color(0xFFC8E6C9),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(achievements) { state ->
            MilestoneCard(state)
        }
    }
}

/* ─────────────────────────────────────────
   شريط تقدم دائري مخصص
   ───────────────────────────────────────── */
@Composable
private fun CircularProgressCard(
    streakDays: Int,
    nextAchievement: AchievementState?,
    currentTier: AchievementTier
) {
    val progress = if (nextAchievement != null) {
        (streakDays.toFloat() / nextAchievement.achievement.daysRequired.toFloat()).coerceIn(0f, 1f)
    } else 1f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label = "circleProgress"
    )

    val tierColor = Color(currentTier.colorHex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // الشريط الدائري
            Box(
                modifier = Modifier.size(170.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val strokeWidth = 14.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2

                    // الخلفية
                    drawCircle(
                        color = TrackColor,
                        radius = radius,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // التقدم
                    if (animatedProgress > 0f) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    tierColor.copy(alpha = 0.6f),
                                    tierColor
                                )
                            ),
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$streakDays",
                        color = TextPrimary,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "يوم",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // نص الإنجاز التالي
            if (nextAchievement != null) {
                val ach = nextAchievement.achievement
                val remaining = ach.daysRequired - streakDays

                Text(
                    text = "${ach.emoji}  ${ach.title}",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "بقي $remaining يوم",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // شريط خطي صغير
                RoundedLinearProgress(
                    progress = animatedProgress,
                    color = tierColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                )
            } else {
                Text(
                    text = "🌟 وصلت للقمة!",
                    color = Color(0xFFFFD54F),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "سنة كاملة من الحرية — فخرُك لا يُوصف",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/* ─────────────────────────────────────────
   شريط تقدم خطي مدوّر
   ───────────────────────────────────────── */
@Composable
private fun RoundedLinearProgress(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.clip(RoundedCornerShape(50))) {
        val cornerR = CornerRadius(size.height / 2)
        drawRoundRect(color = TrackColor, cornerRadius = cornerR, size = size)
        if (progress > 0f) {
            drawRoundRect(
                color = color,
                cornerRadius = cornerR,
                size = Size(size.width * progress, size.height)
            )
        }
    }
}

/* ─────────────────────────────────────────
   شارة صغيرة (للصف الأفقي)
   ───────────────────────────────────────── */
@Composable
private fun BadgeChip(state: AchievementState) {
    val ach = state.achievement
    val color = Color(ach.tier.colorHex)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = ach.emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = ach.title,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* ─────────────────────────────────────────
   بطاقة مرحلة في القائمة
   ───────────────────────────────────────── */
@Composable
private fun MilestoneCard(state: AchievementState) {
    val ach = state.achievement
    val tierColor = Color(ach.tier.colorHex)

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(400)) + slideInVertically(
            tween(400, easing = EaseOutCubic),
            initialOffsetY = { it / 3 }
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (state.isUnlocked)
                        Modifier.shadow(8.dp, RoundedCornerShape(20.dp))
                    else Modifier
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (state.isUnlocked) CardBg else Color(0xFF111A11)
            ),
            border = if (state.isUnlocked)
                androidx.compose.foundation.BorderStroke(1.dp, tierColor.copy(alpha = 0.3f))
            else null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // الأيقونة الدائرية
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            color = if (state.isUnlocked) tierColor.copy(alpha = 0.15f)
                            else Color(0xFF1A1A1A),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isUnlocked) {
                        Text(text = ach.emoji, fontSize = 26.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF3A3A3A),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // النصوص
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = ach.title,
                            color = if (state.isUnlocked) TextPrimary else TextMuted,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (state.isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = tierColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (state.isUnlocked) ach.subtitle
                        else "${ach.daysRequired} يوماً",
                        color = if (state.isUnlocked) TextSecondary else Color(0xFF3A3A3A),
                        fontSize = 13.sp
                    )

                    // شريط التقدم للإنجازات غير المحققة
                    if (!state.isUnlocked && state.progress > 0f) {
                        Spacer(modifier = Modifier.height(8.dp))
                        RoundedLinearProgress(
                            progress = state.progress,
                            color = tierColor.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                        )
                    }
                }

                // رقم الأيام
                Text(
                    text = "${ach.daysRequired}",
                    color = if (state.isUnlocked) tierColor.copy(alpha = 0.5f) else Color(0xFF2A2A2A),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // الآية للإنجازات المحققة
            if (state.isUnlocked) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 0.dp)
                        .padding(bottom = 14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0F200F)
                    ) {
                        Text(
                            text = "«${ach.quote}»",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            color = Color(0xFF6A9A6A),
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

/* ─────────────────────────────────────────
   Dialog احتفالي عند تحقيق إنجاز جديد
   ───────────────────────────────────────── */
@Composable
private fun NewAchievementDialog(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    val tierColor = Color(achievement.tier.colorHex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .shadow(32.dp, RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = DarkBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // توهّج خلفي
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    tierColor.copy(alpha = 0.35f),
                                    tierColor.copy(alpha = 0.05f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = achievement.emoji, fontSize = 60.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "🎉 إنجاز جديد!",
                    color = Color(0xFFFFD54F),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = achievement.title,
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = achievement.subtitle,
                    color = TextSecondary,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // بطاقة الآية
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Text(
                        text = "«${achievement.quote}»",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFFA5D6A7),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = tierColor)
                ) {
                    Text(
                        text = "الحمد لله 🤲",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D1F0D)
                    )
                }
            }
        }
    }
}