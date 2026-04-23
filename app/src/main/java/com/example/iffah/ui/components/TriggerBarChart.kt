package com.example.iffah.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

@Composable
fun TriggerBarChart(triggerCounts: Map<String, Int>) {
    // 1. إعداد البيانات الأساسية
    val allTriggers = listOf("ملل", "توتر", "سهر", "تصفح عشوائي")
    val maxCount = (triggerCounts.values.maxOrNull() ?: 0).coerceAtLeast(1) // نمنع القسمة على صفر

    // ألوان مختلفة لكل عمود
    val barColors = listOf(
        Color(0xFF4CAF50), // أخضر
        Color(0xFF29B6F6), // أزرق
        Color(0xFFFFA726), // برتقالي
        Color(0xFFEF5350)  // أحمر
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        // 2. حساب أماكن الأعمدة والمسافات بينها
        val barCount = allTriggers.size
        val totalGapWidth = size.width * 0.4f // 40% من العرض مسافات
        val gapWidth = totalGapWidth / (barCount + 1)
        val barWidth = (size.width - totalGapWidth) / barCount
        val chartBottom = size.height - 35.dp.toPx()
        val chartHeight = chartBottom - 20.dp.toPx()

        // للكتابة داخل Canvas نستخدم nativeCanvas
        val nativeCanvas = drawContext.canvas.nativeCanvas

        // 3. رسم كل عمود
        allTriggers.forEachIndexed { index, trigger ->
            val count = triggerCounts[trigger] ?: 0
            val barHeight = (count.toFloat() / maxCount) * chartHeight
            val x = gapWidth + index * (barWidth + gapWidth)
            val y = chartBottom - barHeight

            // ظل خفيف للعمود
            drawRoundRect(
                color = barColors[index].copy(alpha = 0.2f),
                topLeft = Offset(x - 2f, y - 2f),
                size = Size(barWidth + 4f, barHeight + 4f),
                cornerRadius = CornerRadius(8.dp.toPx())
            )

            // العمود الأساسي
            if (barHeight > 0f) {
                drawRoundRect(
                    color = barColors[index],
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
            }

            // كتابة الرقم فوق العمود
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 32f
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = true
            }
            nativeCanvas.drawText(count.toString(), x + barWidth / 2f, y - 6.dp.toPx(), textPaint)

            // كتابة اسم المحفز أسفل العمود
            val labelPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#999999")
                textSize = 26f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            nativeCanvas.drawText(trigger, x + barWidth / 2f, size.height - 6.dp.toPx(), labelPaint)
        }
    }
}