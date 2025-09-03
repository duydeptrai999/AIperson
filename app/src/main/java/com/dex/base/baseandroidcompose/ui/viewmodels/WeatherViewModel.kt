package com.dex.base.baseandroidcompose.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dex.base.baseandroidcompose.data.ai.WeatherCompatibilityEngine
import com.dex.base.baseandroidcompose.data.models.*
import com.dex.base.baseandroidcompose.data.repository.WeatherRepository
import com.dex.base.baseandroidcompose.data.repository.UserRepository
import com.dex.base.baseandroidcompose.data.api.AIHealthRepository
import com.dex.base.baseandroidcompose.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val userRepository: UserRepository,
    private val compatibilityEngine: WeatherCompatibilityEngine,
    private val aiHealthRepository: AIHealthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    
    // Flag to prevent duplicate AI health advice requests
    private val isLoadingAIHealthAdvice = AtomicBoolean(false)
    
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
                
                        // Update user points if compatibility exists
                        if (userProfile != null && compatibility != null) {
                            val updatedProfile = userProfile.copy(
                                pointBalance = userProfile.pointBalance + compatibility.pointsEarned,
                                totalPointsEarned = userProfile.totalPointsEarned + compatibility.pointsEarned
                            )
                            _userProfile.value = updatedProfile
                            Logger.d("User profile updated with points: ${compatibility.pointsEarned}")
                        }
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            weatherData = weatherData,
                            compatibility = compatibility,
                            error = null
                        )
                        
                        // Load AI health advice if user profile is available
                        if (userProfile != null) {
                            loadAIHealthAdvice(weatherData, userProfile)
                        }
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
                val weatherResult = weatherRepository.getCurrentWeatherByCoordinates(latitude = lat, longitude = lon)
                Logger.d("WeatherViewModel: Got weather result from repository")
                weatherResult.fold(
                    onSuccess = { weatherData ->
                        updateUIWithWeatherData(weatherData, null)
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
        viewModelScope.launch {
            Logger.d("Refreshing weather data...")
            
            // Refresh weather data based on current user profile
            val currentProfile = _userProfile.value
            if (currentProfile != null) {
                loadWeatherDataForProfile(currentProfile)
            } else {
                // Fallback to default location
                loadWeatherData("Hanoi")
            }
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
        
        _uiState.value = _uiState.value.copy(
            compatibility = compatibility
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
    

    
    /**
     * Load AI health advice based on weather and user profile
     */
    private fun loadAIHealthAdvice(weatherData: WeatherData, userProfile: UserProfile) {
        // Prevent duplicate requests using atomic compareAndSet
        if (!isLoadingAIHealthAdvice.compareAndSet(false, true)) {
            Logger.d("AI health advice request already in progress, skipping duplicate")
            return
        }
        
        Logger.d("Loading AI health advice for weather: ${weatherData.description}")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingHealthAdvice = true,
                healthAdviceError = null
            )
            
            try {
                val result = aiHealthRepository.getHealthAdvice(
                    weatherData = weatherData,
                    userProfile = userProfile,
                    conversationId = _uiState.value.aiHealthAdvice?.conversationId
                )
                
                result.fold(
                    onSuccess = { healthAdvice ->
                        Logger.d("AI Health advice loaded successfully")
                        _uiState.value = _uiState.value.copy(
                            isLoadingHealthAdvice = false,
                            aiHealthAdvice = healthAdvice,
                            healthAdviceError = null
                        )
                    },
                    onFailure = { exception ->
                        Logger.e("Failed to load AI health advice: ${exception.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoadingHealthAdvice = false,
                            healthAdviceError = exception.message ?: "Failed to load health advice"
                        )
                    }
                )
            } catch (e: Exception) {
                Logger.e("Exception loading AI health advice: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoadingHealthAdvice = false,
                    healthAdviceError = e.message ?: "Unknown error occurred"
                )
            } finally {
                isLoadingAIHealthAdvice.set(false)
            }
        }
    }
    
    /**
     * Refresh AI health advice
     */
    fun refreshHealthAdvice() {
        viewModelScope.launch {
            Logger.d("Refreshing health advice...")
            
            val currentWeatherData = _uiState.value.weatherData
            val currentUserProfile = _userProfile.value
            
            if (currentWeatherData != null && currentUserProfile != null) {
                _uiState.value = _uiState.value.copy(isLoadingHealthAdvice = true)
                
                try {
                    val healthAdviceResult = aiHealthRepository.getHealthAdvice(
                        weatherData = currentWeatherData,
                        userProfile = currentUserProfile
                    )
                    
                    healthAdviceResult.fold(
                        onSuccess = { healthAdvice ->
                            _uiState.value = _uiState.value.copy(
                                aiHealthAdvice = healthAdvice,
                                isLoadingHealthAdvice = false,
                                healthAdviceError = null
                            )
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoadingHealthAdvice = false,
                                healthAdviceError = exception.message ?: "Failed to load health advice"
                            )
                        }
                    )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoadingHealthAdvice = false,
                        healthAdviceError = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }
    
    private fun updateUIWithWeatherData(weatherData: WeatherData, cachedHealthAdvice: AIHealthAdvice?) {
        Logger.d("Updating UI with weather data: ${weatherData.location}")
        
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
        
        // Update user points if compatibility exists
        if (userProfile != null && compatibility != null) {
            val updatedProfile = userProfile.copy(
                pointBalance = userProfile.pointBalance + compatibility.pointsEarned,
                totalPointsEarned = userProfile.totalPointsEarned + compatibility.pointsEarned
            )
            _userProfile.value = updatedProfile
            Logger.d("User profile updated with points: ${compatibility.pointsEarned}")
        }
        
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            weatherData = weatherData,
            compatibility = compatibility,
            aiHealthAdvice = cachedHealthAdvice,
            error = null
        )
        
        // Load AI health advice if not cached and user profile is available
        if (cachedHealthAdvice == null && userProfile != null) {
            loadAIHealthAdvice(weatherData, userProfile)
        }
        
        Logger.d("UI state updated successfully with cached/fresh data")
    }

}

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weatherData: WeatherData? = null,
    val compatibility: WeatherCompatibility? = null,
    val aiHealthAdvice: AIHealthAdvice? = null,
    val isLoadingHealthAdvice: Boolean = false,
    val healthAdviceError: String? = null,
    val error: String? = null
)