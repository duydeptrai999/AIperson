package com.dex.base.baseandroidcompose.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dex.base.baseandroidcompose.ui.screens.WeatherHomeScreen
import com.dex.base.baseandroidcompose.ui.screens.WeatherDetailScreen
import com.dex.base.baseandroidcompose.ui.screens.UserProfileScreen
import com.dex.base.baseandroidcompose.ui.screens.RewardScreen
import com.dex.base.baseandroidcompose.ui.viewmodels.UserViewModel
import com.dex.base.baseandroidcompose.ui.viewmodels.WeatherViewModel
import com.dex.base.baseandroidcompose.data.models.UserLevel
import com.dex.base.baseandroidcompose.data.models.UserProfile

/**
 * Main Navigation Graph for the Weather App
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationRoutes.HOME
) {
    val weatherViewModel: WeatherViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    
    Scaffold(
        bottomBar = {
            WeatherBottomNavigation(navController = navController)
        },
        floatingActionButton = {
            WeatherFAB(
                onClick = {
                    weatherViewModel.refreshWeather()
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            // Home Screen
            composable(
                route = NavigationRoutes.HOME,
                enterTransition = {
                    scaleIn(
                        initialScale = 0.9f,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    scaleOut(
                        targetScale = 0.9f,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                WeatherHomeScreen(
                    viewModel = weatherViewModel,
                    onNavigateToDetail = {
                        navController.navigate(NavigationRoutes.WEATHER_DETAIL)
                    }
                )
            }

            
            // Rewards Screen
            composable(
                route = NavigationRoutes.REWARDS,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                RewardScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            
            // Profile Screen
            composable(
                route = NavigationRoutes.PROFILE,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                }
            ) {
                UserProfileScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onProfileSaved = {
                        // Navigate back after saving profile
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

/**
 * Rewards Screen Placeholder
 */

/**
 * Profile Screen Placeholder
 */

/**
 * Enhanced Navigation Graph with Deep Links
 */