package com.lingoflow.android.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lingoflow.android.ui.chat.ChatScreen
import com.lingoflow.android.ui.dashboard.DashboardScreen
import com.lingoflow.android.ui.history.HistoryDetailScreen
import com.lingoflow.android.ui.history.HistoryScreen
import com.lingoflow.android.ui.onboarding.ModelDownloadScreen
import com.lingoflow.android.ui.onboarding.OnboardingScreen
import com.lingoflow.android.ui.settings.SettingsScreen
import com.lingoflow.android.ui.theme.LingoflowTheme

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

@Composable
fun LingoflowNavHost() {
    val navController = rememberNavController()
    val settingsViewModel: com.lingoflow.android.ui.settings.SettingsViewModel = hiltViewModel()
    val themeMode by settingsViewModel.themeMode.collectAsState()

    val bottomNavItems = listOf(
        BottomNavItem(NavRoutes.DASHBOARD, "Dashboard") {
            Icon(Icons.Filled.Dashboard, contentDescription = "Dashboard")
        },
        BottomNavItem(NavRoutes.HISTORY, "History") {
            Icon(Icons.Filled.History, contentDescription = "History")
        },
        BottomNavItem(NavRoutes.SETTINGS, "Settings") {
            Icon(Icons.Filled.Settings, contentDescription = "Settings")
        }
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hierarchy?.any { dest ->
        bottomNavItems.any { it.route == dest.route }
    } == true

    LingoflowTheme(themeMode = themeMode) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        bottomNavItems.forEach { item ->
                            NavigationBarItem(
                                icon = item.icon,
                                label = { Text(item.label) },
                                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = NavRoutes.ONBOARDING,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(NavRoutes.ONBOARDING) {
                    OnboardingScreen(
                        onSetupComplete = {
                            navController.navigate(NavRoutes.DASHBOARD) {
                                popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
                            }
                        },
                        onDownloadModel = {
                            navController.navigate(NavRoutes.MODEL_DOWNLOAD)
                        }
                    )
                }

                composable(NavRoutes.MODEL_DOWNLOAD) {
                    ModelDownloadScreen(
                        onComplete = {
                            navController.navigate(NavRoutes.DASHBOARD) {
                                popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(NavRoutes.DASHBOARD) {
                    DashboardScreen(
                        onScenarioClick = { historyId, scenarioId ->
                            navController.navigate(NavRoutes.chat(historyId, scenarioId))
                        }
                    )
                }

                composable(
                    route = NavRoutes.CHAT,
                    arguments = listOf(
                        navArgument("historyId") { type = NavType.LongType },
                        navArgument("scenarioId") { type = NavType.StringType }
                    )
                ) {
                    ChatScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(NavRoutes.SETTINGS) {
                    SettingsScreen()
                }

                composable(NavRoutes.HISTORY) {
                    HistoryScreen(
                        onHistoryClick = { historyId ->
                            navController.navigate(NavRoutes.historyDetail(historyId))
                        }
                    )
                }

                composable(
                    route = NavRoutes.HISTORY_DETAIL,
                    arguments = listOf(
                        navArgument("historyId") { type = NavType.LongType }
                    )
                ) {
                    HistoryDetailScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
