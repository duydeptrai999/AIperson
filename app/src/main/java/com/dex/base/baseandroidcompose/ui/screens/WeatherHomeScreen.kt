package com.dex.base.baseandroidcompose.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dex.base.baseandroidcompose.R
import com.dex.base.baseandroidcompose.ui.theme.*
import com.dex.base.baseandroidcompose.ui.viewmodels.WeatherViewModel
import com.dex.base.baseandroidcompose.data.models.*
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherHomeScreen(
    viewModel: WeatherViewModel,
    onNavigateToDetail: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current
    
    var isRefreshing by remember { mutableStateOf(false) }
    var isNewPointsEarned by remember { mutableStateOf(false) }
    var currentWeatherScore by remember { mutableStateOf(uiState.compatibility?.compatibilityScore ?: 0f) }
    var totalPoints by remember { mutableStateOf(userProfile.totalPointsEarned) }
    var dailyPointsEarned by remember { mutableStateOf(0) }
    
    // Animation for points
    val pointsScale by animateFloatAsState(
        targetValue = if (isNewPointsEarned) 1.2f else 1f,
        animationSpec = tween(300),
        finishedListener = { isNewPointsEarned = false }
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SkyBlue.copy(alpha = 0.3f),
                        CloudWhite
                    )
                )
            )
    ) {
        // Top App Bar with AI Personalization
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Good Morning! 🌤️",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Perfect weather for ${userProfile.occupation.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = RainyGray
                    )
                }
            },
            actions = {
                // Daily notification indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(CompatibilityGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(onClick = { /* Profile navigation handled by bottom nav */ }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.profile_content_desc),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(
                    onClick = {
                        isRefreshing = true
                        // Simulate AI recalculation
                        currentWeatherScore = (75..95).random().toFloat()
                        dailyPointsEarned = when {
                            currentWeatherScore >= 90 -> 10
                            currentWeatherScore >= 70 -> 7
                            currentWeatherScore >= 50 -> 5
                            currentWeatherScore >= 30 -> 3
                            else -> 1
                        }
                        totalPoints += dailyPointsEarned
                        isNewPointsEarned = true
                        isRefreshing = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh AI Analysis",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Daily AI Insights Card (New Priority Feature)
            item {
                val compatibility = uiState.compatibility
                if (compatibility != null) {
                    DailyAIInsightsCard(
                        score = compatibility.compatibilityScore,
                        pointsEarned = compatibility.pointsEarned,
                        userAge = userProfile.age,
                        userOccupation = userProfile.occupation.displayName
                    )
                }
            }
            
            // Location Card
            item {
                LocationCard()
            }
            
            // Current Weather Card
            item {
                CurrentWeatherCard(
                    weatherData = uiState.weatherData,
                    onNavigateToDetails = onNavigateToDetail
                )
            }
            
            // Enhanced Compatibility Score Card
            item {
                val compatibility = uiState.compatibility
                if (compatibility != null) {
                    EnhancedCompatibilityScoreCard(
                        score = compatibility.compatibilityScore,
                        userAge = userProfile.age,
                        userOccupation = userProfile.occupation.displayName,
                        reasoning = compatibility.reasoning.joinToString("\n"),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Enhanced Points Display Card
            item {
                EnhancedPointsDisplayCard(
                    totalPoints = userProfile.pointBalance,
                    dailyPointsEarned = uiState.compatibility?.pointsEarned ?: 0,
                    isNewPointsEarned = isNewPointsEarned,
                    animatedPoints = userProfile.pointBalance.toFloat(),
                    onNavigateToRewards = { /* Rewards navigation handled by bottom nav */ }
                )
            }
            

        }
    }
}

@Composable
fun DailyAIInsightsCard(
    score: Float,
    pointsEarned: Int,
    userAge: Int,
    userOccupation: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🤖 AI Daily Insights",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Personalized for you",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    RewardGold,
                                    RewardGold.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+$pointsEarned",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // AI Analysis based on user profile
            val analysisText = when {
                userAge < 25 && score > 80 -> "Perfect weather for outdoor activities! Great for your active lifestyle."
                userOccupation.contains("Developer", ignoreCase = true) && score > 75 -> "Ideal conditions for your commute to work. Low humidity won't affect your devices."
                userAge > 50 && score > 70 -> "Comfortable weather conditions. Perfect for your daily routine."
                score < 50 -> "Weather might be challenging today. Consider indoor activities."
                else -> "Good weather conditions for your daily activities."
            }
            
            Text(
                text = analysisText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Quick stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickStatItem(
                    icon = "🎯",
                    label = "Match",
                    value = "${score.roundToInt()}%"
                )
                QuickStatItem(
                    icon = "👤",
                    label = "Age",
                    value = "$userAge"
                )
                QuickStatItem(
                    icon = "💼",
                    label = "Profile",
                    value = userOccupation.take(8)
                )
            }
        }
    }
}

