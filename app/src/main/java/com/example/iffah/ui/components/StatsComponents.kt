package com.example.iffah.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iffah.data.RelapseEntity
import java.text.SimpleDateFormat
import java.util.*

// بطاقة العدد الإجمالي أو أكثر محفز
@Composable
fun SummaryCard(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B2F)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = Color(0xFF888888), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = valueColor,
                fontSize = if (value.length <= 3) 24.sp else 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// شكل عنصر واحد في قائمة الانتكاسات
@Composable
fun RelapseItem(relapse: RelapseEntity) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd  •  HH:mm", Locale("ar"))
    val dateText = dateFormat.format(Date(relapse.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B2F)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = relapse.trigger, color = Color(0xFFFFA726), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = dateText, color = Color(0xFF777777), fontSize = 13.sp)
            }
            Text(text = "انتكاسة", color = Color(0xFFFF6B6B), fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}