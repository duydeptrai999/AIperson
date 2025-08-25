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
            
            // Weather Detail Screen
            composable(
                route = NavigationRoutes.WEATHER_DETAIL,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) {
                WeatherDetailScreen(
                    viewModel = weatherViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            
            // Rewards Screen (Placeholder)
            composable(
                route = NavigationRoutes.REWARDS,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                }
            ) {
                RewardsScreen(
                    viewModel = weatherViewModel
                )
            }
            
            // Profile Screen (Placeholder)
            composable(
                route = NavigationRoutes.PROFILE,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                }
            ) {
                ProfileScreen(
                    viewModel = weatherViewModel
                )
            }
        }
    }
}

/**
 * Rewards Screen Placeholder
 */
@Composable
fun RewardsScreen(
    viewModel: WeatherViewModel
) {
    val userProfile by viewModel.userProfile.collectAsState()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Phần thưởng",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Điểm hiện tại",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${userProfile.pointBalance} điểm",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Cấp độ: ${UserLevel.getLevelFromPoints(userProfile.totalPointsEarned).title}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    LinearProgressIndicator(
                        progress = UserLevel.getProgressToNextLevel(userProfile.totalPointsEarned),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Phần thưởng sẽ được triển khai trong phiên bản tiếp theo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Profile Screen Placeholder
 */
@Composable
fun ProfileScreen(
    viewModel: WeatherViewModel
) {
    val userProfile by viewModel.userProfile.collectAsState()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Hồ sơ cá nhân",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Thông tin cá nhân",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Tuổi: ${userProfile.age}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "Nghề nghiệp: ${userProfile.occupation.displayName}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "Vị trí: ${userProfile.location.city}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Chỉnh sửa hồ sơ sẽ được triển khai trong phiên bản tiếp theo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Enhanced Navigation Graph with Deep Links
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedWeatherNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationRoutes.HOME
) {
    val weatherViewModel: WeatherViewModel = hiltViewModel()
    var fabExpanded by remember { mutableStateOf(false) }
    
    Scaffold(
        bottomBar = {
            EnhancedWeatherBottomNavigation(
                navController = navController,
                notificationCounts = mapOf(
                    NavigationRoutes.REWARDS to 3,
                    NavigationRoutes.PROFILE to 1
                )
            )
        },
        floatingActionButton = {
            WeatherFAB(
                onClick = {
                    weatherViewModel.refreshWeather()
                },
                isExpanded = fabExpanded
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable(NavigationRoutes.HOME) {
                LaunchedEffect(Unit) {
                    fabExpanded = true
                }
                WeatherHomeScreen(
                    viewModel = weatherViewModel,
                    onNavigateToDetail = {
                        navController.navigate(NavigationRoutes.WEATHER_DETAIL)
                    }
                )
            }
            
            composable(NavigationRoutes.WEATHER_DETAIL) {
                LaunchedEffect(Unit) {
                    fabExpanded = false
                }
                WeatherDetailScreen(
                    viewModel = weatherViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(NavigationRoutes.REWARDS) {
                LaunchedEffect(Unit) {
                    fabExpanded = false
                }
                RewardsScreen(viewModel = weatherViewModel)
            }
            
            composable(NavigationRoutes.PROFILE) {
                LaunchedEffect(Unit) {
                    fabExpanded = false
                }
                ProfileScreen(viewModel = weatherViewModel)
            }
        }
    }
}