@Composable
fun QuickStatItem(
    icon: String,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun LocationCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = WeatherCardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = DeepSkyBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = stringResource(R.string.current_location),
                    style = MaterialTheme.typography.bodyMedium,
                    color = RainyGray
                )
                Text(
                    text = "Ho Chi Minh City, Vietnam", // TODO: Get from location service
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun CurrentWeatherCard(
    weatherData: WeatherData?,
    onNavigateToDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = WeatherCardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        onClick = onNavigateToDetails
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (weatherData != null) {
                // Enhanced weather icon based on description with Vietnamese support
                val weatherIcon = when {
                    weatherData.description.contains("clear", ignoreCase = true) || 
                    weatherData.description.contains("trời quang", ignoreCase = true) -> "☀️"
                    weatherData.description.contains("cloud", ignoreCase = true) || 
                    weatherData.description.contains("mây", ignoreCase = true) -> "☁️"
                    weatherData.description.contains("rain", ignoreCase = true) || 
                    weatherData.description.contains("mưa", ignoreCase = true) -> when {
                        weatherData.description.contains("heavy", ignoreCase = true) || 
                        weatherData.description.contains("cường độ nặng", ignoreCase = true) -> "🌧️"
                        weatherData.description.contains("light", ignoreCase = true) || 
                        weatherData.description.contains("nhẹ", ignoreCase = true) -> "🌦️"
                        else -> "🌧️"
                    }
                    weatherData.description.contains("snow", ignoreCase = true) || 
                    weatherData.description.contains("tuyết", ignoreCase = true) -> "❄️"
                    weatherData.description.contains("mist", ignoreCase = true) || 
                    weatherData.description.contains("fog", ignoreCase = true) || 
                    weatherData.description.contains("sương mù", ignoreCase = true) -> "🌫️"
                    weatherData.description.contains("thunderstorm", ignoreCase = true) || 
                    weatherData.description.contains("dông", ignoreCase = true) -> "⛈️"
                    else -> "🌤️"
                }
                
                val iconColor = when {
                    weatherData.description.contains("clear", ignoreCase = true) || 
                    weatherData.description.contains("trời quang", ignoreCase = true) -> SunYellow
                    weatherData.description.contains("cloud", ignoreCase = true) || 
                    weatherData.description.contains("mây", ignoreCase = true) -> Color.Gray
                    weatherData.description.contains("rain", ignoreCase = true) || 
                    weatherData.description.contains("mưa", ignoreCase = true) -> DeepSkyBlue
                    weatherData.description.contains("thunderstorm", ignoreCase = true) || 
                    weatherData.description.contains("dông", ignoreCase = true) -> Color(0xFF4A148C)
                    else -> SunYellow
                }
                
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(40.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(iconColor, iconColor.copy(alpha = 0.7f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = weatherIcon,
                        fontSize = 40.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Temperature from API
                Text(
                    text = "${weatherData.temperature.roundToInt()}°C",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Weather Description from API
                Text(
                    text = weatherData.description.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    color = RainyGray
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Enhanced Weather Details with more comprehensive information
                Column {
                    // Primary weather details row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WeatherDetailItem(
                            label = stringResource(R.string.humidity),
                            value = "${weatherData.humidity}%",
                            icon = "💧"
                        )
                        WeatherDetailItem(
                            label = stringResource(R.string.wind_speed),
                            value = "${weatherData.windSpeed.roundToInt()} m/s",
                            icon = "💨"
                        )
                        WeatherDetailItem(
                            label = "Feels Like",
                            value = "${weatherData.feelsLike.roundToInt()}°C",
                            icon = "🌡️"
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Secondary weather details row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WeatherDetailItem(
                            label = "Pressure",
                            value = "${weatherData.pressure} hPa",
                            icon = "📊"
                        )
                        WeatherDetailItem(
                            label = "Visibility",
                            value = "${(weatherData.visibility / 1000.0).roundToInt()} km",
                            icon = "👁️"
                        )
                        WeatherDetailItem(
                            label = "Cloudiness",
                            value = "${weatherData.cloudiness}%",
                            icon = "☁️"
                        )
                    }
                }
            } else {
                // Loading state
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(40.dp))
                        .background(Color.Gray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = DeepSkyBlue
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Text(
                    text = "Getting weather data",
                    style = MaterialTheme.typography.titleMedium,
                    color = RainyGray
                )
            }
        }
    }
}

@Composable
fun WeatherDetailItem(
    label: String,
    value: String,
    icon: String = ""
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        if (icon.isNotEmpty()) {
            Text(
                text = icon,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = RainyGray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EnhancedCompatibilityScoreCard(
    score: Float,
    userAge: Int,
    userOccupation: String,
    reasoning: String,
    modifier: Modifier = Modifier
) {
    val scoreColor = when {
        score >= 90 -> CompatibilityGreen
        score >= 70 -> SunYellow
        score >= 50 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFE57373) // Light Red
    }
    
    val scoreText = when {
        score >= 90 -> "Excellent Match"
        score >= 70 -> "Good Match"
        score >= 50 -> "Fair Match"
        else -> "Poor Match"
    }
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = WeatherCardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎯 AI Compatibility Score",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "Based on your profile",
                    style = MaterialTheme.typography.bodySmall,
                    color = RainyGray
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Enhanced Score Circle with animation
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    scoreColor,
                                    scoreColor.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${score.roundToInt()}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "%",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = scoreText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = scoreColor
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // AI reasoning from compatibility engine
                    Text(
                        text = reasoning,
                        style = MaterialTheme.typography.bodyMedium,
                        color = RainyGray,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Factors affecting score
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScoreFactorChip("Age: $userAge")
                        ScoreFactorChip(userOccupation.take(10))
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreFactorChip(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EnhancedPointsDisplayCard(
    totalPoints: Int,
    dailyPointsEarned: Int,
    isNewPointsEarned: Boolean,
    animatedPoints: Float,
    onNavigateToRewards: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isNewPointsEarned) 1.1f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "points_scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable { onNavigateToRewards() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isNewPointsEarned) 
                SunYellow.copy(alpha = 0.1f) 
            else 
                WeatherCardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isNewPointsEarned) 8.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with trophy and level indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Trophy",
                        tint = SunYellow,
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "🏆 Your Points",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Level badge
                val level = (totalPoints / 1000) + 1
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SunYellow.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Lv.$level",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = SunYellow,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Main points display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "${animatedPoints.roundToInt()}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = SunYellow
                    )
                    
                    Text(
                        text = "Total Points",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RainyGray
                    )
                }
                
                // Daily points earned indicator
                if (dailyPointsEarned > 0) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "+$dailyPointsEarned",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CompatibilityGreen
                            )
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Points earned",
                                tint = CompatibilityGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.bodySmall,
                            color = RainyGray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress to next level
            val level = (totalPoints / 1000) + 1
            val progressToNextLevel = (totalPoints % 1000) / 1000f
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress to Level ${level + 1}",
                        style = MaterialTheme.typography.bodySmall,
                        color = RainyGray
                    )
                    
                    Text(
                        text = "${totalPoints % 1000}/1000",
                        style = MaterialTheme.typography.bodySmall,
                        color = RainyGray
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                LinearProgressIndicator(
                    progress = progressToNextLevel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = SunYellow,
                    trackColor = SunYellow.copy(alpha = 0.2f)
                )
            }
        }
    }
}