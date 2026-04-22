package com.example.iffah.data

enum class AchievementTier(val label: String, val colorHex: Long) {
    BEGINNER("مبتدئ", 0xFF66BB6A),
    BRONZE("برونزي", 0xFFCD7F32),
    SILVER("فضي", 0xFFB0BEC5),
    GOLD("ذهبي", 0xFFFFD54F),
    PLATINUM("بلاتيني", 0xFF80DEEA),
    DIAMOND("ماسي", 0xFFCE93D8)
}

data class Achievement(
    val id: Int,
    val title: String,
    val subtitle: String,
    val daysRequired: Int,
    val emoji: String,
    val tier: AchievementTier,
    val quote: String
)

data class AchievementState(
    val achievement: Achievement,
    val isUnlocked: Boolean,
    val progress: Float
)

object AchievementsList {
    val all = listOf(
        Achievement(1, "البداية", "أول خطوة في الطريق", 1, "🌱", AchievementTier.BEGINNER,
            "إِنَّمَا الْأَعْمَالُ بِالنِّيَّاتِ"),
        Achievement(2, "عزيمة", "ثلاثة أيام من الثبات", 3, "💪", AchievementTier.BEGINNER,
            "وَمَن يَتَوَكَّلْ عَلَى اللَّهِ فَهُوَ حَسْبُهُ"),
        Achievement(3, "أسبوع حر", "سبعة أيام من النقاء", 7, "⭐", AchievementTier.BRONZE,
            "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا"),
        Achievement(4, "نصف شهر", "أربعة عشر يوماً من التحكم", 14, "🔥", AchievementTier.BRONZE,
            "وَالَّذِينَ جَاهَدُوا فِينَا لَنَهْدِيَنَّهُمْ سُبُلَنَا"),
        Achievement(5, "ثلاثة أسابيع", "العادة الجديدة تتشكّل", 21, "🌙", AchievementTier.SILVER,
            "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ"),
        Achievement(6, "شهر كامل", "ثلاثون يوماً من الحرية", 30, "🏆", AchievementTier.SILVER,
            "إِنَّ اللَّهَ لَا يُغَيِّرُ مَا بِقَوْمٍ حَتَّىٰ يُغَيِّرُوا مَا بِأَنفُسِهِمْ"),
        Achievement(7, "ستون يوماً", "الاستقرار يبدأ", 60, "🛡️", AchievementTier.GOLD,
            "وَاسْتَعِينُوا بِالصَّبْرِ وَالصَّلَاةِ"),
        Achievement(8, "فصل حر", "تسعون يوماً — تحوّل حقيقي", 90, "👑", AchievementTier.GOLD,
            "وَلَسَوْفَ يُعْطِيكَ رَبُّكَ فَتَرْضَىٰ"),
        Achievement(9, "نصف عام", "مئة وثمانون يوماً", 180, "💎", AchievementTier.PLATINUM,
            "إِنَّ اللَّهَ مَعَ الصَّابِرِينَ"),
        Achievement(10, "سنة حرية", "ثلاثمئة وستون يوماً — القمة", 365, "🌟", AchievementTier.DIAMOND,
            "وَمَن يَتَّقِ اللَّهَ يَجْعَل لَّهُ مَخْرَجًا")
    )
}

fun getCurrentTier(streakDays: Int): AchievementTier = when {
    streakDays >= 365 -> AchievementTier.DIAMOND
    streakDays >= 180 -> AchievementTier.PLATINUM
    streakDays >= 60  -> AchievementTier.GOLD
    streakDays >= 21  -> AchievementTier.SILVER
    streakDays >= 7   -> AchievementTier.BRONZE
    else -> AchievementTier.BEGINNER
}