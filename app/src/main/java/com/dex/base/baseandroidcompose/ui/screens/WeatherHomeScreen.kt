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
                    aiHealthAdvice = uiState.aiHealthAdvice,
                    isLoading = uiState.isLoadingHealthAdvice,
                    error = uiState.healthAdviceError,
                    pointsScale = pointsScale,
                    onRefresh = { viewModel.refreshHealthAdvice() }
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
                val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                val (greeting, greetingIcon) = when (currentHour) {
                    in 5..11 -> "Good Morning!" to "🌅"
                    in 12..17 -> "Good Afternoon!" to "☀️"
                    in 18..21 -> "Good Evening!" to "🌇"
                    else -> "Good Night!" to "🌙"
                }
                
                Text(
                    text = "$greeting $greetingIcon",
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
                    
                    // Weather icon with time-based logic
                    val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                    val weatherIcon = when {
                        weatherData.description.contains("thunderstorm", ignoreCase = true) -> "⛈️"
                        weatherData.description.contains("drizzle", ignoreCase = true) -> "🌦️"
                        weatherData.description.contains("rain", ignoreCase = true) -> {
                            when {
                                weatherData.description.contains("heavy", ignoreCase = true) -> "🌧️"
                                weatherData.description.contains("light", ignoreCase = true) -> "🌦️"
                                else -> "🌧️"
                            }
                        }
                        weatherData.description.contains("snow", ignoreCase = true) -> {
                            when {
                                weatherData.description.contains("heavy", ignoreCase = true) -> "❄️"
                                weatherData.description.contains("light", ignoreCase = true) -> "🌨️"
                                else -> "❄️"
                            }
                        }
                        weatherData.description.contains("mist", ignoreCase = true) || 
                        weatherData.description.contains("fog", ignoreCase = true) -> "🌫️"
                        weatherData.description.contains("haze", ignoreCase = true) -> "😶‍🌫️"
                        weatherData.description.contains("dust", ignoreCase = true) || 
                        weatherData.description.contains("sand", ignoreCase = true) -> "🌪️"
                        weatherData.description.contains("clear", ignoreCase = true) -> {
                            when (currentHour) {
                                in 6..11 -> "🌅"  // Morning
                                in 12..17 -> "☀️"  // Afternoon
                                in 18..19 -> "🌇"  // Evening
                                else -> "🌙"       // Night
                            }
                        }
                        weatherData.description.contains("cloud", ignoreCase = true) -> {
                            when {
                                weatherData.description.contains("few", ignoreCase = true) -> {
                                    when (currentHour) {
                                        in 6..18 -> "🌤️"  // Partly cloudy day
                                        else -> "☁️"       // Cloudy night
                                    }
                                }
                                weatherData.description.contains("scattered", ignoreCase = true) -> "⛅"
                                weatherData.description.contains("broken", ignoreCase = true) || 
                                weatherData.description.contains("overcast", ignoreCase = true) -> "☁️"
                                else -> "☁️"
                            }
                        }
                        else -> {
                            when (currentHour) {
                                in 6..18 -> "🌤️"  // Default day
                                else -> "🌙"       // Default night
                            }
                        }
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
fun getHealthColor(score: Int): Color {
    return when {
        score >= 8 -> Color(0xFF4CAF50) // Green
        score >= 6 -> Color(0xFFFF9800) // Orange
        score >= 4 -> Color(0xFFFF5722) // Red-Orange
        else -> Color(0xFFF44336) // Red
    }
}

@Composable
fun HealthAdviceCard(
    aiHealthAdvice: AIHealthAdvice?,
    isLoading: Boolean,
    error: String?,
    pointsScale: Float,
    onRefresh: () -> Unit
) {
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(pointsScale)
            .clickable { if (error != null) onRefresh() },
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏥",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Column {
                        Text(
                            text = "Lời khuyên sức khỏe hôm nay",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        val currentTime = java.text.SimpleDateFormat("HH:mm dd/MM", java.util.Locale.getDefault()).format(java.util.Date())
                        Text(
                            text = "Cập nhật lúc $currentTime",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            when {
                isLoading -> {
                    // Loading state
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                error != null -> {
                    // Error state
                    Column {
                        Text(
                            text = "Không thể tải lời khuyên sức khỏe",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Nhấn để thử lại",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
                
                aiHealthAdvice != null -> {
                    // Success state with AI data
                    Column {
                        // Status and Assessment Score
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = aiHealthAdvice.statusMessage,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = getHealthColor(aiHealthAdvice.assessmentScore)
                                )
                                Text(
                                    text = aiHealthAdvice.assessmentLevel,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            
                            // Assessment Score Badge
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = getHealthColor(aiHealthAdvice.assessmentScore).copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "${aiHealthAdvice.assessmentScore}/10",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = getHealthColor(aiHealthAdvice.assessmentScore),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Health Analysis
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📊",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Phân tích tình trạng sức khỏe",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = aiHealthAdvice.healthAnalysis,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                        
                        if (aiHealthAdvice.recommendations.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Recommendations with specific categories
                            val shouldDoRecommendations = aiHealthAdvice.recommendations.filter { 
                                it.title.contains("nên làm", ignoreCase = true) || 
                                it.title.contains("Hoạt động nên làm", ignoreCase = true)
                            }
                            val shouldAvoidRecommendations = aiHealthAdvice.recommendations.filter { 
                                it.title.contains("cần tránh", ignoreCase = true) || 
                                it.title.contains("Hoạt động cần tránh", ignoreCase = true)
                            }
                            val endOfDayRecommendations = aiHealthAdvice.recommendations.filter { 
                                it.title.contains("cuối ngày", ignoreCase = true) || 
                                it.title.contains("Kế hoạch từ giờ đến cuối ngày", ignoreCase = true)
                            }
                            
                            // Should Do Activities
                            if (shouldDoRecommendations.isNotEmpty()) {
                                RecommendationSection(
                                    icon = "✅",
                                    title = "Hoạt động nên làm",
                                    recommendations = shouldDoRecommendations,
                                    backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.1f),
                                    titleColor = Color(0xFF4CAF50)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            
                            // Should Avoid Activities
                            if (shouldAvoidRecommendations.isNotEmpty()) {
                                RecommendationSection(
                                    icon = "⚠️",
                                    title = "Hoạt động cần tránh",
                                    recommendations = shouldAvoidRecommendations,
                                    backgroundColor = Color(0xFFFF9800).copy(alpha = 0.1f),
                                    titleColor = Color(0xFFFF9800)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            
                            // End of Day Plan
                            if (endOfDayRecommendations.isNotEmpty()) {
                                RecommendationSection(
                                    icon = "🌅",
                                    title = "Kế hoạch từ giờ đến cuối ngày",
                                    recommendations = endOfDayRecommendations,
                                    backgroundColor = Color(0xFF2196F3).copy(alpha = 0.1f),
                                    titleColor = Color(0xFF2196F3)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            
                            // Other recommendations (fallback)
                            val otherRecommendations = aiHealthAdvice.recommendations.filter { recommendation ->
                                !shouldDoRecommendations.contains(recommendation) &&
                                !shouldAvoidRecommendations.contains(recommendation) &&
                                !endOfDayRecommendations.contains(recommendation)
                            }
                            
                            if (otherRecommendations.isNotEmpty()) {
                                RecommendationSection(
                                    icon = "💡",
                                    title = "Khuyến nghị khác",
                                    recommendations = otherRecommendations,
                                    backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    titleColor = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        
                        // Nutritional Advice
                        if (aiHealthAdvice.nutritionalAdvice.isNotBlank()) {
                            AdviceSection(
                                icon = "🥗",
                                title = "Dinh dưỡng hôm nay",
                                content = aiHealthAdvice.nutritionalAdvice,
                                backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        // Workout Tips
                        if (aiHealthAdvice.workoutTips.isNotBlank()) {
                            AdviceSection(
                                icon = "💪",
                                title = "Vận động hôm nay",
                                content = aiHealthAdvice.workoutTips,
                                backgroundColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
                
                else -> {
                    // Default state
                    Text(
                        text = "Đang chuẩn bị lời khuyên sức khỏe cho bạn...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendationSection(
    icon: String,
    title: String,
    recommendations: List<HealthRecommendation>,
    backgroundColor: Color,
    titleColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            recommendations.forEach { recommendation ->
                Text(
                    text = "• ${recommendation.content}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AdviceSection(
    icon: String,
    title: String,
    content: String,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
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