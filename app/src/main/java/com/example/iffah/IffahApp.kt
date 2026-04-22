package com.example.iffah

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.iffah.ui.screens.AchievementsScreen
import com.example.iffah.ui.screens.AdhkarScreen
import com.example.iffah.ui.screens.SettingsScreen
import com.example.iffah.ui.screens.StatsScreen

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun IffahApp(
    navController: NavHostController = rememberNavController(),
    viewModel: IffahViewModel = viewModel(),
    onRescheduleReminder: () -> Unit = {}
) {
    val bottomNavItems = listOf(
        BottomNavItem("achievements", "إنجازات", Icons.Default.Star),
        BottomNavItem("stats", "إحصائيات", Icons.Default.Info),
        BottomNavItem("adhkar", "أذكار", Icons.Default.Favorite),
        BottomNavItem("settings", "إعدادات", Icons.Default.Settings)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0B180B),
                contentColor = Color(0xFF3D5A3D)
            ) {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF4CAF50),
                            selectedTextColor = Color(0xFF4CAF50),
                            unselectedIconColor = Color(0xFF3A4A3A),
                            unselectedTextColor = Color(0xFF3A4A3A),
                            indicatorColor = Color(0xFF1A2E1A)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "achievements",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("achievements") {
                viewModel.computeAchievements()
                val achievements by viewModel.achievements.collectAsState()
                val streakDays by viewModel.streakDays.collectAsState()
                val newlyUnlocked by viewModel.newlyUnlocked.collectAsState()

                AchievementsScreen(
                    streakDays = streakDays,
                    achievements = achievements,
                    newlyUnlocked = newlyUnlocked,
                    onDismissNewAchievement = viewModel::onAchievementDialogDismissed,
                    onRelapse = { trigger -> viewModel.relapse(trigger) }
                )
            }

            composable("stats") {
                StatsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("adhkar") {
                AdhkarScreen()
            }

            composable("settings") {
                SettingsScreen(
                    viewModel = viewModel,
                    onRescheduleReminder = onRescheduleReminder
                )
            }
        }
    }
}