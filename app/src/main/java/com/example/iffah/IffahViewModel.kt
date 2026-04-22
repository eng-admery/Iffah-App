package com.example.iffah

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.iffah.data.IffahDatabase
import com.example.iffah.data.RelapseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import com.example.iffah.data.Achievement
import com.example.iffah.data.AchievementState
import com.example.iffah.data.AchievementsList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class IffahViewModel(application: Application) : AndroidViewModel(application) {
    // ── حساب الإنجازات ──
    fun computeAchievements() {
        val days = streakDays.value  // ← غيّر الاسم لو متغيرك مختلف (مثلاً currentStreak أو daysCount)
        _achievements.value = AchievementsList.all.map { achievement ->
            AchievementState(
                achievement = achievement,
                isUnlocked = days >= achievement.daysRequired,
                progress = (days.toFloat() / achievement.daysRequired.toFloat()).coerceIn(0f, 1f)
            )
        }

        // التحقق من إنجاز جديد لم يُبلَّغ عنه
        val lastNotifiedId = prefs.getInt("last_notified_achievement_id", 0)
        val latestUnlocked = _achievements.value
            .filter { it.isUnlocked }
            .maxByOrNull { it.achievement.id }

        if (latestUnlocked != null && latestUnlocked.achievement.id > lastNotifiedId) {
            _newlyUnlocked.value = latestUnlocked.achievement
        }
    }

    fun onAchievementDialogDismissed() {
        val id = _newlyUnlocked.value?.id ?: return
        prefs.edit().putInt("last_notified_achievement_id", id).apply()
        _newlyUnlocked.value = null
    }
    // ── الإنجازات ──
    private val _achievements = MutableStateFlow<List<AchievementState>>(emptyList())
    val achievements: StateFlow<List<AchievementState>> = _achievements.asStateFlow()

    private val _newlyUnlocked = MutableStateFlow<Achievement?>(null)
    val newlyUnlocked: StateFlow<Achievement?> = _newlyUnlocked.asStateFlow()
    private val prefs = application.getSharedPreferences("iffah_prefs", Context.MODE_PRIVATE)
    private val dao = IffahDatabase.getDatabase(application).relapseDao()

    private var startTime = prefs.getLong("start_time", 0L)

    private val _streakDays = mutableStateOf(0)
    val streakDays: State<Int> = _streakDays

    // متغير جديد لحفظ قائمة الانتكاسات
    private val _relapsesList = mutableStateOf<List<RelapseEntity>>(emptyList())
    val relapsesList: State<List<RelapseEntity>> = _relapsesList

    // ================= وقت التذكير =================
    private val _reminderHour24 = mutableStateOf(prefs.getInt("reminder_hour", 9))
    private val _reminderMinute = mutableStateOf(prefs.getInt("reminder_minute", 0))
    val reminderHour24: State<Int> = _reminderHour24
    val reminderMinute: State<Int> = _reminderMinute

    fun updateReminderTime(hour24: Int, minute: Int) {
        prefs.edit()
            .putInt("reminder_hour", hour24)
            .putInt("reminder_minute", minute)
            .apply()
        _reminderHour24.value = hour24
        _reminderMinute.value = minute
    }

    // ================= مسح البيانات =================
    fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteAllRelapses()
            withContext(Dispatchers.Main) {
                // نمسح بيانات التعافي فقط، ونحافظ على إعدادات التذكير
                prefs.edit().remove("start_time").apply()
                _streakDays.value = 0
                _relapsesList.value = emptyList()
            }
        }
    }

    init {
        calculateStreak()
        fetchRelapses() // نستدعي دالة جلب البيانات عند فتح التطبيق
    }

    private fun calculateStreak() {
        if (startTime == 0L) { _streakDays.value = 0 }
        else {
            val currentTime = System.currentTimeMillis()
            val diffInMillis = currentTime - startTime
            _streakDays.value = TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()
        }
    }

    // دالة جديدة لجلب البيانات من Room وتحديث الواجهة تلقائياً
    private fun fetchRelapses() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.getAllRelapses().collect { list ->
                withContext(Dispatchers.Main) {
                    _relapsesList.value = list
                }
            }
        }
    }

    // دالة جديدة لمعرفة المحفز الأكثر تكراراً
    fun getMostCommonTrigger(): String {
        if (_relapsesList.value.isEmpty()) return "لا يوجد بعد"
        return _relapsesList.value
            .groupingBy { it.trigger }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key ?: "لا يوجد بعد"
    }

    fun startNewStreak() {
        startTime = System.currentTimeMillis()
        prefs.edit().putLong("start_time", startTime).apply()
        calculateStreak()
    }

    fun relapse(selectedTrigger: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertRelapse(RelapseEntity(trigger = selectedTrigger))
            withContext(Dispatchers.Main) {
                startTime = 0L
                prefs.edit().putLong("start_time", 0L).apply()
                _streakDays.value = 0
            }
        }
    }
}