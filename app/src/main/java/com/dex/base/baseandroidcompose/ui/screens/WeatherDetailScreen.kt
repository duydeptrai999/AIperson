package com.dex.base.baseandroidcompose.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dex.base.baseandroidcompose.data.models.*
import com.dex.base.baseandroidcompose.ui.viewmodels.WeatherViewModel
import com.dex.base.baseandroidcompose.ui.theme.*

/**
 * Get color based on compatibility score
 */
internal fun getScoreColor(score: Float): Color {
    return when {
        score >= 80f -> Color(0xFF4CAF50) // Green
        score >= 60f -> Color(0xFFFF9800) // Orange
        score >= 40f -> Color(0xFFFF5722) // Red-Orange
        else -> Color(0xFFF44336) // Red
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailScreen(
    onBackClick: () -> Unit = {},
    viewModel: WeatherViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chi tiết thời tiết",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Lỗi: ${uiState.error}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.refreshWeather() }
                    ) {
                        Text("Thử lại")
                    }
                }
            }
        } else if (uiState.weatherData != null && uiState.compatibility != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Weather Overview Card
                uiState.weatherData?.let { weatherData ->
                    uiState.compatibility?.let { compatibility ->
                        item {
                            WeatherOverviewCard(
                                weatherData = weatherData,
                                compatibility = compatibility
                            )
                        }
                    }
                }
                
                // Compatibility Analysis Card
                uiState.compatibility?.let { compatibility ->
                    item {
                        userProfile?.let { profile ->
                            CompatibilityAnalysisCard(
                                compatibility = compatibility,
                                userProfile = profile
                            )
                        }
                    }
                }
                
                // Detailed Factors Card
                uiState.compatibility?.let { compatibility ->
                    item {
                        DetailedFactorsCard(
                            factors = compatibility.factors
                        )
                    }
                }
                
                // Recommendations Card
                uiState.compatibility?.let { compatibility ->
                    item {
                        RecommendationsCard(
                            recommendations = compatibility.recommendations
                        )
                    }
                }
                
                // Weather Metrics Card
                uiState.weatherData?.let { weatherData ->
                    item {
                        WeatherMetricsCard(
                            weatherData = weatherData
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherOverviewCard(
    weatherData: WeatherData,
    compatibility: WeatherCompatibility
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            getScoreColor(compatibility.compatibilityScore).copy(alpha = 0.1f),
                            getScoreColor(compatibility.compatibilityScore).copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = weatherData.location,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = weatherData.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Text(
                        text = "${weatherData.temperature.toInt()}°C",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = getScoreColor(compatibility.compatibilityScore)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherInfoItem(
                        icon = Icons.Default.Thermostat,
                        label = "Cảm giác",
                        value = "${weatherData.feelsLike.toInt()}°C"
                    )
                    WeatherInfoItem(
                        icon = Icons.Default.Water,
                        label = "Độ ẩm",
                        value = "${weatherData.humidity}%"
                    )
                    WeatherInfoItem(
                        icon = Icons.Default.Air,
                        label = "Gió",
                        value = "${weatherData.windSpeed.toInt()} m/s"
                    )
                }
            }
        }
    }
}

@Composable
fun CompatibilityAnalysisCard(
    compatibility: WeatherCompatibility,
    userProfile: UserProfile
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = getScoreColor(compatibility.compatibilityScore),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Phân tích AI",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Compatibility Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Điểm tương thích",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${compatibility.compatibilityScore.toInt()}/100",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = getScoreColor(compatibility.compatibilityScore)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = compatibility.compatibilityScore / 100f,
                modifier = Modifier.fillMaxWidth(),
                color = getScoreColor(compatibility.compatibilityScore),
                        trackColor = getScoreColor(compatibility.compatibilityScore).copy(alpha = 0.2f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Points Earned
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Điểm kiếm được",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "+${compatibility.pointsEarned}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Reasoning
            Text(
                text = "Lý do:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            compatibility.reasoning.forEach { reason ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun DetailedFactorsCard(
    factors: CompatibilityFactors
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Chi tiết các yếu tố",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val factorsList = listOf(
                "Nhiệt độ" to factors.temperatureScore,
                "Độ ẩm" to factors.humidityScore,
                "Gió" to factors.windScore,
                "Tầm nhìn" to factors.visibilityScore,
                "Áp suất" to factors.pressureScore,
                "Thoải mái" to factors.comfortScore,
                "Sức khỏe" to factors.healthScore,
                "Hoạt động" to factors.activityScore,
                "Nghề nghiệp" to factors.occupationScore,
                "Độ tuổi" to factors.ageScore
            )
            
            factorsList.forEach { (name, score) ->
                FactorScoreItem(
                    name = name,
                    score = score
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FactorScoreItem(
    name: String,
    score: Float
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${score.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = getScoreColor(score)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = score / 100f,
            modifier = Modifier.fillMaxWidth(),
            color = getScoreColor(score),
            trackColor = getScoreColor(score).copy(alpha = 0.2f)
        )
    }
}

@Composable
fun RecommendationsCard(
    recommendations: List<Recommendation>
) {
    if (recommendations.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gợi ý",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                recommendations.forEach { recommendation ->
                    RecommendationItem(recommendation = recommendation)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun RecommendationItem(
    recommendation: Recommendation
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (recommendation.priority) {
                RecommendationPriority.HIGH -> MaterialTheme.colorScheme.errorContainer
                RecommendationPriority.MEDIUM -> MaterialTheme.colorScheme.primaryContainer
                RecommendationPriority.LOW -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = recommendation.icon,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = recommendation.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = recommendation.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun WeatherMetricsCard(
    weatherData: WeatherData
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Thông số chi tiết",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(
                    icon = Icons.Default.Refresh,
                    label = "Tầm nhìn",
                    value = "${weatherData.visibility / 1000} km"
                )
                MetricItem(
                    icon = Icons.Default.Star,
                    label = "Áp suất",
                    value = "${weatherData.pressure} hPa"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(
                    icon = Icons.Default.LocationOn,
                    label = "Tầm nhìn",
                    value = "${weatherData.visibility / 1000}km"
                )
                MetricItem(
                    icon = Icons.Default.Person,
                    label = "Mây che",
                    value = "${weatherData.cloudiness}%"
                )
            }
        }
    }
}

@Composable
fun WeatherInfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MetricItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}