package com.example.iffah.data

data class TriggerSolution(
    val triggerKey: String,
    val triggerLabel: String,
    val icon: String,
    val verse: String,
    val verseSource: String,
    val solutions: List<SolutionItem>
)

data class SolutionItem(
    val title: String,
    val description: String,
    val icon: String,
    val category: SolutionCategory
)

enum class SolutionCategory {
    WORSHIP,    // عبادات
    PHYSICAL,   // جسدية
    PSYCHOLOGICAL, // نفسية
    PRACTICAL   // عملية
}