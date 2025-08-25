package com.dex.base.baseandroidcompose.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dex.base.baseandroidcompose.data.ai.WeatherCompatibilityEngine
import com.dex.base.baseandroidcompose.data.models.*
import com.dex.base.baseandroidcompose.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val compatibilityEngine: WeatherCompatibilityEngine
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    private val _userProfile = MutableStateFlow(createMockUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()
    
    init {
        loadWeatherData("Ho Chi Minh City")
    }
    
    fun loadWeatherData(city: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val weatherResult = weatherRepository.getCurrentWeatherByCity(city)
                weatherResult.fold(
                    onSuccess = { weatherData ->
                        val compatibility = compatibilityEngine.calculateCompatibility(
                            weatherData = weatherData,
                            userProfile = _userProfile.value
                        )
                
                        // Update user points
                        val updatedProfile = _userProfile.value.copy(
                            pointBalance = _userProfile.value.pointBalance + compatibility.pointsEarned,
                            totalPointsEarned = _userProfile.value.totalPointsEarned + compatibility.pointsEarned
                        )
                        _userProfile.value = updatedProfile
                        
                        // Generate daily insights
                        val insights = generateDailyInsights(weatherData, compatibility, updatedProfile)
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            weatherData = weatherData,
                            compatibility = compatibility,
                            dailyInsights = insights,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load weather data"
                        )
                    }
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun loadWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val weatherResult = weatherRepository.getCurrentWeatherByCoordinates(lat, lon)
                weatherResult.fold(
                    onSuccess = { weatherData ->
                        val compatibility = compatibilityEngine.calculateCompatibility(
                            weatherData = weatherData,
                            userProfile = _userProfile.value
                        )
                        
                        // Update user points
                        val updatedProfile = _userProfile.value.copy(
                            pointBalance = _userProfile.value.pointBalance + compatibility.pointsEarned,
                            totalPointsEarned = _userProfile.value.totalPointsEarned + compatibility.pointsEarned
                        )
                        _userProfile.value = updatedProfile
                        
                        // Generate daily insights
                        val insights = generateDailyInsights(weatherData, compatibility, updatedProfile)
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            weatherData = weatherData,
                            compatibility = compatibility,
                            dailyInsights = insights,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load weather data"
                        )
                    }
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    fun refreshWeather() {
        val currentWeather = _uiState.value.weatherData
        if (currentWeather != null) {
            loadWeatherData(currentWeather.location)
        }
    }
    
    fun updateUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        // Recalculate compatibility with new profile
        val currentWeather = _uiState.value.weatherData
        if (currentWeather != null) {
            viewModelScope.launch {
                val compatibility = compatibilityEngine.calculateCompatibility(
                    weatherData = currentWeather,
                    userProfile = profile
                )
                
                val insights = generateDailyInsights(currentWeather, compatibility, profile)
                
                _uiState.value = _uiState.value.copy(
                    compatibility = compatibility,
                    dailyInsights = insights
                )
            }
        }
    }
    
    private fun generateDailyInsights(
        weatherData: WeatherData,
        compatibility: WeatherCompatibility,
        userProfile: UserProfile
    ): DailyAIInsights {
        val greeting = generatePersonalizedGreeting(userProfile, weatherData)
        val quickStats = generateQuickStats(weatherData, compatibility, userProfile)
        val aiAnalysis = generateAIAnalysis(compatibility)
        val achievements = generateAchievements(userProfile, compatibility)
        
        return DailyAIInsights(
            date = getCurrentDate(),
            greeting = greeting,
            weatherSummary = aiAnalysis,
            personalizedTips = generateInteractionElements(),
            healthAdvice = "Hãy uống đủ nước và giữ ấm cơ thể.",
            activitySuggestions = listOf("Đi bộ trong công viên", "Tập yoga ngoài trời", "Chụp ảnh phong cảnh"),
            pointsEarned = compatibility.pointsEarned,
            streakDays = 3, // Mock data
            achievements = achievements
        )
    }
    
    private fun generatePersonalizedGreeting(
        userProfile: UserProfile,
        weatherData: WeatherData
    ): String {
        val timeOfDay = getTimeOfDay()
        val weatherCondition = weatherData.description.lowercase()
        
        return when {
            weatherData.temperature > 30 -> "$timeOfDay! Hôm nay khá nóng (${weatherData.temperature.toInt()}°C), hãy giữ mát nhé!"
            weatherData.temperature < 15 -> "$timeOfDay! Trời hơi lạnh (${weatherData.temperature.toInt()}°C), nhớ mặc ấm!"
            weatherCondition.contains("rain") -> "$timeOfDay! Có mưa hôm nay, đừng quên ô!"
            weatherCondition.contains("sun") -> "$timeOfDay! Thời tiết đẹp, thích hợp đi dạo!"
            else -> "$timeOfDay! Chúc bạn một ngày tuyệt vời!"
        }
    }
    
    private fun generateQuickStats(
        weatherData: WeatherData,
        compatibility: WeatherCompatibility,
        userProfile: UserProfile
    ): QuickStats {
        return QuickStats(
            todayScore = compatibility.compatibilityScore,
            weeklyAverage = 85.5f, // Mock data
            pointsToday = compatibility.pointsEarned,
            totalPoints = userProfile.totalPointsEarned,
            currentStreak = 3, // Mock data
            bestStreak = 7 // Mock data
        )
    }
    
    private fun generateAIAnalysis(compatibility: WeatherCompatibility): String {
        val score = compatibility.compatibilityScore
        return when {
            score >= 80 -> "AI phân tích: Thời tiết hôm nay rất phù hợp với bạn! Điểm tương thích cao nhờ nhiệt độ và độ ẩm lý tưởng."
            score >= 60 -> "AI phân tích: Thời tiết khá ổn, một số yếu tố có thể ảnh hưởng nhẹ đến hoạt động của bạn."
            score >= 40 -> "AI phân tích: Thời tiết không hoàn toàn phù hợp, hãy chuẩn bị kỹ trước khi ra ngoài."
            else -> "AI phân tích: Thời tiết khó khăn hôm nay, nên hạn chế hoạt động ngoài trời."
        }
    }
    
    private fun generateAchievements(
        userProfile: UserProfile,
        compatibility: WeatherCompatibility
    ): List<Achievement> {
        val achievements = mutableListOf<Achievement>()
        
        // Check for high compatibility achievement
        if (compatibility.compatibilityScore >= 90) {
            achievements.add(
                Achievement(
                    id = "perfect_day",
                    title = "Ngày Hoàn Hảo",
                    description = "Đạt điểm tương thích 90+",
                    icon = "🌟",
                    pointsReward = 50,
                    unlockedAt = getCurrentTimestamp()
                )
            )
        }
        
        // Check for points milestone
        if (userProfile.totalPointsEarned >= 1000) {
            achievements.add(
                Achievement(
                    id = "point_master",
                    title = "Bậc Thầy Điểm Số",
                    description = "Đạt 1000+ điểm tổng",
                    icon = "🏆",
                    pointsReward = 100,
                    unlockedAt = getCurrentTimestamp()
                )
            )
        }
        
        return achievements
    }
    
    private fun generateInteractionElements(): List<String> {
        return listOf(
            "Nhấn để xem chi tiết thời tiết",
            "Vuốt để làm mới dữ liệu",
            "Chạm vào điểm số để xem lịch sử"
        )
    }
    
    private fun getTimeOfDay(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Chào buổi sáng"
            hour < 18 -> "Chào buổi chiều"
            else -> "Chào buổi tối"
        }
    }
    
    private fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
    
    private fun getCurrentDate(): String {
        return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    }
    
    private fun createMockUserProfile(): UserProfile {
        return UserProfile(
            id = "user_001",
            age = 28,
            location = Location(
                city = "Ho Chi Minh City",
                country = "Vietnam",
                latitude = 10.8231,
                longitude = 106.6297,
                timezone = "Asia/Ho_Chi_Minh"
            ),
            occupation = Occupation.OFFICE_WORKER,
            preferences = WeatherPreferences(
                preferredTemperatureRange = TemperatureRange(22.0, 28.0),
                preferredHumidityRange = HumidityRange(40, 70),
                windSensitivity = 0.3f
            ),
            pointBalance = 750,
            totalPointsEarned = 2150,
            level = 5, // Level based on points
            createdAt = getCurrentTimestamp(),
            lastUpdated = getCurrentTimestamp()
        )
    }
}

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weatherData: WeatherData? = null,
    val compatibility: WeatherCompatibility? = null,
    val dailyInsights: DailyAIInsights? = null,
    val error: String? = null
)