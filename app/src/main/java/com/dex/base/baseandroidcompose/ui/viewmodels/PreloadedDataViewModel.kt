package com.dex.base.baseandroidcompose.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dex.base.baseandroidcompose.data.models.UserProfile
import com.dex.base.baseandroidcompose.data.models.WeatherData
import com.dex.base.baseandroidcompose.data.models.AIHealthAdvice
import com.dex.base.baseandroidcompose.data.preload.PreloadManager
import com.dex.base.baseandroidcompose.data.repository.WeatherRepository
import com.dex.base.baseandroidcompose.data.repository.UserRepository
import com.dex.base.baseandroidcompose.data.api.AIHealthRepository
import com.dex.base.baseandroidcompose.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel để quản lý dữ liệu đã được preload
 * Cung cấp interface để truy cập cached data từ PreloadManager
 */
@HiltViewModel
class PreloadedDataViewModel @Inject constructor(
    private val preloadManager: PreloadManager,
    private val weatherRepository: WeatherRepository,
    private val userRepository: UserRepository,
    private val aiHealthRepository: AIHealthRepository
) : ViewModel() {
    
    private val _weatherData = MutableStateFlow<WeatherData?>(null)
    val weatherData: StateFlow<WeatherData?> = _weatherData.asStateFlow()
    
    private val _healthAdvice = MutableStateFlow<AIHealthAdvice?>(null)
    val healthAdvice: StateFlow<AIHealthAdvice?> = _healthAdvice.asStateFlow()
    
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadPreloadedData()
    }
    
    /**
     * Load dữ liệu từ PreloadManager cache
     */
    private fun loadPreloadedData() {
        Logger.d("PreloadedDataViewModel: Loading preloaded data")
        
        viewModelScope.launch {
            try {
                // Lấy cached data từ PreloadManager
                val cachedWeather = preloadManager.getCachedWeatherData()
                val cachedHealth = preloadManager.getCachedHealthAdvice()
                val cachedUser = preloadManager.getCachedUserProfile()
                
                if (cachedWeather != null) {
                    Logger.d("PreloadedDataViewModel: Found cached weather data")
                    _weatherData.value = cachedWeather
                } else {
                    Logger.d("PreloadedDataViewModel: No cached weather data, loading fresh")
                    loadFreshWeatherData()
                }
                
                if (cachedHealth != null) {
                    Logger.d("PreloadedDataViewModel: Found cached health advice")
                    _healthAdvice.value = cachedHealth
                }
                
                if (cachedUser != null) {
                    Logger.d("PreloadedDataViewModel: Found cached user profile")
                    _userProfile.value = cachedUser
                } else {
                    Logger.d("PreloadedDataViewModel: No cached user profile, loading fresh")
                    loadFreshUserProfile()
                }
                
            } catch (e: Exception) {
                Logger.e("PreloadedDataViewModel: Error loading preloaded data: ${e.message}")
                _error.value = e.message
            }
        }
    }
    
    /**
     * Load fresh weather data nếu không có cached data
     */
    private suspend fun loadFreshWeatherData() {
        _isLoading.value = true
        
        try {
            val userProfile = _userProfile.value ?: userRepository.getCurrentUserProfile()
            
            val result = if (userProfile != null && userProfile.location.latitude != 0.0) {
                weatherRepository.getCurrentWeatherByCoordinates(
                    latitude = userProfile.location.latitude,
                    longitude = userProfile.location.longitude
                )
            } else {
                weatherRepository.getCurrentWeatherByCity("Hanoi")
            }
            
            result.fold(
                onSuccess = { weatherData ->
                    Logger.d("PreloadedDataViewModel: Fresh weather data loaded successfully")
                    _weatherData.value = weatherData
                },
                onFailure = { exception ->
                    Logger.e("PreloadedDataViewModel: Failed to load fresh weather data: ${exception.message}")
                    _error.value = exception.message
                }
            )
        } catch (e: Exception) {
            Logger.e("PreloadedDataViewModel: Exception loading fresh weather: ${e.message}")
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * Load fresh user profile nếu không có cached data
     */
    private suspend fun loadFreshUserProfile() {
        try {
            val userProfile = userRepository.getCurrentUserProfile()
            if (userProfile != null) {
                Logger.d("PreloadedDataViewModel: Fresh user profile loaded successfully")
                _userProfile.value = userProfile
            }
        } catch (e: Exception) {
            Logger.e("PreloadedDataViewModel: Exception loading fresh user profile: ${e.message}")
        }
    }
    
    /**
     * Refresh weather data
     */
    fun refreshWeatherData() {
        Logger.d("PreloadedDataViewModel: Refreshing weather data")
        viewModelScope.launch {
            loadFreshWeatherData()
        }
    }
    
    /**
     * Load health advice cho weather data hiện tại
     */
    fun loadHealthAdviceForCurrentWeather() {
        Logger.d("PreloadedDataViewModel: Loading health advice for current weather")
        
        viewModelScope.launch {
            val currentWeather = _weatherData.value
            val currentUser = _userProfile.value
            
            if (currentWeather != null && currentUser != null) {
                _isLoading.value = true
                
                try {
                    val result = aiHealthRepository.getHealthAdvice(
                        weatherData = currentWeather,
                        userProfile = currentUser,
                        conversationId = null
                    )
                    
                    result.fold(
                        onSuccess = { healthAdvice ->
                            Logger.d("PreloadedDataViewModel: Health advice loaded successfully")
                            _healthAdvice.value = healthAdvice
                        },
                        onFailure = { exception ->
                            Logger.e("PreloadedDataViewModel: Failed to load health advice: ${exception.message}")
                            _error.value = exception.message
                        }
                    )
                } catch (e: Exception) {
                    Logger.e("PreloadedDataViewModel: Exception loading health advice: ${e.message}")
                    _error.value = e.message
                } finally {
                    _isLoading.value = false
                }
            } else {
                Logger.w("PreloadedDataViewModel: Cannot load health advice - missing weather data or user profile")
                _error.value = "Missing weather data or user profile"
            }
        }
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Kiểm tra xem có dữ liệu preloaded không
     */
    fun hasPreloadedData(): Boolean {
        return _weatherData.value != null
    }
    
    /**
     * Get preload status từ PreloadManager
     */
    fun getPreloadState() = preloadManager.preloadState
    
    /**
     * Clear tất cả cached data
     */
    fun clearAllData() {
        Logger.d("PreloadedDataViewModel: Clearing all data")
        _weatherData.value = null
        _healthAdvice.value = null
        _userProfile.value = null
        _error.value = null
        preloadManager.clearCache()
    }
}