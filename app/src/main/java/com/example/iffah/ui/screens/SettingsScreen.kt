package com.example.iffah.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iffah.IffahViewModel
import com.example.iffah.ui.theme.DangerRed
import com.example.iffah.ui.theme.LightGreenBg
import com.example.iffah.ui.theme.PrimaryGreen
import com.example.iffah.ui.theme.SecondaryGreen
import com.example.iffah.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: IffahViewModel,
    onRescheduleReminder: () -> Unit,
    modifier: Modifier = Modifier
) {
    // قراءة الوقت الحالي من ViewModel (24 ساعة)
    val hour24 by viewModel.reminderHour24
    val minute by viewModel.reminderMinute

    // تحويل 24 ساعة ← 12 ساعة للعرض
    val isPM = hour24 >= 12
    val h12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }
    val displayHour = String.format("%02d", h12)
    val displayMinute = String.format("%02d", minute)

    // حالة القوائم المنسدلة
    var expandedHour by remember { mutableStateOf(false) }
    var expandedMinute by remember { mutableStateOf(false) }
    var expandedPeriod by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }

    // خيارات القوائم
    val hours = (1..12).map { String.format("%02d", it) }
    val minutes = listOf("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55")
    val periods = listOf("صباحاً", "مساءً")

    // تحويل 12 ساعة ← 24 ساعة للحفظ
    fun to24h(h: Int, pm: Boolean): Int = when {
        h == 12 && !pm -> 0
        h == 12 && pm -> 12
        pm -> h + 12
        else -> h
    }

    Scaffold(
        containerColor = LightGreenBg,
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("الإعدادات", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ========== بطاقة وقت التذكير ==========
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "⏰ وقت التذكير اليومي",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "يصلك إشعار تذكير في هذا الوقت كل يوم",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // صف الساعة : الدقائق : الفترة
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // —— قائمة الساعات ——
                        Box {
                            TextButton(
                                onClick = { expandedHour = true },
                                modifier = Modifier.width(80.dp)
                            ) {
                                Text(displayHour, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                            }
                            DropdownMenu(
                                expanded = expandedHour,
                                onDismissRequest = { expandedHour = false }
                            ) {
                                hours.forEach { h ->
                                    DropdownMenuItem(
                                        text = { Text(h, fontSize = 18.sp) },
                                        onClick = {
                                            viewModel.updateReminderTime(to24h(h.toInt(), isPM), minute)
                                            onRescheduleReminder()
                                            expandedHour = false
                                        }
                                    )
                                }
                            }
                        }

                        Text(text = ":", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                        // —— قائمة الدقائق ——
                        Box {
                            TextButton(
                                onClick = { expandedMinute = true },
                                modifier = Modifier.width(80.dp)
                            ) {
                                Text(displayMinute, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                            }
                            DropdownMenu(
                                expanded = expandedMinute,
                                onDismissRequest = { expandedMinute = false }
                            ) {
                                minutes.forEach { m ->
                                    DropdownMenuItem(
                                        text = { Text(m, fontSize = 18.sp) },
                                        onClick = {
                                            viewModel.updateReminderTime(hour24, m.toInt())
                                            onRescheduleReminder()
                                            expandedMinute = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // —— قائمة صباحاً/مساءً ——
                        Box {
                            TextButton(onClick = { expandedPeriod = true }) {
                                Text(
                                    if (isPM) "مساءً" else "صباحاً",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryGreen
                                )
                            }
                            DropdownMenu(
                                expanded = expandedPeriod,
                                onDismissRequest = { expandedPeriod = false }
                            ) {
                                periods.forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text(p, fontSize = 18.sp) },
                                        onClick = {
                                            viewModel.updateReminderTime(to24h(h12, p == "مساءً"), minute)
                                            onRescheduleReminder()
                                            expandedPeriod = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ========== بطاقة مسح البيانات ==========
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "🗑️ مسح البيانات",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DangerRed
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "سيتم حذف سجل الانتكاسات بالكامل وإعادة العداد إلى الصفر",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showClearDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                    ) {
                        Text(text = "مسح كل البيانات", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ========== بطاقة عن التطبيق ==========
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "عفّة", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "الإصدار 1.0", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "صُنع بـ ❤️ لخدمة شباب الأمة",
                        fontSize = 14.sp,
                        color = SecondaryGreen,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // ========== حوار تأكيد المسح ==========
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(text = "⚠️ تأكيد مسح البيانات", fontWeight = FontWeight.Bold, color = DangerRed)
            },
            text = {
                Column {
                    Text(text = "هل أنت متأكد؟ سيتم حذف:", fontSize = 15.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• سجل الانتكاسات بالكامل", fontSize = 14.sp, color = Color.Black)
                    Text(text = "• إعادة العداد إلى الصفر", fontSize = 14.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "هذا الإجراء لا يمكن التراجع عنه.", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WarningOrange)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDialog = false
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text(text = "مسح البيانات")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showClearDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "تراجع", color = Color.Gray)
                }
            }
        )
    }
}