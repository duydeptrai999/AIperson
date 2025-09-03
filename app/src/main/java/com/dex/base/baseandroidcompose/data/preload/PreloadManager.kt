package com.dex.base.baseandroidcompose.data.preload

import com.dex.base.baseandroidcompose.data.models.UserProfile
import com.dex.base.baseandroidcompose.data.models.WeatherData
import com.dex.base.baseandroidcompose.data.models.AIHealthAdvice
import com.dex.base.baseandroidcompose.data.repository.WeatherRepository
import com.dex.base.baseandroidcompose.data.repository.UserRepository
import com.dex.base.baseandroidcompose.data.api.AIHealthRepository
import com.dex.base.baseandroidcompose.data.ai.WeatherCompatibilityEngine
import com.dex.base.baseandroidcompose.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager để preload dữ liệu thời tiết và AI health advice
 * Được sử dụng trong splash screen để giảm thời gian chờ ở màn chính
 */
@Singleton
class PreloadManager @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val userRepository: UserRepository,
    private val aiHealthRepository: AIHealthRepository,
    private val compatibilityEngine: WeatherCompatibilityEngine
) {
    
    private val preloadScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Flags để track trạng thái preload
    private val isPreloadingWeather = AtomicBoolean(false)
    private val isPreloadingHealthAdvice = AtomicBoolean(false)
    
    // State flows để theo dõi kết quả preload
    private val _preloadState = MutableStateFlow(PreloadState())
    val preloadState: StateFlow<PreloadState> = _preloadState.asStateFlow()
    
    // Cache dữ liệu đã preload
    private var cachedWeatherData: WeatherData? = null
    private var cachedHealthAdvice: AIHealthAdvice? = null
    private var cachedUserProfile: UserProfile? = null
    
    /**
     * Bắt đầu preload tất cả dữ liệu cần thiết
     */
    fun startPreloading() {
        Logger.d("PreloadManager: Starting preload process")
        
        preloadScope.launch {
            try {
                _preloadState.value = _preloadState.value.copy(isLoading = true)
                
                // Load user profile trước
                val userProfile = userRepository.getCurrentUserProfile()
                cachedUserProfile = userProfile
                
                if (userProfile != null) {
                    Logger.d("PreloadManager: User profile found, starting weather and health advice preload")
                    
                    // Preload weather và health advice song song
                    val weatherDeferred = async { preloadWeatherData(userProfile) }
                    val healthAdviceDeferred = async { 
                        // Chờ weather data trước khi load health advice
                        val weatherData = weatherDeferred.await()
                        if (weatherData != null) {
                            preloadHealthAdvice(weatherData, userProfile)
                        } else null
                    }
                    
                    // Chờ cả hai hoàn thành
                    val weatherData = weatherDeferred.await()
                    val healthAdvice = healthAdviceDeferred.await()
                    
                    _preloadState.value = _preloadState.value.copy(
                        isLoading = false,
                        weatherDataReady = weatherData != null,
                        healthAdviceReady = healthAdvice != null,
                        userProfileReady = true
                    )
                    
                    Logger.d("PreloadManager: Preload completed - Weather: ${weatherData != null}, Health: ${healthAdvice != null}")
                } else {
                    Logger.d("PreloadManager: No user profile, preloading default weather data")
                    
                    // Nếu không có user profile, load weather mặc định cho Hanoi
                    val weatherData = preloadDefaultWeatherData()
                    
                    _preloadState.value = _preloadState.value.copy(
                        isLoading = false,
                        weatherDataReady = weatherData != null,
                        healthAdviceReady = false,
                        userProfileReady = false
                    )
                }
                
            } catch (e: Exception) {
                Logger.e("PreloadManager: Error during preload: ${e.message}")
                _preloadState.value = _preloadState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    /**
     * Preload weather data dựa trên user profile
     */
    private suspend fun preloadWeatherData(userProfile: UserProfile): WeatherData? {
        if (!isPreloadingWeather.compareAndSet(false, true)) {
            Logger.d("PreloadManager: Weather preload already in progress")
            return cachedWeatherData
        }
        
        return try {
            Logger.d("PreloadManager: Preloading weather data for ${userProfile.location.city}")
            
            val result = if (userProfile.location.latitude != 0.0 && userProfile.location.longitude != 0.0) {
                weatherRepository.getCurrentWeatherByCoordinates(
                    latitude = userProfile.location.latitude,
                    longitude = userProfile.location.longitude
                )
            } else {
                weatherRepository.getCurrentWeatherByCity(userProfile.location.city)
            }
            
            result.fold(
                onSuccess = { weatherData ->
                    Logger.d("PreloadManager: Weather data preloaded successfully")
                    cachedWeatherData = weatherData
                    weatherData
                },
                onFailure = { exception ->
                    Logger.e("PreloadManager: Failed to preload weather data: ${exception.message}")
                    null
                }
            )
        } catch (e: Exception) {
            Logger.e("PreloadManager: Exception preloading weather: ${e.message}")
            null
        } finally {
            isPreloadingWeather.set(false)
        }
    }
    
    /**
     * Preload weather data mặc định cho Hanoi
     */
    private suspend fun preloadDefaultWeatherData(): WeatherData? {
        if (!isPreloadingWeather.compareAndSet(false, true)) {
            return cachedWeatherData
        }
        
        return try {
            Logger.d("PreloadManager: Preloading default weather data for Hanoi")
            
            val result = weatherRepository.getCurrentWeatherByCity("Hanoi")
            
            result.fold(
                onSuccess = { weatherData ->
                    Logger.d("PreloadManager: Default weather data preloaded successfully")
                    cachedWeatherData = weatherData
                    weatherData
                },
                onFailure = { exception ->
                    Logger.e("PreloadManager: Failed to preload default weather data: ${exception.message}")
                    null
                }
            )
        } catch (e: Exception) {
            Logger.e("PreloadManager: Exception preloading default weather: ${e.message}")
            null
        } finally {
            isPreloadingWeather.set(false)
        }
    }
    
    /**
     * Preload AI health advice
     */
    private suspend fun preloadHealthAdvice(weatherData: WeatherData, userProfile: UserProfile): AIHealthAdvice? {
        if (!isPreloadingHealthAdvice.compareAndSet(false, true)) {
            Logger.d("PreloadManager: Health advice preload already in progress")
            return cachedHealthAdvice
        }
        
        return try {
            Logger.d("PreloadManager: Preloading AI health advice")
            
            val result = aiHealthRepository.getHealthAdvice(
                weatherData = weatherData,
                userProfile = userProfile,
                conversationId = null
            )
            
            result.fold(
                onSuccess = { healthAdvice ->
                    Logger.d("PreloadManager: AI health advice preloaded successfully")
                    cachedHealthAdvice = healthAdvice
                    healthAdvice
                },
                onFailure = { exception ->
                    Logger.e("PreloadManager: Failed to preload AI health advice: ${exception.message}")
                    null
                }
            )
        } catch (e: Exception) {
            Logger.e("PreloadManager: Exception preloading health advice: ${e.message}")
            null
        } finally {
            isPreloadingHealthAdvice.set(false)
        }
    }
    
    /**
     * Lấy cached weather data
     */
    fun getCachedWeatherData(): WeatherData? = cachedWeatherData
    
    /**
     * Lấy cached health advice
     */
    fun getCachedHealthAdvice(): AIHealthAdvice? = cachedHealthAdvice
    
    /**
     * Lấy cached user profile
     */
    fun getCachedUserProfile(): UserProfile? = cachedUserProfile
    
    /**
     * Kiểm tra xem có dữ liệu cached không
     */
    fun hasPreloadedData(): Boolean {
        return cachedWeatherData != null
    }
    
    /**
     * Clear cache
     */
    fun clearCache() {
        Logger.d("PreloadManager: Clearing cache")
        cachedWeatherData = null
        cachedHealthAdvice = null
        cachedUserProfile = null
        _preloadState.value = PreloadState()
    }
    
    /**
     * Kiểm tra xem preload có đang chạy không
     */
    fun isPreloading(): Boolean {
        return _preloadState.value.isLoading
    }
}

/**
 * Data class để track trạng thái preload
 */
data class PreloadState(
    val isLoading: Boolean = false,
    val weatherDataReady: Boolean = false,
    val healthAdviceReady: Boolean = false,
    val userProfileReady: Boolean = false,
    val error: String? = null
) {
    val isCompleted: Boolean
        get() = !isLoading && (weatherDataReady || error != null)
        
    val isFullyReady: Boolean
        get() = !isLoading && weatherDataReady && (healthAdviceReady || !userProfileReady)
}