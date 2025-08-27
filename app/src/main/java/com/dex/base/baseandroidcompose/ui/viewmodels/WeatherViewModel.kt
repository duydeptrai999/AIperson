package com.dex.base.baseandroidcompose.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dex.base.baseandroidcompose.data.ai.WeatherCompatibilityEngine
import com.dex.base.baseandroidcompose.data.models.*
import com.dex.base.baseandroidcompose.data.repository.WeatherRepository
import com.dex.base.baseandroidcompose.data.repository.UserRepository
import com.dex.base.baseandroidcompose.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val userRepository: UserRepository,
    private val compatibilityEngine: WeatherCompatibilityEngine
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    
    init {
        loadUserProfile()
        // Listen to user profile changes for auto-update
        observeUserProfileChanges()
        // Weather data will be loaded when user profile is available
        // If no user profile, load default location
    }
    
    fun loadWeatherData(city: String) {
        Logger.d("Loading weather data for city: $city")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            Logger.d("UI State updated - Loading: true")
            
            try {
                val weatherResult = weatherRepository.getCurrentWeatherByCity(city)
                Logger.d("Weather repository result: ${if (weatherResult.isSuccess) "SUCCESS" else "FAILURE"}")
                
                weatherResult.fold(
                    onSuccess = { weatherData ->
                        Logger.d("WeatherViewModel: Weather data received successfully: ${weatherData.location}")
                        Logger.d("Weather data received successfully: $weatherData")
                        
                        val userProfile = _userProfile.value
                        val compatibility = if (userProfile != null) {
                            compatibilityEngine.calculateCompatibility(
                                weatherData = weatherData,
                                userProfile = userProfile
                            )
                        } else {
                            null
                        }
                        Logger.d("Compatibility calculated: $compatibility")
                
                        val insights = if (userProfile != null && compatibility != null) {
                            // Update user points
                            val updatedProfile = userProfile.copy(
                                pointBalance = userProfile.pointBalance + compatibility.pointsEarned,
                                totalPointsEarned = userProfile.totalPointsEarned + compatibility.pointsEarned
                            )
                            _userProfile.value = updatedProfile
                            Logger.d("User profile updated with points: ${compatibility.pointsEarned}")
                            
                            // Generate daily insights
                            generateDailyInsights(weatherData, compatibility, updatedProfile)
                        } else {
                            null
                        }
                        Logger.d("Daily insights generated: $insights")
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            weatherData = weatherData,
                            compatibility = compatibility,
                            dailyInsights = insights,
                            error = null
                        )
                        Logger.d("WeatherViewModel: UI state updated successfully, isLoading = false")
                        Logger.d("UI State updated with weather data successfully")
                    },
                    onFailure = { exception ->
                        val errorMsg = exception.message ?: "Failed to load weather data"
                        Logger.e("Weather data loading failed: $errorMsg")
                        Logger.e("Exception details: ${exception.stackTraceToString()}")
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = errorMsg
                        )
                        Logger.d("UI State updated with error: $errorMsg")
                    }
                )
                
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Unknown error occurred"
                Logger.e("Exception in loadWeatherData: $errorMsg")
                Logger.e("Exception stack trace: ${e.stackTraceToString()}")
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMsg
                )
                Logger.d("UI State updated with exception error: $errorMsg")
            }
        }
    }
    
    fun loadWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            Logger.d("WeatherViewModel: Starting loadWeatherByCoordinates for lat=$lat, lon=$lon")
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            Logger.d("WeatherViewModel: Set isLoading = true")
            
            try {
                val weatherResult = weatherRepository.getCurrentWeatherByCoordinates(lat, lon)
                Logger.d("WeatherViewModel: Got weather result from repository")
                weatherResult.fold(
                    onSuccess = { weatherData ->
                        val userProfile = _userProfile.value
                        val compatibility = if (userProfile != null) {
                            compatibilityEngine.calculateCompatibility(
                                weatherData = weatherData,
                                userProfile = userProfile
                            )
                        } else {
                            null
                        }
                        
                        val insights = if (userProfile != null && compatibility != null) {
                            // Update user points
                            val updatedProfile = userProfile.copy(
                                pointBalance = userProfile.pointBalance + compatibility.pointsEarned,
                                totalPointsEarned = userProfile.totalPointsEarned + compatibility.pointsEarned
                            )
                            _userProfile.value = updatedProfile
                            
                            // Generate daily insights
                            generateDailyInsights(weatherData, compatibility, updatedProfile)
                        } else {
                            null
                        }
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            weatherData = weatherData,
                            compatibility = compatibility,
                            dailyInsights = insights,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        Logger.e("WeatherViewModel: Failed to load weather data: ${exception.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load weather data"
                        )
                        Logger.d("WeatherViewModel: UI state updated with error, isLoading = false")
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
    
    /**
     * Observe user profile changes for auto-update
     */
    private fun observeUserProfileChanges() {
        viewModelScope.launch {
            userRepository.userProfile.collect { profile ->
                val previousProfile = _userProfile.value
                _userProfile.value = profile
                
                // Check if location has changed
                val locationChanged = previousProfile?.location != profile?.location
                
                if (locationChanged && profile != null) {
                    Logger.d("User location changed, reloading weather data")
                    loadWeatherDataForProfile(profile)
                }
            }
        }
    }
    
    /**
     * Load user profile from repository (initial load)
     */
    private fun loadUserProfile() {
        viewModelScope.launch {
            // Get current profile value for initial load
            val profile = userRepository.getCurrentUserProfile()
            _userProfile.value = profile
            
            // Load weather data based on user location
            if (profile != null) {
                loadWeatherDataForProfile(profile)
            } else {
                // Load default location if no user profile
                Logger.d("No user profile found, using default location: Hanoi")
                loadWeatherData("Hanoi")
            }
        }
    }
    
    /**
     * Load weather data based on user profile location
     */
    private fun loadWeatherDataForProfile(profile: UserProfile) {
        Logger.d("User profile found: city=${profile.location.city}, lat=${profile.location.latitude}, lon=${profile.location.longitude}")
        // Check if we have valid coordinates or use city name
        if (profile.location.latitude != 0.0 && profile.location.longitude != 0.0) {
            // Load weather by coordinates if available
            Logger.d("Loading weather by coordinates: lat=${profile.location.latitude}, lon=${profile.location.longitude}")
            loadWeatherByCoordinates(
                lat = profile.location.latitude,
                lon = profile.location.longitude
            )
        } else if (profile.location.city.isNotBlank()) {
            // Load weather by city name if coordinates are not available
            Logger.d("Loading weather by city name: ${profile.location.city}")
            loadWeatherData(profile.location.city)
        } else {
            // Fallback to default location
            Logger.d("No valid location data, using default: Hanoi")
            loadWeatherData("Hanoi")
        }
        
        // If we have weather data and profile, recalculate compatibility
        val currentWeatherData = _uiState.value.weatherData
        if (currentWeatherData != null) {
            calculateAndUpdateCompatibility(currentWeatherData, profile)
        }
    }
    
    /**
     * Save user profile to repository
     */
    fun saveUserProfile(userProfile: UserProfile) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val result = userRepository.saveUserProfile(userProfile)
                
                if (result.isSuccess) {
                    Logger.d("User profile saved successfully")
                    
                    // Recalculate compatibility with new profile
                    val currentWeatherData = _uiState.value.weatherData
                    if (currentWeatherData != null) {
                        calculateAndUpdateCompatibility(currentWeatherData, userProfile)
                    }
                } else {
                    Logger.e("Failed to save user profile: ${result.exceptionOrNull()?.message}")
                }
                
            } catch (e: Exception) {
                Logger.e("Error saving user profile: ${e.message}")
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
    
    /**
     * Update user profile (legacy method for compatibility)
     */
    fun updateUserProfile(newProfile: UserProfile) {
        saveUserProfile(newProfile)
    }
    
    /**
     * Calculate and update compatibility
     */
    private fun calculateAndUpdateCompatibility(weatherData: WeatherData, userProfile: UserProfile) {
        val compatibility = compatibilityEngine.calculateCompatibility(
            weatherData = weatherData,
            userProfile = userProfile
        )
        
        val updatedProfile = userProfile.copy(
            pointBalance = userProfile.pointBalance + compatibility.pointsEarned,
            totalPointsEarned = userProfile.totalPointsEarned + compatibility.pointsEarned
        )
        
        // Save updated profile with new points
        viewModelScope.launch {
            userRepository.saveUserProfile(updatedProfile)
        }
        
        val dailyInsights = generateDailyInsights(
            weatherData = weatherData,
            compatibility = compatibility,
            userProfile = updatedProfile
        )
        
        _uiState.value = _uiState.value.copy(
            compatibility = compatibility,
            dailyInsights = dailyInsights
        )
    }
    
    /**
     * Get current user profile or create default one
     */
    fun getCurrentUserProfileOrDefault(): UserProfile {
        return _userProfile.value ?: UserProfile.getDefaultProfile()
    }
    
    /**
     * Check if user has completed profile setup
     */
    fun hasUserProfile(): Boolean {
        return userRepository.hasUserProfile()
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