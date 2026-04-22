package com.example.iffah

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.iffah.navigation.IffahRoutes
import com.example.iffah.ui.screens.AchievementsScreen
import com.example.iffah.ui.screens.AdhkarScreen
import com.example.iffah.ui.screens.HomeScreen
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
    // قائمة التنقل الكاملة (الإنجازات داخل القائمة بشكل صحيح)
    val bottomNavItems = listOf(
        BottomNavItem(
            route = IffahRoutes.Home.route,
            label = "الرئيسية",
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            route = "achievements",
            label = "إنجازات",
            icon = Icons.Default.Star
        ),
        BottomNavItem(
            route = IffahRoutes.Stats.route,
            label = "الإحصائيات",
            icon = Icons.Default.Info
        ),
        BottomNavItem(
            route = IffahRoutes.Adhkar.route,
            label = "الأذكار",
            icon = Icons.Default.Favorite
        ),
        BottomNavItem(
            route = IffahRoutes.Settings.route,
            label = "الإعدادات",
            icon = Icons.Default.Settings
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = IffahRoutes.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(IffahRoutes.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToStats = {
                        navController.navigate(IffahRoutes.Stats.route)
                    }
                )
            }

            composable("achievements") {
                viewModel.computeAchievements()

                val achievements by viewModel.achievements.collectAsState()
                val streakDays = viewModel.streakDays.value // Removed .collectAsState() because it's already a State
                val newlyUnlocked by viewModel.newlyUnlocked.collectAsState()

                AchievementsScreen(
                    streakDays = streakDays,
                    achievements = achievements,
                    newlyUnlocked = newlyUnlocked,
                    onDismissNewAchievement = viewModel::onAchievementDialogDismissed
                )
            }

            composable(IffahRoutes.Stats.route) {
                StatsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(IffahRoutes.Adhkar.route) {
                AdhkarScreen()
            }

            composable(IffahRoutes.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onRescheduleReminder = onRescheduleReminder
                )
            }
        }
    }
}