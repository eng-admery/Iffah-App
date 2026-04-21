package com.example.iffah

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val helper = NotificationHelper(context)
        helper.createNotificationChannel()

        // عبارات تذكر وت Rodgers تظهر عشوائياً
        val messages = listOf(
            "يا بني، حافظ على فرجك، فإنه من صام عنها دخل الجنة.",
            "اللهم إني أعوذ بك من زوال نعمتك، وتحول عافيتك، وفجاءة نقمتك، وجميع سخطك.",
            "استعذ بالله من الشيطان الرجيم، وتوضأ وصلي ركعتين.",
            "لا تكن سهلاً على نفسك، فإن أهون الناس على أنفسهم أهل الغفلة.",
            "تذكر أن الله يراك، واستحِ من الملائكة الذين معك."
        )
        val randomMessage = messages.random()

        helper.showNotification("تذكير من عفّة 💚", randomMessage)
    }
}