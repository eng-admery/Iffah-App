package com.example.iffah.navigation

sealed class IffahRoutes(val route: String) {
    object Home : IffahRoutes("home")
    object Stats : IffahRoutes("stats")
    object Settings : IffahRoutes("settings")
    object Adhkar : IffahRoutes("adhkar")
    data object Achievements : IffahRoutes("achievements")
}