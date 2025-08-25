package com.dex.base.baseandroidcompose.data.api

import com.dex.base.baseandroidcompose.data.models.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * OpenWeatherMap API service interface
 */
interface WeatherApiService {
    
    /**
     * Get current weather by city name
     */
    @GET("weather")
    suspend fun getCurrentWeatherByCity(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "vi"
    ): Response<WeatherResponse>
    
    /**
     * Get current weather by coordinates
     */
    @GET("weather")
    suspend fun getCurrentWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "vi"
    ): Response<WeatherResponse>
    
    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        
        // You need to get your API key from https://openweathermap.org/api
        const val API_KEY = "YOUR_API_KEY_HERE" // Replace with actual API key
    }
}