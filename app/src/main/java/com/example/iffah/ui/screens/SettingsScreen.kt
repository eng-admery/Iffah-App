package com.example.iffah.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iffah.IffahViewModel
import com.example.iffah.ui.theme.DangerRed
import com.example.iffah.ui.theme.LightGreenBg
import com.example.iffah.ui.theme.PrimaryGreen
import com.example.iffah.ui.theme.SecondaryGreen
import com.example.iffah.ui.theme.ThemeMode
import com.example.iffah.ui.theme.WarningOrange

// ==========================================
// 1. الشاشة الرئيسية
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: IffahViewModel,
    onRescheduleReminder: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = LightGreenBg,
        modifier = modifier,
        topBar = { SettingsTopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ThemeSection(viewModel = viewModel)
            ReminderSection(viewModel = viewModel, onRescheduleReminder = onRescheduleReminder)
            ClearDataSection(onClearClick = { showClearDialog = true })
            AboutSection()
        }
    }

    if (showClearDialog) {
        ClearDataDialog(
            onConfirm = {
                viewModel.clearAllData()
                showClearDialog = false
            },
            onDismiss = { showClearDialog = false }
        )
    }
}

// ==========================================
// 2. شريط العنوان
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar() {
    TopAppBar(
        title = { Text("الإعدادات", fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryGreen,
            titleContentColor = Color.White
        )
    )
}

// ==========================================
// 3. قسم المظهر
// ==========================================
@Composable
private fun ThemeSection(viewModel: IffahViewModel) {
    val themeMode by viewModel.themeMode.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    SettingsCard(containerColor = colorScheme.surfaceVariant) {
        Text("🎨 مظهر التطبيق", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary)
        Spacer(Modifier.height(12.dp))

        ThemeRadioButton("تلقائي (حسب النظام)", themeMode == ThemeMode.SYSTEM, colorScheme) { viewModel.setThemeMode(ThemeMode.SYSTEM) }
        ThemeRadioButton("داكن", themeMode == ThemeMode.DARK, colorScheme) { viewModel.setThemeMode(ThemeMode.DARK) }
        ThemeRadioButton("فاتح", themeMode == ThemeMode.LIGHT, colorScheme) { viewModel.setThemeMode(ThemeMode.LIGHT) }
    }
}

@Composable
private fun ThemeRadioButton(
    label: String,
    selected: Boolean,
    colorScheme: androidx.compose.material3.ColorScheme,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = colorScheme.primary,
                unselectedColor = colorScheme.onSurfaceVariant
            )
        )
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = colorScheme.onSurface)
    }
}

// ==========================================
// 4. قسم التذكيرات
// ==========================================
@Composable
private fun ReminderSection(viewModel: IffahViewModel, onRescheduleReminder: () -> Unit) {
    val hour24 by viewModel.reminderHour24.collectAsState()
    val minute by viewModel.reminderMinute.collectAsState()

    val isPM = hour24 >= 12
    val h12 = when { hour24 == 0 -> 12; hour24 > 12 -> hour24 - 12; else -> hour24 }
    val displayHour = String.format("%02d", h12)
    val displayMinute = String.format("%02d", minute)

    var expandedHour by remember { mutableStateOf(false) }
    var expandedMinute by remember { mutableStateOf(false) }
    var expandedPeriod by remember { mutableStateOf(false) }

    fun to24h(h: Int, pm: Boolean): Int = when {
        h == 12 && !pm -> 0
        h == 12 && pm -> 12
        pm -> h + 12
        else -> h
    }

    SettingsCard {
        Text("⏰ وقت التذكير اليومي", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
        Spacer(Modifier.height(4.dp))
        Text("يصلك إشعار تذكير في هذا الوقت كل يوم", fontSize = 13.sp, color = Color.Gray)
        Spacer(Modifier.height(20.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {

            TimeDropdownButton(
                text = displayHour,
                expanded = expandedHour,
                onClick = { expandedHour = true },
                onDismiss = { expandedHour = false }
            ) {
                (1..12).map { String.format("%02d", it) }.forEach { h ->
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

            Text(":", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

            TimeDropdownButton(
                text = displayMinute,
                expanded = expandedMinute,
                onClick = { expandedMinute = true },
                onDismiss = { expandedMinute = false }
            ) {
                listOf("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55").forEach { m ->
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

            Spacer(Modifier.width(8.dp))

            Box {
                TextButton(onClick = { expandedPeriod = true }) {
                    Text(if (isPM) "مساءً" else "صباحاً", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SecondaryGreen)
                }
                DropdownMenu(expanded = expandedPeriod, onDismissRequest = { expandedPeriod = false }) {
                    listOf("صباحاً", "مساءً").forEach { p ->
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

@Composable
private fun TimeDropdownButton(
    text: String,
    expanded: Boolean,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Box {
        TextButton(onClick = onClick, modifier = Modifier.width(80.dp)) {
            Text(text, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
            content()
        }
    }
}

// ==========================================
// 5. قسم مسح البيانات
// ==========================================
@Composable
private fun ClearDataSection(onClearClick: () -> Unit) {
    SettingsCard {
        Text("🗑️ مسح البيانات", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DangerRed)
        Spacer(Modifier.height(4.dp))
        Text("سيتم حذف سجل الانتكاسات بالكامل وإعادة العداد إلى الصفر", fontSize = 13.sp, color = Color.Gray)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onClearClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DangerRed)) {
            Text("مسح كل البيانات", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// 6. قسم عن التطبيق
// ==========================================
@Composable
private fun AboutSection() {
    SettingsCard(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("عفّة", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
        Spacer(Modifier.height(4.dp))
        Text("الإصدار 1.0", fontSize = 14.sp, color = Color.Gray)
        Spacer(Modifier.height(8.dp))
        Text("صُنع بـ ❤️ لخدمة شباب الأمة", fontSize = 14.sp, color = SecondaryGreen, textAlign = TextAlign.Center)
    }
}

// ==========================================
// 7. نافذة التأكيد
// ==========================================
@Composable
private fun ClearDataDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = { Text("⚠️ تأكيد مسح البيانات", fontWeight = FontWeight.Bold, color = DangerRed) },
        text = {
            Column {
                Text("هل أنت متأكد؟ سيتم حذف:", fontSize = 15.sp, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Text("• سجل الانتكاسات بالكامل", fontSize = 14.sp, color = Color.Black)
                Text("• إعادة العداد إلى الصفر", fontSize = 14.sp, color = Color.Black)
                Spacer(Modifier.height(8.dp))
                Text("هذا الإجراء لا يمكن التراجع عنه.", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WarningOrange)
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DangerRed)) {
                Text("مسح البيانات")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                Text("تراجع", color = Color.Gray)
            }
        }
    )
}

// ==========================================
// 8. بطاقة مخصصة قابلة لإعادة الاستخدام
// ==========================================
@Composable
private fun SettingsCard(
    containerColor: Color = Color.White,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}