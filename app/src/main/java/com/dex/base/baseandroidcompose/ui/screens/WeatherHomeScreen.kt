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
import androidx.compose.ui.text.font.FontStyle
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
    var isNewPointsEarned by remember { mutableStateOf(false) }
    
    val pointsScale by animateFloatAsState(
        targetValue = if (isNewPointsEarned) 1.1f else 1f,
        animationSpec = tween(200),
        finishedListener = { isNewPointsEarned = false }
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SkyBlue.copy(alpha = 0.2f),
                        CloudWhite
                    )
                )
            )
    ) {
        // Compact Top Bar
        CompactTopBar(
            userProfile = userProfile,
            onRefresh = {
                isNewPointsEarned = true
                viewModel.refreshWeather()
            }
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Combined Location & Weather Card
            item {
                when {
                    uiState.isLoading -> CompactLoadingCard()
                    uiState.error != null -> CompactErrorCard(
                        error = uiState.error ?: "Unknown error",
                        onRetry = { viewModel.refreshWeather() }
                    )
                    else -> CompactWeatherCard(
                        weatherData = uiState.weatherData,
                        userProfile = userProfile,
                        onNavigateToDetails = onNavigateToDetail
                    )
                }
            }
            
            // Health Advice Card
            item {
                HealthAdviceCard(
                    weatherData = uiState.weatherData,
                    pointsScale = pointsScale
                )
            }
        }
    }
}





@Composable
fun CompactTopBar(
    userProfile: UserProfile?,
    onRefresh: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good Morning! 🌤️",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = DeepSkyBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = userProfile?.let {
                            if (it.location.country.isNotBlank()) {
                                "${it.location.city}, ${it.location.country}"
                            } else it.location.city
                        } ?: "Ho Chi Minh City, Vietnam",
                        style = MaterialTheme.typography.bodySmall,
                        color = RainyGray
                    )
                }
            }
            
            Row {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CompatibilityGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CompactErrorCard(
    error: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WeatherCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.error_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = RainyGray
                )
            }
            Button(
                onClick = onRetry,
                modifier = Modifier.height(32.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.retry),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun CompactLoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WeatherCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.loading),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.getting_weather_data),
                    style = MaterialTheme.typography.bodySmall,
                    color = RainyGray
                )
            }
        }
    }
}

@Composable
fun CompactWeatherCard(
    weatherData: WeatherData?,
    userProfile: UserProfile?,
    onNavigateToDetails: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToDetails() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WeatherCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (weatherData != null) {
                // Main weather row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${weatherData.temperature.roundToInt()}°C",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = weatherData.description.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyMedium,
                            color = RainyGray
                        )
                    }
                    
                    // Weather icon
                    val weatherIcon = when {
                        weatherData.description.contains("clear", ignoreCase = true) -> "☀️"
                        weatherData.description.contains("cloud", ignoreCase = true) -> "☁️"
                        weatherData.description.contains("rain", ignoreCase = true) -> "🌧️"
                        weatherData.description.contains("snow", ignoreCase = true) -> "❄️"
                        weatherData.description.contains("mist", ignoreCase = true) || 
                        weatherData.description.contains("fog", ignoreCase = true) -> "🌫️"
                        weatherData.description.contains("thunderstorm", ignoreCase = true) -> "⛈️"
                        else -> "🌤️"
                    }
                    
                    Text(
                        text = weatherIcon,
                        fontSize = 48.sp,
                        modifier = Modifier.size(60.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Compact weather details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CompactWeatherDetail(
                        label = "Humidity",
                        value = "${weatherData.humidity}%",
                        icon = "💧"
                    )
                    CompactWeatherDetail(
                        label = "Wind",
                        value = "${weatherData.windSpeed.roundToInt()}m/s",
                        icon = "💨"
                    )
                    CompactWeatherDetail(
                        label = "Feels",
                        value = "${weatherData.feelsLike.roundToInt()}°C",
                        icon = "🌡️"
                    )
                    CompactWeatherDetail(
                        label = "Visibility",
                        value = "${(weatherData.visibility / 1000.0).roundToInt()}km",
                        icon = "👁️"
                    )
                }
            } else {
                Text(
                    text = "No weather data available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RainyGray,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun CompactWeatherDetail(
    label: String,
    value: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = RainyGray,
            fontSize = 10.sp
        )
    }
}

