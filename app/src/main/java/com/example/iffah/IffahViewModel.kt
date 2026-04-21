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

class IffahViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("iffah_prefs", Context.MODE_PRIVATE)
    private val dao = IffahDatabase.getDatabase(application).relapseDao()

    private var startTime = prefs.getLong("start_time", 0L)

    private val _streakDays = mutableStateOf(0)
    val streakDays: State<Int> = _streakDays

    // متغير جديد لحفظ قائمة الانتكاسات
    private val _relapsesList = mutableStateOf<List<RelapseEntity>>(emptyList())
    val relapsesList: State<List<RelapseEntity>> = _relapsesList

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