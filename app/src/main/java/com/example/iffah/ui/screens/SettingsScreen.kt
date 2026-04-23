package com.example.iffah.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iffah.IffahViewModel
import com.example.iffah.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: IffahViewModel,
    onRescheduleReminder: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearDialog by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.background, // كان LightGreenBg
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
            onConfirm = { viewModel.clearAllData(); showClearDialog = false },
            onDismiss = { showClearDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar() {
    val colorScheme = MaterialTheme.colorScheme
    TopAppBar(
        title = { Text("الإعدادات", fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.primary, // كان PrimaryGreen
            titleContentColor = colorScheme.onPrimary // كان Color.White
        )
    )
}

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
private fun ThemeRadioButton(label: String, selected: Boolean, colorScheme: ColorScheme, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().selectable(selected = selected, onClick = onClick, role = Role.RadioButton).padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = colorScheme.primary, unselectedColor = colorScheme.onSurfaceVariant))
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = colorScheme.onSurface)
    }
}

@Composable
private fun ReminderSection(viewModel: IffahViewModel, onRescheduleReminder: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
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
        Text("⏰ وقت التذكير اليومي", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary) // كان PrimaryGreen
        Spacer(Modifier.height(4.dp))
        Text("يصلك إشعار تذكير في هذا الوقت كل يوم", fontSize = 13.sp, color = colorScheme.onSurfaceVariant) // كان Color.Gray
        Spacer(Modifier.height(20.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            TimeDropdownButton(text = displayHour, expanded = expandedHour, onClick = { expandedHour = true }, onDismiss = { expandedHour = false }, colorScheme = colorScheme) {
                (1..12).map { String.format("%02d", it) }.forEach { h ->
                    DropdownMenuItem(text = { Text(h, fontSize = 18.sp) }, onClick = {
                        viewModel.updateReminderTime(to24h(h.toInt(), isPM), minute); onRescheduleReminder(); expandedHour = false
                    })
                }
            }

            Text(":", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurfaceVariant) // كان Color.Gray

            TimeDropdownButton(text = displayMinute, expanded = expandedMinute, onClick = { expandedMinute = true }, onDismiss = { expandedMinute = false }, colorScheme = colorScheme) {
                listOf("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55").forEach { m ->
                    DropdownMenuItem(text = { Text(m, fontSize = 18.sp) }, onClick = {
                        viewModel.updateReminderTime(hour24, m.toInt()); onRescheduleReminder(); expandedMinute = false
                    })
                }
            }

            Spacer(Modifier.width(8.dp))

            Box {
                TextButton(onClick = { expandedPeriod = true }) {
                    Text(if (isPM) "مساءً" else "صباحاً", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorScheme.secondary) // كان SecondaryGreen
                }
                DropdownMenu(expanded = expandedPeriod, onDismissRequest = { expandedPeriod = false }) {
                    listOf("صباحاً", "مساءً").forEach { p ->
                        DropdownMenuItem(text = { Text(p, fontSize = 18.sp) }, onClick = {
                            viewModel.updateReminderTime(to24h(h12, p == "مساءً"), minute); onRescheduleReminder(); expandedPeriod = false
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeDropdownButton(text: String, expanded: Boolean, onClick: () -> Unit, onDismiss: () -> Unit, colorScheme: ColorScheme, content: @Composable () -> Unit) {
    Box {
        TextButton(onClick = onClick, modifier = Modifier.width(80.dp)) {
            Text(text, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary) // كان PrimaryGreen
        }
        DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) { content() }
    }
}

@Composable
private fun ClearDataSection(onClearClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    SettingsCard {
        Text("🗑️ مسح البيانات", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorScheme.error) // كان DangerRed
        Spacer(Modifier.height(4.dp))
        Text("سيتم حذف سجل الانتكاسات بالكامل وإعادة العداد إلى الصفر", fontSize = 13.sp, color = colorScheme.onSurfaceVariant) // كان Color.Gray
        Spacer(Modifier.height(12.dp))
        Button(onClick = onClearClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error)) { // كان DangerRed
            Text("مسح كل البيانات", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colorScheme.onError)
        }
    }
}

@Composable
private fun AboutSection() {
    val colorScheme = MaterialTheme.colorScheme
    SettingsCard(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("عفّة", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary) // كان PrimaryGreen
        Spacer(Modifier.height(4.dp))
        Text("الإصدار 1.0", fontSize = 14.sp, color = colorScheme.onSurfaceVariant) // كان Color.Gray
        Spacer(Modifier.height(8.dp))
        Text("صُنع بـ ❤️ لخدمة شباب الأمة", fontSize = 14.sp, color = colorScheme.secondary, textAlign = TextAlign.Center) // كان SecondaryGreen
    }
}

@Composable
private fun ClearDataDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = { Text("⚠️ تأكيد مسح البيانات", fontWeight = FontWeight.Bold, color = colorScheme.error) }, // كان DangerRed
        text = {
            Column {
                Text("هل أنت متأكد؟ سيتم حذف:", fontSize = 15.sp, color = colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Text("• سجل الانتكاسات بالكامل", fontSize = 14.sp, color = colorScheme.onSurface) // كان Color.Black
                Text("• إعادة العداد إلى الصفر", fontSize = 14.sp, color = colorScheme.onSurface) // كان Color.Black
                Spacer(Modifier.height(8.dp))
                Text("هذا الإجراء لا يمكن التراجع عنه.", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorScheme.tertiary) // كان WarningOrange
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error)) { // كان DangerRed
                Text("مسح البيانات", color = colorScheme.onError)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                Text("تراجع", color = colorScheme.onSurfaceVariant) // كان Color.Gray
            }
        }
    )
}

@Composable
private fun SettingsCard(
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface, // كان Color.White
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
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}