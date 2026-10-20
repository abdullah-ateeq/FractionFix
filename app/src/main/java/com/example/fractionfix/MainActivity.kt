package com.example.fractionfix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fractionfix.ui.FractionFixViewModel
import com.example.fractionfix.ui.about.AboutScreen
import com.example.fractionfix.ui.history.HistoryScreen
import com.example.fractionfix.ui.home.HomeScreen
import com.example.fractionfix.ui.navigation.FractionFixBottomBar
import com.example.fractionfix.ui.navigation.Screen
import com.example.fractionfix.ui.settings.SettingsScreen
import com.example.fractionfix.ui.splash.SplashScreen
import com.example.fractionfix.ui.theme.FractionFixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: FractionFixViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            FractionFixTheme(
                themeMode = uiState.themeMode,
                dynamicColor = uiState.dynamicColor
            ) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Show bottom navigation only on main screens (not on splash)
                        if (currentRoute != null && currentRoute != Screen.Splash.route) {
                            FractionFixBottomBar(
                                currentRoute = currentRoute,
                                onNavigateToRoute = { route ->
                                    navController.navigate(route) {
                                        popUpTo(Screen.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Splash.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Splash.route) {
                            SplashScreen(
                                onSplashFinished = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Splash.route) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }

                        composable(Screen.Home.route) {
                            HomeScreen(
                                uiState = uiState,
                                viewModel = viewModel
                            )
                        }

                        composable(Screen.History.route) {
                            HistoryScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                onNavigateToHome = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Home.route) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }

                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                uiState = uiState,
                                viewModel = viewModel
                            )
                        }

                        composable(Screen.About.route) {
                            AboutScreen()
                        }
                    }
                }
            }
        }
    }
}
