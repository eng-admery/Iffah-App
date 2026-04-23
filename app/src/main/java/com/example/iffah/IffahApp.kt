package com.example.iffah

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.example.iffah.ui.screens.JournalScreen
import com.example.iffah.ui.screens.SettingsScreen
import com.example.iffah.ui.screens.StatsScreen
import com.example.iffah.ui.theme.IffahTheme

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
    val themeMode by viewModel.themeMode.collectAsState()

    IffahTheme(themeMode = themeMode) {
        val colorScheme = MaterialTheme.colorScheme

        val bottomNavItems = listOf(
            BottomNavItem("home", "الرئيسية", Icons.Default.Star), // تغيير الاسم
            BottomNavItem("stats", "إحصائيات", Icons.Default.Info),
            BottomNavItem("adhkar", "أذكار", Icons.Default.Favorite),
            BottomNavItem("settings", "إعدادات", Icons.Default.Settings)
        )

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Scaffold(
            containerColor = colorScheme.background,
            // تم حذف floatingActionButton بالكامل من هنا
            bottomBar = {
                NavigationBar(
                    containerColor = colorScheme.surface,
                    contentColor = colorScheme.onSurfaceVariant
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
                                selectedIconColor = colorScheme.primary,
                                selectedTextColor = colorScheme.primary,
                                unselectedIconColor = colorScheme.onSurfaceVariant,
                                unselectedTextColor = colorScheme.onSurfaceVariant,
                                indicatorColor = colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home", // تغيير المسار
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    viewModel.computeAchievements()
                    val achievements by viewModel.achievements.collectAsState()
                    val streakDays by viewModel.streakDays.collectAsState()
                    val newlyUnlocked by viewModel.newlyUnlocked.collectAsState()

                    AchievementsScreen(
                        streakDays = streakDays,
                        achievements = achievements,
                        newlyUnlocked = newlyUnlocked,
                        onDismissNewAchievement = viewModel::onAchievementDialogDismissed,
                        onRelapse = { trigger -> viewModel.relapse(trigger) },
                        onNavigateToJournal = { navController.navigate("journal") } // تمرير عمل الانتقال
                    )
                }

                composable("stats") {
                    val relapses by viewModel.relapsesList.collectAsState(initial = emptyList())
                    val streakDays by viewModel.streakDays.collectAsState()
                    StatsScreen(
                        relapses = relapses,
                        streakDays = streakDays,
                        onDeleteAllRelapses = { viewModel.clearAllData() }
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

                composable("journal") {
                    JournalScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}