package com.example.iffah.data

object SolutionsRepository {
    private val solutions = listOf(
        TriggerSolution(
            triggerKey = "ملل",
            triggerLabel = "الملل",
            icon = "😴",
            verse = "﴿ الَّذِينَ يَذْكُرُونَ اللَّهَ قِيَامًا وَقُعُودًا وَعَلَىٰ جُنُوبِهِمْ ﴾",
            verseSource = "آل عمران: 191",
            solutions = listOf(
                SolutionItem("قم للصلاة", "صلِ ركعتين بنية التوبة وتجديد النشاط.", "🕌", SolutionCategory.WORSHIP),
                SolutionItem("القراءة", "اقرأ في كتاب مفيد لمدة 15 دقيقة.", "📚", SolutionCategory.PRACTICAL),
                SolutionItem("الرياضة", "مارس تمارين الضغط أو المشي السريع.", "🏃", SolutionCategory.PHYSICAL)
            )
        ),
        TriggerSolution(
            triggerKey = "توتر",
            triggerLabel = "التوتر",
            icon = "😰",
            verse = "﴿ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ ﴾",
            verseSource = "الرعد: 28",
            solutions = listOf(
                SolutionItem("التنفس العميق", "طبق تقنية 4-4-6 للتنفس الهادئ.", "🌬️", SolutionCategory.PSYCHOLOGICAL),
                SolutionItem("الاستعاذة", "استعذ بالله من الشيطان الرجيم بصدق.", "🛡️", SolutionCategory.WORSHIP),
                SolutionItem("الاستحمام بالماء البارد", "يساعد في تهدئة الجهاز العصبي فوراً.", "🚿", SolutionCategory.PHYSICAL)
            )
        ),
        TriggerSolution(
            triggerKey = "سهر",
            triggerLabel = "السهر",
            icon = "🌙",
            verse = "﴿ وَجَعَلْنَا النَّوْمَ سُبَاتًا ﴾",
            verseSource = "النبأ: 9",
            solutions = listOf(
                SolutionItem("اترك هاتفك", "ضع الهاتف في غرفة أخرى قبل النوم بـ 30 دقيقة.", "📵", SolutionCategory.PRACTICAL),
                SolutionItem("أذكار النوم", "اقرأ أذكار النوم بتمعن.", "📖", SolutionCategory.WORSHIP),
                SolutionItem("تغيير المكان", "إذا لم تستطع النوم، لا تبقَ في السرير.", "🚶", SolutionCategory.PRACTICAL)
            )
        ),
        TriggerSolution(
            triggerKey = "تصفح عشوائي",
            triggerLabel = "التصفح العشوائي",
            icon = "📱",
            verse = "﴿ وَالَّذِينَ هُمْ عَنِ اللَّغْوِ مُعْرِضُونَ ﴾",
            verseSource = "المؤمنون: 3",
            solutions = listOf(
                SolutionItem("قاعدة الـ 5 دقائق", "أغلق التطبيق فوراً وعد بعد 5 دقائق إذا كان ضرورياً.", "⏱️", SolutionCategory.PRACTICAL),
                SolutionItem("حذف التطبيق المثير", "احذف التطبيق الذي يسبب لك التصفح اللاواعي.", "🗑️", SolutionCategory.PRACTICAL),
                SolutionItem("الاستغفار", "استغفر الله كلما وجدت نفسك تائهاً في الشاشة.", "📿", SolutionCategory.WORSHIP)
            )
        )
    )

    fun getSolution(trigger: String): TriggerSolution? {
        return solutions.find { it.triggerKey == trigger }
    }
}