@Composable
fun HealthAdviceCard(
    weatherData: WeatherData?,
    pointsScale: Float
) {
    // Generate health advice based on weather conditions
    val healthAdvice = generateHealthAdvice(weatherData)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(pointsScale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = WeatherCardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = healthAdvice.icon,
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "AI Health Advice",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Health advice content
            Text(
                text = healthAdvice.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = healthAdvice.color
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = healthAdvice.advice,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Additional tips
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Tip",
                    tint = healthAdvice.color,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                     text = healthAdvice.tip,
                     style = MaterialTheme.typography.bodySmall,
                     color = RainyGray,
                     fontStyle = FontStyle.Italic
                 )
            }
        }
    }
}

data class HealthAdvice(
    val icon: String,
    val title: String,
    val advice: String,
    val tip: String,
    val color: Color
)

fun generateHealthAdvice(weatherData: WeatherData?): HealthAdvice {
    if (weatherData == null) {
        return HealthAdvice(
            icon = "🏥",
            title = "Stay Healthy",
            advice = "Keep yourself hydrated and maintain a balanced diet for optimal health.",
            tip = "Regular exercise boosts immunity",
            color = CompatibilityGreen
        )
    }
    
    val temp = weatherData.temperature
    val humidity = weatherData.humidity
    val description = weatherData.description.lowercase()
    
    return when {
        temp > 35 -> HealthAdvice(
            icon = "🌡️",
            title = "Heat Warning",
            advice = "Very hot weather! Stay indoors during peak hours, drink plenty of water, and wear light-colored clothing.",
            tip = "Avoid outdoor activities between 10 AM - 4 PM",
            color = Color(0xFFE57373)
        )
        
        temp > 30 -> HealthAdvice(
            icon = "☀️",
            title = "Hot Weather Care",
            advice = "Hot day ahead! Stay hydrated, use sunscreen, and take breaks in shade when outdoors.",
            tip = "Drink water every 15-20 minutes when active",
            color = Color(0xFFFF9800)
        )
        
        temp < 10 -> HealthAdvice(
            icon = "🧥",
            title = "Cold Weather Protection",
            advice = "Cold weather! Dress in layers, protect extremities, and stay warm to prevent hypothermia.",
            tip = "Warm up before going outside",
            color = DeepSkyBlue
        )
        
        humidity > 80 -> HealthAdvice(
            icon = "💧",
            title = "High Humidity Alert",
            advice = "High humidity can make you feel hotter. Stay in air-conditioned spaces and avoid strenuous activities.",
            tip = "Use dehumidifier indoors if possible",
            color = Color(0xFF42A5F5)
        )
        
        description.contains("rain") -> HealthAdvice(
            icon = "☔",
            title = "Rainy Day Health",
            advice = "Rainy weather! Stay dry, boost your mood with indoor activities, and be careful of slippery surfaces.",
            tip = "Vitamin D supplement may help on cloudy days",
            color = Color(0xFF5C6BC0)
        )
        
        description.contains("clear") && temp >= 20 && temp <= 28 -> HealthAdvice(
            icon = "🌞",
            title = "Perfect Weather",
            advice = "Ideal weather conditions! Great time for outdoor activities, exercise, and fresh air.",
            tip = "Perfect day for a walk or outdoor workout",
            color = CompatibilityGreen
        )
        
        else -> HealthAdvice(
            icon = "🌤️",
            title = "General Wellness",
            advice = "Moderate weather conditions. Maintain regular exercise, balanced diet, and adequate sleep.",
            tip = "Listen to your body and adjust activities accordingly",
            color = SunYellow
        )
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