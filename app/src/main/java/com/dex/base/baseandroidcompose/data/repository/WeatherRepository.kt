package com.dex.base.baseandroidcompose.data.repository

import com.dex.base.baseandroidcompose.data.api.NetworkModule
import com.dex.base.baseandroidcompose.data.api.WeatherApiService
import com.dex.base.baseandroidcompose.data.models.Location
import com.dex.base.baseandroidcompose.data.models.WeatherData
import com.dex.base.baseandroidcompose.data.models.WeatherResponse
import com.dex.base.baseandroidcompose.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * Repository for weather data with caching strategy
 */
class WeatherRepository {
    
    private val apiService: WeatherApiService = NetworkModule.provideWeatherApiService()
    private val cache = ConcurrentHashMap<String, CachedWeatherData>()
    
    // Cache duration: 2 hours (as per brainstorm requirements)
    private val cacheValidityDuration = 2 * 60 * 60 * 1000L // 2 hours in milliseconds
    
    /**
     * Get current weather by city name with caching
     */
    suspend fun getCurrentWeatherByCity(cityName: String): Result<WeatherData> {
        return withContext(Dispatchers.IO) {
            try {
                val cacheKey = "city_$cityName"
                
                // Check cache first
                val cachedData = cache[cacheKey]
                if (cachedData != null && isCacheValid(cachedData.timestamp)) {
                    return@withContext Result.success(cachedData.weatherData)
                }
                
                // Fetch from API
                Logger.d("Fetching weather data for city: $cityName")
                Logger.d("Sending city name to API: '$cityName'")
                Logger.d("API URL parameters - cityName: $cityName, apiKey: ${WeatherApiService.API_KEY.take(8)}..., units: metric, lang: vi")
                
                val response = apiService.getCurrentWeatherByCity(
                    cityName = cityName,
                    apiKey = WeatherApiService.API_KEY
                )
                
                Logger.d("API Response - Code: ${response.code()}, Success: ${response.isSuccessful}")
                
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    Logger.d("Raw API Response: $responseBody")
                    
                    val weatherData = WeatherData.fromWeatherResponse(responseBody)
                    Logger.d("Converted WeatherData: $weatherData")
                    
                    // Cache the result
                    cache[cacheKey] = CachedWeatherData(
                        weatherData = weatherData,
                        timestamp = System.currentTimeMillis()
                    )
                    
                    Logger.d("Weather data cached successfully for $cityName")
                    Result.success(weatherData)
                } else {
                    val errorMsg = "API Error: ${response.code()} - ${response.message()}"
                    Logger.e(errorMsg)
                    if (response.errorBody() != null) {
                        Logger.e("Error body: ${response.errorBody()?.string()}")
                    }
                    Result.failure(Exception(errorMsg))
                }
                
            } catch (e: Exception) {
                // Return cached data if available, even if expired
                val cacheKey = "city_$cityName"
                val cachedData = cache[cacheKey]
                if (cachedData != null) {
                    Result.success(cachedData.weatherData)
                } else {
                    Result.failure(e)
                }
            }
        }
    }
    
    /**
     * Get current weather by coordinates with caching
     */
    suspend fun getCurrentWeatherByCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<WeatherData> {
        return withContext(Dispatchers.IO) {
            try {
                val cacheKey = "coords_${latitude}_$longitude"
                
                // Check cache first
                val cachedData = cache[cacheKey]
                if (cachedData != null && isCacheValid(cachedData.timestamp)) {
                    return@withContext Result.success(cachedData.weatherData)
                }
                
                // Fetch from API
                val response = apiService.getCurrentWeatherByCoordinates(
                    latitude = latitude,
                    longitude = longitude,
                    apiKey = WeatherApiService.API_KEY
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val weatherData = WeatherData.fromWeatherResponse(response.body()!!)
                    
                    // Cache the result
                    cache[cacheKey] = CachedWeatherData(
                        weatherData = weatherData,
                        timestamp = System.currentTimeMillis()
                    )
                    
                    Result.success(weatherData)
                } else {
                    Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
                }
                
            } catch (e: Exception) {
                // Return cached data if available, even if expired
                val cacheKey = "coords_${latitude}_$longitude"
                val cachedData = cache[cacheKey]
                if (cachedData != null) {
                    Result.success(cachedData.weatherData)
                } else {
                    Result.failure(e)
                }
            }
        }
    }
    
    /**
     * Get weather for user's location
     */
    suspend fun getWeatherForLocation(location: Location): Result<WeatherData> {
        return getCurrentWeatherByCoordinates(location.latitude, location.longitude)
    }
    
    /**
     * Force refresh weather data (ignore cache)
     */
    suspend fun refreshWeatherData(location: Location): Result<WeatherData> {
        val cacheKey = "coords_${location.latitude}_${location.longitude}"
        cache.remove(cacheKey) // Remove from cache to force refresh
        return getWeatherForLocation(location)
    }
    
    /**
     * Clear all cached data
     */
    fun clearCache() {
        cache.clear()
    }
    
    /**
     * Get cached weather data if available
     */
    fun getCachedWeatherData(location: Location): WeatherData? {
        val cacheKey = "coords_${location.latitude}_${location.longitude}"
        return cache[cacheKey]?.weatherData
    }
    
    /**
     * Check if cached data is still valid
     */
    private fun isCacheValid(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp < cacheValidityDuration
    }
    
    /**
     * Get cache status for debugging
     */
    fun getCacheInfo(): Map<String, Any> {
        return mapOf(
            "cacheSize" to cache.size,
            "validEntries" to cache.values.count { isCacheValid(it.timestamp) },
            "expiredEntries" to cache.values.count { !isCacheValid(it.timestamp) }
        )
    }
    
    /**
     * Clean expired cache entries
     */
    fun cleanExpiredCache() {
        val expiredKeys = cache.entries
            .filter { !isCacheValid(it.value.timestamp) }
            .map { it.key }
        
        expiredKeys.forEach { cache.remove(it) }
    }
}

/**
 * Cached weather data with timestamp
 */
private data class CachedWeatherData(
    val weatherData: WeatherData,
    val timestamp: Long
)