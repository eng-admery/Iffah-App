package com.example.iffah.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iffah.IffahViewModel
import com.example.iffah.ui.components.EmergencyDialog
import com.example.iffah.ui.components.RelapseDialog
import com.example.iffah.ui.theme.DangerRed
import com.example.iffah.ui.theme.LightGreenBg
import com.example.iffah.ui.theme.PrimaryGreen
import com.example.iffah.ui.theme.SecondaryGreen
import com.example.iffah.ui.theme.WarningOrange

@Composable
fun HomeScreen(
    viewModel: IffahViewModel,
    onNavigateToStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    val days by viewModel.streakDays
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var showRelapseDialog by remember { mutableStateOf(false) }

    if (showEmergencyDialog) {
        EmergencyDialog(onDismiss = { showEmergencyDialog = false })
    }
    if (showRelapseDialog) {
        RelapseDialog(
            onDismiss = { showRelapseDialog = false },
            onConfirmRelapse = { trigger ->
                viewModel.relapse(trigger)
                showRelapseDialog = false
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color(0xFFFFFFFF), LightGreenBg)))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "عفّة", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
            Text(text = "رحلتك نحو الحرية والنقاء", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(200.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "يوم من التعافي", fontSize = 20.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "$days", fontSize = 80.sp, fontWeight = FontWeight.Bold, color = SecondaryGreen)
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = { showEmergencyDialog = true },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WarningOrange)
            ) {
                Text(text = "🆘 أحتاج مساعدة الآن", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (days == 0) {
                Button(
                    onClick = { viewModel.startNewStreak() },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text(text = "ابدأ رحلة التعافي اليوم", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                OutlinedButton(
                    onClick = { showRelapseDialog = true },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, DangerRed),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed)
                ) {
                    Text(text = "سجّل انتكاسة", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(onClick = onNavigateToStats) {
                Text(
                    text = "📊 عرض السجل والإحصائيات",
                    color = PrimaryGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}