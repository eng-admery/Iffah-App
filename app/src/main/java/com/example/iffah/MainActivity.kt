package com.example.iffah

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.content.ContextCompat
import com.example.iffah.data.RelapseEntity
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val PrimaryGreen = Color(0xFF1B5E20)
val SecondaryGreen = Color(0xFF4CAF50)
val LightGreenBg = Color(0xFFF1F8E9)
val WarningOrange = Color(0xFFE65100)
val DangerRed = Color(0xFFC62828)

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) scheduleDailyReminder()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val helper = NotificationHelper(this)
        helper.createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                scheduleDailyReminder()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            scheduleDailyReminder()
        }

        setContent {
            // متغير للتحكم في إظهار شاشة البداية
            var showSplash by remember { mutableStateOf(true) }

            // هذه الدالة تنتظر 2.5 ثانية ثم تخفي شاشة البداية
            LaunchedEffect(Unit) {
                delay(2500)
                showSplash = false
            }

            if (showSplash) {
                // عرض شاشة البداية
                SplashScreen()
            } else {
                // عرض التطبيق الأساسي
                var currentScreen by remember { mutableStateOf("home") }
                val viewModel: IffahViewModel = viewModel()

                when (currentScreen) {
                    "home" -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToStats = { currentScreen = "stats" }
                    )
                    "stats" -> StatsScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = "home" }
                    )
                }
            }
        }
    }

    private fun scheduleDailyReminder() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}

// ================= كود شاشة البداية الجديدة =================
@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PrimaryGreen, SecondaryGreen) // تدرج أخضر جميل
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "عفّة",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "إِنَّ اللَّهَ يُحِبُّ التَّوَّابِينَ وَيُحِبُّ الْمُتَطَهِّرِينَ",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}
// ==========================================================

@Composable
fun HomeScreen(viewModel: IffahViewModel = viewModel(), onNavigateToStats: () -> Unit) {
    val days by viewModel.streakDays
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var showRelapseDialog by remember { mutableStateOf(false) }

    if (showEmergencyDialog) { EmergencyDialog(onDismiss = { showEmergencyDialog = false }) }
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
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color(0xFFFFFFFF), LightGreenBg)))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = "عفّة", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
            Text(text = "رحلتك نحو الحرية والنقاء", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(40.dp))

            Card(modifier = Modifier.fillMaxWidth(0.85f).height(200.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(text = "يوم من التعافي", fontSize = 20.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "$days", fontSize = 80.sp, fontWeight = FontWeight.Bold, color = SecondaryGreen)
                }
            }
            Spacer(modifier = Modifier.height(50.dp))

            Button(onClick = { showEmergencyDialog = true }, modifier = Modifier.fillMaxWidth(0.8f).height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = WarningOrange)) {
                Text(text = "🆘 أحتاج مساعدة الآن", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (days == 0) {
                Button(onClick = { viewModel.startNewStreak() }, modifier = Modifier.fillMaxWidth(0.8f).height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)) {
                    Text(text = "ابدأ رحلة التعافي اليوم", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                OutlinedButton(onClick = { showRelapseDialog = true }, modifier = Modifier.fillMaxWidth(0.8f).height(56.dp), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(2.dp, DangerRed), colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed)) {
                    Text(text = "سجّل انتكاسة", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = onNavigateToStats) { Text(text = "📊 عرض السجل والإحصائيات", color = PrimaryGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: IffahViewModel, onBack: () -> Unit) {
    val relapses by viewModel.relapsesList
    val mostCommon = viewModel.getMostCommonTrigger()
    val dateFormat = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar"))

    Scaffold(containerColor = LightGreenBg, topBar = {
        TopAppBar(title = { Text("سجل الانتكاسات", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen, titleContentColor = Color.White), navigationIcon = {
            TextButton(onClick = onBack) { Text("← رجوع", color = Color.White, fontWeight = FontWeight.Bold) }
        })
    }) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "⚠️ انتبه لنقطة الضعف!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WarningOrange)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "أكثر محفز يؤدي للانتكاسة لديك هو:", fontSize = 14.sp, color = Color.Gray)
                    Text(text = mostCommon, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = WarningOrange)
                }
            }
            Text(text = "السجل الكامل:", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryGreen, modifier = Modifier.padding(bottom = 8.dp))

            if (relapses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "لا يوجد سجل انتكاسات بعد..\nأحمد الله وحافظ على عافيتك!", textAlign = TextAlign.Center, color = Color.Gray, fontSize = 18.sp)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(relapses) { relapse ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(text = relapse.trigger, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DangerRed)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = dateFormat.format(Date(relapse.timestamp)), fontSize = 13.sp, color = Color.Gray)
                                }
                                Text(text = "❌", fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmergencyDialog(onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = { onDismiss() }, shape = RoundedCornerShape(24.dp), title = { Text(text = "اللهم احفظني", fontWeight = FontWeight.Bold, color = PrimaryGreen) }, text = {
        Column {
            Text(text = "﴿ وَمَن يَتَّقِ اللَّهَ يَجْعَل لَّهُ مَخْرَجًا ﴾", fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "تنفس بعمق...\nخذ نفساً عميقاً من أنفك (4 ثوان)\nاحبسه (4 ثوان)\nأخرجه من فمك ببطء (6 ثوان)", fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }, confirmButton = { Button(onClick = { onDismiss() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)) { Text(text = "لقد هدأت، الحمد لله") } })
}

@Composable
fun RelapseDialog(onDismiss: () -> Unit, onConfirmRelapse: (String) -> Unit) {
    val triggers = listOf("الملل", "التوتر والضغط النفسي", "السهر والوحدة", "التصفح العشوائي بدون هدف")
    var selectedTrigger by remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = { onDismiss() }, shape = RoundedCornerShape(24.dp), title = { Text(text = "لا تيأس، رحمة الله واسعة", fontWeight = FontWeight.Bold, color = DangerRed) }, text = {
        Column {
            Text(text = "التوبة تجب ما قبلها. لا تدع الشيطان يخبرك أنك فشلت! ما هو الشيء الذي أوقعك؟", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            triggers.forEach { trigger ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    RadioButton(selected = (trigger == selectedTrigger), onClick = { selectedTrigger = trigger }, colors = RadioButtonDefaults.colors(selectedColor = DangerRed))
                    Text(text = trigger)
                }
            }
        }
    }, confirmButton = { Button(onClick = { onConfirmRelapse(selectedTrigger) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), enabled = selectedTrigger.isNotEmpty(), colors = ButtonDefaults.buttonColors(containerColor = DangerRed)) { Text(text = if(selectedTrigger.isNotEmpty()) "لقد تبت، ابدأ من جديد" else "اختر المحفز أولاً") } }, dismissButton = { TextButton(onClick = { onDismiss() }) { Text(text = "إلغاء") } })
}