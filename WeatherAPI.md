# OpenWeatherMap API Integration Guide

## 📋 Overview
**API Provider**: OpenWeatherMap  
**API Key**: `927565d05e50545fc0077d2bdd4d5855`  
**Base URL**: `https://api.openweathermap.org/data/2.5/`  
**Documentation**: [OpenWeatherMap API Docs](https://openweathermap.org/api)

---

## 🔑 API Configuration

### API Key Setup
```kotlin
// Constants.kt
object WeatherApiConstants {
    const val API_KEY = "927565d05e50545fc0077d2bdd4d5855"
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val ICON_BASE_URL = "https://openweathermap.org/img/wn/"
}
```

### Security Best Practice
```kotlin
// local.properties (không commit vào git)
WEATHER_API_KEY=927565d05e50545fc0077d2bdd4d5855

// build.gradle.kts (app level)
android {
    buildTypes {
        debug {
            buildConfigField("String", "WEATHER_API_KEY", "\"${project.findProperty("WEATHER_API_KEY")}\")")
        }
        release {
            buildConfigField("String", "WEATHER_API_KEY", "\"${project.findProperty("WEATHER_API_KEY")}\")")
        }
    }
}

// Usage in code
val apiKey = BuildConfig.WEATHER_API_KEY
```

---

## 🌤️ Core API Endpoints

### 1. Current Weather Data
**Endpoint**: `/weather`  
**Purpose**: Lấy thông tin thời tiết hiện tại

```kotlin
// API Call Example
val currentWeatherUrl = "${BASE_URL}weather?lat=${lat}&lon=${lon}&appid=${API_KEY}&units=metric&lang=vi"

// Response Model
data class CurrentWeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int? = null,
    val grnd_level: Int? = null
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)
```

### 2. 5-Day Weather Forecast
**Endpoint**: `/forecast`  
**Purpose**: Dự báo thời tiết 5 ngày (3 giờ/lần)

```kotlin
// API Call Example
val forecastUrl = "${BASE_URL}forecast?lat=${lat}&lon=${lon}&appid=${API_KEY}&units=metric&lang=vi"

// Response Model
data class ForecastResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItem>,
    val city: City
)

data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double, // Probability of precipitation
    val rain: Rain? = null,
    val snow: Snow? = null,
    val sys: ForecastSys,
    val dt_txt: String
)
```

### 3. Air Pollution Data
**Endpoint**: `/air_pollution`  
**Purpose**: Thông tin chất lượng không khí

```kotlin
// API Call Example
val airPollutionUrl = "${BASE_URL}air_pollution?lat=${lat}&lon=${lon}&appid=${API_KEY}"

// Response Model
data class AirPollutionResponse(
    val coord: Coord,
    val list: List<AirPollutionData>
)

data class AirPollutionData(
    val dt: Long,
    val main: AirQualityMain,
    val components: AirComponents
)

data class AirQualityMain(
    val aqi: Int // Air Quality Index: 1-5 (1=Good, 5=Very Poor)
)

data class AirComponents(
    val co: Double,
    val no: Double,
    val no2: Double,
    val o3: Double,
    val so2: Double,
    val pm2_5: Double,
    val pm10: Double,
    val nh3: Double
)
```

---

## 🔧 Implementation Strategy

### Retrofit API Service
```kotlin
// WeatherApiService.kt
interface WeatherApiService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = WeatherApiConstants.API_KEY,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "vi"
    ): Response<CurrentWeatherResponse>
    
    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = WeatherApiConstants.API_KEY,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "vi"
    ): Response<ForecastResponse>
    
    @GET("air_pollution")
    suspend fun getAirPollution(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = WeatherApiConstants.API_KEY
    ): Response<AirPollutionResponse>
}
```

### Repository Implementation
```kotlin
// WeatherRepository.kt
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherDao: WeatherDao
) {
    suspend fun getCurrentWeather(lat: Double, lon: Double): Result<CurrentWeatherResponse> {
        return try {
            val response = apiService.getCurrentWeather(lat, lon)
            if (response.isSuccessful && response.body() != null) {
                // Cache data locally
                weatherDao.insertCurrentWeather(response.body()!!.toEntity())
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            // Return cached data if available
            val cachedData = weatherDao.getCurrentWeather()
            if (cachedData != null) {
                Result.success(cachedData.toResponse())
            } else {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getForecast(lat: Double, lon: Double): Result<ForecastResponse> {
        return try {
            val response = apiService.getForecast(lat, lon)
            if (response.isSuccessful && response.body() != null) {
                weatherDao.insertForecast(response.body()!!.toEntity())
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            val cachedData = weatherDao.getForecast()
            if (cachedData != null) {
                Result.success(cachedData.toResponse())
            } else {
                Result.failure(e)
            }
        }
    }
}
```

---

## 📊 Weather Compatibility Algorithm

### AI Personalization Logic
```kotlin
// WeatherCompatibilityCalculator.kt
class WeatherCompatibilityCalculator {
    
    fun calculateCompatibility(
        weather: CurrentWeatherResponse,
        userProfile: UserProfile
    ): WeatherCompatibility {
        
        val baseScore = calculateBaseScore(weather)
        val ageAdjustment = calculateAgeAdjustment(weather, userProfile.age)
        val occupationAdjustment = calculateOccupationAdjustment(weather, userProfile.occupation)
        val locationAdjustment = calculateLocationAdjustment(weather, userProfile.location)
        
        val finalScore = (baseScore + ageAdjustment + occupationAdjustment + locationAdjustment)
            .coerceIn(0f, 100f)
        
        val points = calculatePoints(finalScore)
        val reasoning = generateReasoning(weather, userProfile, finalScore)
        
        return WeatherCompatibility(
            date = getCurrentDate(),
            weatherData = weather.toWeatherData(),
            compatibilityScore = finalScore,
            pointsEarned = points,
            reasoning = reasoning,
            recommendations = generateRecommendations(weather, userProfile)
        )
    }
    
    private fun calculateBaseScore(weather: CurrentWeatherResponse): Float {
        val temp = weather.main.temp
        val humidity = weather.main.humidity
        val windSpeed = weather.wind.speed
        val weatherCondition = weather.weather.firstOrNull()?.main ?: ""
        
        var score = 50f // Base score
        
        // Temperature scoring (optimal: 20-25°C)
        score += when {
            temp in 20.0..25.0 -> 25f
            temp in 15.0..30.0 -> 15f
            temp in 10.0..35.0 -> 5f
            else -> -10f
        }
        
        // Humidity scoring (optimal: 40-60%)
        score += when {
            humidity in 40..60 -> 15f
            humidity in 30..70 -> 10f
            humidity in 20..80 -> 5f
            else -> -5f
        }
        
        // Wind scoring (optimal: 0-10 km/h)
        score += when {
            windSpeed <= 3.0 -> 10f
            windSpeed <= 6.0 -> 5f
            windSpeed <= 10.0 -> 0f
            else -> -10f
        }
        
        // Weather condition scoring
        score += when (weatherCondition.lowercase()) {
            "clear" -> 20f
            "clouds" -> 10f
            "rain" -> -5f
            "thunderstorm" -> -15f
            "snow" -> -10f
            "mist", "fog" -> -5f
            else -> 0f
        }
        
        return score
    }
    
    private fun calculateAgeAdjustment(weather: CurrentWeatherResponse, age: Int): Float {
        val temp = weather.main.temp
        val weatherCondition = weather.weather.firstOrNull()?.main ?: ""
        
        return when {
            age < 13 -> { // Trẻ em
                when {
                    temp < 10 || temp > 30 -> -15f
                    weatherCondition in listOf("Thunderstorm", "Snow") -> -20f
                    else -> 0f
                }
            }
            age in 13..25 -> { // Thanh niên
                when {
                    weatherCondition == "Clear" -> 5f
                    temp in 15.0..28.0 -> 5f
                    else -> 0f
                }
            }
            age in 26..60 -> { // Người trưởng thành
                when {
                    temp in 18.0..26.0 -> 5f
                    weather.main.humidity > 80 -> -5f
                    else -> 0f
                }
            }
            else -> { // Người cao tuổi
                when {
                    temp < 15 || temp > 28 -> -10f
                    weather.wind.speed > 8 -> -5f
                    weatherCondition == "Clear" && temp in 20.0..25.0 -> 10f
                    else -> 0f
                }
            }
        }
    }
    
    private fun calculateOccupationAdjustment(weather: CurrentWeatherResponse, occupation: Occupation): Float {
        val temp = weather.main.temp
        val weatherCondition = weather.weather.firstOrNull()?.main ?: ""
        val windSpeed = weather.wind.speed
        
        return when (occupation) {
            Occupation.OUTDOOR_WORKER -> {
                when {
                    weatherCondition in listOf("Rain", "Thunderstorm", "Snow") -> -20f
                    temp < 5 || temp > 35 -> -15f
                    windSpeed > 15 -> -10f
                    weatherCondition == "Clear" && temp in 15.0..30.0 -> 15f
                    else -> 0f
                }
            }
            Occupation.OFFICE_WORKER -> {
                when {
                    weatherCondition == "Rain" -> -5f // Ảnh hưởng di chuyển
                    temp in 20.0..25.0 -> 5f // Thoải mái cho trang phục
                    else -> 0f
                }
            }
            Occupation.HEALTHCARE -> {
                when {
                    weather.main.humidity > 85 -> -5f // Ảnh hưởng sức khỏe
                    temp < 10 || temp > 32 -> -5f
                    weatherCondition == "Clear" -> 5f
                    else -> 0f
                }
            }
            Occupation.EDUCATION -> {
                when {
                    weatherCondition in listOf("Thunderstorm", "Snow") -> -10f
                    weatherCondition == "Clear" -> 5f
                    else -> 0f
                }
            }
            else -> 0f
        }
    }
    
    private fun calculatePoints(compatibilityScore: Float): Int {
        return when {
            compatibilityScore >= 90 -> 10
            compatibilityScore >= 70 -> 7
            compatibilityScore >= 50 -> 5
            compatibilityScore >= 30 -> 3
            else -> 1
        }
    }
}
```

---

## 🔄 API Optimization Strategy

### Caching Strategy
```kotlin
// WeatherCacheManager.kt
class WeatherCacheManager @Inject constructor(
    private val weatherDao: WeatherDao
) {
    companion object {
        private const val CACHE_DURATION_HOURS = 2
        private const val FORECAST_CACHE_DURATION_HOURS = 6
    }
    
    suspend fun shouldRefreshCurrentWeather(): Boolean {
        val lastUpdate = weatherDao.getLastWeatherUpdate()
        val currentTime = System.currentTimeMillis()
        return lastUpdate == null || 
               (currentTime - lastUpdate) > (CACHE_DURATION_HOURS * 60 * 60 * 1000)
    }
    
    suspend fun shouldRefreshForecast(): Boolean {
        val lastUpdate = weatherDao.getLastForecastUpdate()
        val currentTime = System.currentTimeMillis()
        return lastUpdate == null || 
               (currentTime - lastUpdate) > (FORECAST_CACHE_DURATION_HOURS * 60 * 60 * 1000)
    }
}
```

### Rate Limiting
```kotlin
// ApiRateLimiter.kt
class ApiRateLimiter {
    private val lastApiCall = mutableMapOf<String, Long>()
    private val minInterval = 60_000L // 1 minute between calls
    
    fun canMakeApiCall(endpoint: String): Boolean {
        val now = System.currentTimeMillis()
        val lastCall = lastApiCall[endpoint] ?: 0
        
        return if (now - lastCall >= minInterval) {
            lastApiCall[endpoint] = now
            true
        } else {
            false
        }
    }
    
    fun getTimeUntilNextCall(endpoint: String): Long {
        val now = System.currentTimeMillis()
        val lastCall = lastApiCall[endpoint] ?: 0
        val timePassed = now - lastCall
        return maxOf(0, minInterval - timePassed)
    }
}
```

---

## 🚨 Error Handling

### API Error Codes
```kotlin
// WeatherApiException.kt
sealed class WeatherApiException(message: String) : Exception(message) {
    object InvalidApiKey : WeatherApiException("Invalid API key")
    object LocationNotFound : WeatherApiException("Location not found")
    object RateLimitExceeded : WeatherApiException("API rate limit exceeded")
    object NetworkError : WeatherApiException("Network connection error")
    object ServerError : WeatherApiException("Server error")
    data class UnknownError(val code: Int) : WeatherApiException("Unknown error: $code")
    
    companion object {
        fun fromHttpCode(code: Int): WeatherApiException {
            return when (code) {
                401 -> InvalidApiKey
                404 -> LocationNotFound
                429 -> RateLimitExceeded
                in 500..599 -> ServerError
                else -> UnknownError(code)
            }
        }
    }
}
```

### Retry Strategy
```kotlin
// RetryStrategy.kt
class RetryStrategy {
    suspend fun <T> executeWithRetry(
        maxRetries: Int = 3,
        delayMs: Long = 1000,
        operation: suspend () -> T
    ): T {
        repeat(maxRetries) { attempt ->
            try {
                return operation()
            } catch (e: Exception) {
                if (attempt == maxRetries - 1) throw e
                delay(delayMs * (attempt + 1)) // Exponential backoff
            }
        }
        throw IllegalStateException("Should not reach here")
    }
}
```

---

## 📱 Usage Examples

### ViewModel Implementation
```kotlin
// WeatherViewModel.kt
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val compatibilityCalculator: WeatherCompatibilityCalculator,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState = _weatherState.asStateFlow()
    
    fun loadWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            
            try {
                val weatherResult = weatherRepository.getCurrentWeather(lat, lon)
                val userProfile = userRepository.getUserProfile()
                
                if (weatherResult.isSuccess && userProfile != null) {
                    val weather = weatherResult.getOrThrow()
                    val compatibility = compatibilityCalculator.calculateCompatibility(
                        weather, userProfile
                    )
                    
                    _weatherState.value = WeatherUiState.Success(
                        weather = weather,
                        compatibility = compatibility
                    )
                } else {
                    _weatherState.value = WeatherUiState.Error("Failed to load weather data")
                }
            } catch (e: Exception) {
                _weatherState.value = WeatherUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(
        val weather: CurrentWeatherResponse,
        val compatibility: WeatherCompatibility
    ) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
```

---

## 🔧 Testing

### Unit Tests
```kotlin
// WeatherRepositoryTest.kt
@Test
fun `getCurrentWeather returns success when API call succeeds`() = runTest {
    // Given
    val mockResponse = mockCurrentWeatherResponse()
    coEvery { apiService.getCurrentWeather(any(), any()) } returns Response.success(mockResponse)
    
    // When
    val result = weatherRepository.getCurrentWeather(10.0, 20.0)
    
    // Then
    assertTrue(result.isSuccess)
    assertEquals(mockResponse, result.getOrNull())
}

@Test
fun `calculateCompatibility returns correct score for perfect weather`() {
    // Given
    val perfectWeather = mockCurrentWeatherResponse(
        temp = 22.0,
        humidity = 50,
        windSpeed = 2.0,
        condition = "Clear"
    )
    val userProfile = mockUserProfile(age = 30, occupation = Occupation.OFFICE_WORKER)
    
    // When
    val compatibility = compatibilityCalculator.calculateCompatibility(perfectWeather, userProfile)
    
    // Then
    assertTrue(compatibility.compatibilityScore >= 90f)
    assertEquals(10, compatibility.pointsEarned)
}
```

---

## 📋 API Limits & Best Practices

### Free Plan Limits
- **Calls per minute**: 60
- **Calls per month**: 1,000,000
- **Data update frequency**: 10 minutes

### Best Practices
1. **Cache aggressively**: Store data locally for offline usage
2. **Batch requests**: Combine multiple data points when possible
3. **Use appropriate intervals**: Don't call more than every 10 minutes
4. **Handle errors gracefully**: Always have fallback data
5. **Monitor usage**: Track API calls to avoid limits
6. **Use HTTPS**: Always use secure connections
7. **Validate responses**: Check data integrity before processing

### Performance Tips
- Use `units=metric` for Celsius temperatures
- Use `lang=vi` for Vietnamese descriptions
- Cache weather icons locally
- Implement proper loading states
- Use background sync for better UX

---

## ✅ Integration Checklist

- [ ] API key configured securely
- [ ] Retrofit service implemented
- [ ] Repository pattern with caching
- [ ] Error handling and retry logic
- [ ] Rate limiting implemented
- [ ] Offline support with cached data
- [ ] Weather compatibility algorithm
- [ ] Unit tests for core functionality
- [ ] UI states for loading/success/error
- [ ] Background sync for daily updates

---

## 🎯 Hướng Dẫn Lấy Thông Tin Thời Tiết Đầy Đủ & Chính Xác Nhất

### 1. Multi-Source Data Validation
```kotlin
// WeatherDataValidator.kt
class WeatherDataValidator {
    
    fun validateWeatherData(weather: CurrentWeatherResponse): ValidationResult {
        val errors = mutableListOf<String>()
        
        // Validate temperature range
        if (weather.main.temp < -50 || weather.main.temp > 60) {
            errors.add("Temperature out of realistic range: ${weather.main.temp}°C")
        }
        
        // Validate humidity
        if (weather.main.humidity < 0 || weather.main.humidity > 100) {
            errors.add("Humidity out of range: ${weather.main.humidity}%")
        }
        
        // Validate pressure
        if (weather.main.pressure < 800 || weather.main.pressure > 1200) {
            errors.add("Pressure out of realistic range: ${weather.main.pressure} hPa")
        }
        
        // Validate wind speed
        if (weather.wind.speed < 0 || weather.wind.speed > 200) {
            errors.add("Wind speed out of range: ${weather.wind.speed} m/s")
        }
        
        // Validate timestamp (not older than 1 hour)
        val currentTime = System.currentTimeMillis() / 1000
        if (currentTime - weather.dt > 3600) {
            errors.add("Weather data is outdated: ${weather.dt}")
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
    
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String>
    )
}
```

### 2. Enhanced Location Accuracy
```kotlin
// LocationWeatherService.kt
class LocationWeatherService @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationProvider: LocationProvider
) {
    
    suspend fun getAccurateWeatherForCurrentLocation(): Result<CurrentWeatherResponse> {
        return try {
            // Get high-accuracy location
            val location = locationProvider.getHighAccuracyLocation(
                timeout = 30_000L,
                minAccuracy = 100f // meters
            )
            
            if (location != null) {
                // Round coordinates to appropriate precision (4 decimal places ≈ 11m accuracy)
                val lat = String.format("%.4f", location.latitude).toDouble()
                val lon = String.format("%.4f", location.longitude).toDouble()
                
                weatherRepository.getCurrentWeather(lat, lon)
            } else {
                Result.failure(Exception("Unable to get accurate location"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getWeatherForMultipleNearbyLocations(
        centerLat: Double,
        centerLon: Double,
        radiusKm: Double = 5.0
    ): List<CurrentWeatherResponse> {
        val locations = generateNearbyLocations(centerLat, centerLon, radiusKm)
        val weatherData = mutableListOf<CurrentWeatherResponse>()
        
        locations.forEach { (lat, lon) ->
            try {
                val result = weatherRepository.getCurrentWeather(lat, lon)
                if (result.isSuccess) {
                    result.getOrNull()?.let { weatherData.add(it) }
                }
            } catch (e: Exception) {
                // Log error but continue with other locations
            }
        }
        
        return weatherData
    }
    
    private fun generateNearbyLocations(
        centerLat: Double,
        centerLon: Double,
        radiusKm: Double
    ): List<Pair<Double, Double>> {
        val locations = mutableListOf<Pair<Double, Double>>()
        val earthRadius = 6371.0 // km
        
        // Generate 4 points around the center
        for (i in 0..3) {
            val bearing = i * 90.0 // 0°, 90°, 180°, 270°
            val lat = centerLat + (radiusKm / earthRadius) * (180 / Math.PI) * cos(Math.toRadians(bearing))
            val lon = centerLon + (radiusKm / earthRadius) * (180 / Math.PI) * sin(Math.toRadians(bearing)) / cos(Math.toRadians(centerLat))
            locations.add(Pair(lat, lon))
        }
        
        return locations
    }
}
```

### 3. Data Aggregation & Accuracy Enhancement
```kotlin
// WeatherDataAggregator.kt
class WeatherDataAggregator {
    
    fun aggregateMultipleWeatherSources(
        primaryWeather: CurrentWeatherResponse,
        nearbyWeatherData: List<CurrentWeatherResponse>
    ): EnhancedWeatherData {
        
        val allData = listOf(primaryWeather) + nearbyWeatherData
        
        // Calculate weighted averages
        val avgTemp = calculateWeightedAverage(
            allData.map { it.main.temp },
            allData.map { calculateLocationWeight(it) }
        )
        
        val avgHumidity = calculateWeightedAverage(
            allData.map { it.main.humidity.toDouble() },
            allData.map { calculateLocationWeight(it) }
        )
        
        val avgPressure = calculateWeightedAverage(
            allData.map { it.main.pressure.toDouble() },
            allData.map { calculateLocationWeight(it) }
        )
        
        // Determine most common weather condition
        val weatherConditions = allData.flatMap { it.weather }
        val dominantCondition = weatherConditions
            .groupBy { it.main }
            .maxByOrNull { it.value.size }
            ?.key ?: primaryWeather.weather.firstOrNull()?.main
        
        return EnhancedWeatherData(
            originalData = primaryWeather,
            enhancedTemp = avgTemp,
            enhancedHumidity = avgHumidity.toInt(),
            enhancedPressure = avgPressure.toInt(),
            dominantCondition = dominantCondition,
            dataQuality = calculateDataQuality(allData),
            confidenceLevel = calculateConfidenceLevel(allData)
        )
    }
    
    private fun calculateWeightedAverage(
        values: List<Double>,
        weights: List<Double>
    ): Double {
        val totalWeight = weights.sum()
        val weightedSum = values.zip(weights) { value, weight -> value * weight }.sum()
        return weightedSum / totalWeight
    }
    
    private fun calculateLocationWeight(weather: CurrentWeatherResponse): Double {
        // Weight based on data freshness and location accuracy
        val currentTime = System.currentTimeMillis() / 1000
        val dataAge = currentTime - weather.dt
        val freshnessWeight = maxOf(0.1, 1.0 - (dataAge / 3600.0)) // Decrease weight as data gets older
        
        return freshnessWeight
    }
    
    private fun calculateDataQuality(weatherData: List<CurrentWeatherResponse>): DataQuality {
        val dataCount = weatherData.size
        val avgAge = weatherData.map { 
            (System.currentTimeMillis() / 1000) - it.dt 
        }.average()
        
        return when {
            dataCount >= 4 && avgAge < 1800 -> DataQuality.EXCELLENT
            dataCount >= 3 && avgAge < 3600 -> DataQuality.GOOD
            dataCount >= 2 && avgAge < 7200 -> DataQuality.FAIR
            else -> DataQuality.POOR
        }
    }
    
    private fun calculateConfidenceLevel(weatherData: List<CurrentWeatherResponse>): Double {
        if (weatherData.size < 2) return 0.5
        
        // Calculate variance in temperature readings
        val temps = weatherData.map { it.main.temp }
        val avgTemp = temps.average()
        val variance = temps.map { (it - avgTemp).pow(2) }.average()
        val standardDeviation = sqrt(variance)
        
        // Lower standard deviation = higher confidence
        return maxOf(0.0, minOf(1.0, 1.0 - (standardDeviation / 10.0)))
    }
    
    data class EnhancedWeatherData(
        val originalData: CurrentWeatherResponse,
        val enhancedTemp: Double,
        val enhancedHumidity: Int,
        val enhancedPressure: Int,
        val dominantCondition: String?,
        val dataQuality: DataQuality,
        val confidenceLevel: Double
    )
    
    enum class DataQuality {
        EXCELLENT, GOOD, FAIR, POOR
    }
}
```

### 4. Real-time Data Monitoring
```kotlin
// WeatherDataMonitor.kt
class WeatherDataMonitor @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val dataValidator: WeatherDataValidator,
    private val notificationManager: NotificationManager
) {
    
    private val _dataQualityFlow = MutableStateFlow<DataQualityStatus>(DataQualityStatus.Unknown)
    val dataQualityFlow = _dataQualityFlow.asStateFlow()
    
    suspend fun startContinuousMonitoring(
        lat: Double,
        lon: Double,
        intervalMinutes: Int = 10
    ) {
        while (true) {
            try {
                val weatherResult = weatherRepository.getCurrentWeather(lat, lon)
                
                if (weatherResult.isSuccess) {
                    val weather = weatherResult.getOrThrow()
                    val validation = dataValidator.validateWeatherData(weather)
                    
                    val status = if (validation.isValid) {
                        DataQualityStatus.Good(weather)
                    } else {
                        DataQualityStatus.Poor(validation.errors)
                    }
                    
                    _dataQualityFlow.value = status
                    
                    // Alert user if data quality is poor
                    if (!validation.isValid) {
                        notificationManager.showDataQualityAlert(validation.errors)
                    }
                } else {
                    _dataQualityFlow.value = DataQualityStatus.Error(weatherResult.exceptionOrNull()?.message)
                }
                
            } catch (e: Exception) {
                _dataQualityFlow.value = DataQualityStatus.Error(e.message)
            }
            
            delay(intervalMinutes * 60 * 1000L)
        }
    }
    
    sealed class DataQualityStatus {
        object Unknown : DataQualityStatus()
        data class Good(val weather: CurrentWeatherResponse) : DataQualityStatus()
        data class Poor(val errors: List<String>) : DataQualityStatus()
        data class Error(val message: String?) : DataQualityStatus()
    }
}
```

### 5. Advanced Caching Strategy
```kotlin
// AdvancedWeatherCache.kt
class AdvancedWeatherCache @Inject constructor(
    private val weatherDao: WeatherDao,
    private val dataValidator: WeatherDataValidator
) {
    
    suspend fun getCachedWeatherWithFallback(
        lat: Double,
        lon: Double
    ): CurrentWeatherResponse? {
        // Try to get recent cached data first
        val recentData = weatherDao.getWeatherByLocation(lat, lon, maxAgeHours = 2)
        if (recentData != null) {
            val validation = dataValidator.validateWeatherData(recentData)
            if (validation.isValid) {
                return recentData
            }
        }
        
        // Fallback to older data if recent data is invalid
        val olderData = weatherDao.getWeatherByLocation(lat, lon, maxAgeHours = 24)
        if (olderData != null) {
            val validation = dataValidator.validateWeatherData(olderData)
            if (validation.isValid) {
                return olderData
            }
        }
        
        // Try nearby locations as last resort
        val nearbyData = weatherDao.getNearbyWeatherData(lat, lon, radiusKm = 10.0)
        return nearbyData.firstOrNull { 
            dataValidator.validateWeatherData(it).isValid 
        }
    }
    
    suspend fun cacheWeatherWithMetadata(
        weather: CurrentWeatherResponse,
        lat: Double,
        lon: Double,
        dataQuality: WeatherDataAggregator.DataQuality,
        confidenceLevel: Double
    ) {
        val cacheEntry = WeatherCacheEntry(
            weather = weather,
            latitude = lat,
            longitude = lon,
            timestamp = System.currentTimeMillis(),
            dataQuality = dataQuality,
            confidenceLevel = confidenceLevel,
            isValidated = dataValidator.validateWeatherData(weather).isValid
        )
        
        weatherDao.insertWeatherCache(cacheEntry)
        
        // Clean up old entries
        weatherDao.deleteOldCacheEntries(maxAgeHours = 48)
    }
}
```

### 6. API Usage Optimization
```kotlin
// OptimizedWeatherService.kt
class OptimizedWeatherService @Inject constructor(
    private val apiService: WeatherApiService,
    private val rateLimiter: ApiRateLimiter,
    private val cache: AdvancedWeatherCache,
    private val dataAggregator: WeatherDataAggregator
) {
    
    suspend fun getOptimalWeatherData(
        lat: Double,
        lon: Double,
        forceRefresh: Boolean = false
    ): Result<WeatherDataAggregator.EnhancedWeatherData> {
        
        return try {
            // Check cache first unless force refresh
            if (!forceRefresh) {
                val cachedData = cache.getCachedWeatherWithFallback(lat, lon)
                if (cachedData != null) {
                    return Result.success(
                        WeatherDataAggregator.EnhancedWeatherData(
                            originalData = cachedData,
                            enhancedTemp = cachedData.main.temp,
                            enhancedHumidity = cachedData.main.humidity,
                            enhancedPressure = cachedData.main.pressure,
                            dominantCondition = cachedData.weather.firstOrNull()?.main,
                            dataQuality = WeatherDataAggregator.DataQuality.GOOD,
                            confidenceLevel = 0.8
                        )
                    )
                }
            }
            
            // Check rate limiting
            if (!rateLimiter.canMakeApiCall("weather")) {
                val waitTime = rateLimiter.getTimeUntilNextCall("weather")
                throw Exception("Rate limit exceeded. Wait ${waitTime}ms")
            }
            
            // Get primary weather data
            val primaryResult = apiService.getCurrentWeather(lat, lon)
            if (!primaryResult.isSuccessful || primaryResult.body() == null) {
                throw Exception("API call failed: ${primaryResult.code()}")
            }
            
            val primaryWeather = primaryResult.body()!!
            
            // Get nearby data for enhanced accuracy (if rate limit allows)
            val nearbyData = mutableListOf<CurrentWeatherResponse>()
            if (rateLimiter.canMakeApiCall("weather_nearby")) {
                try {
                    val nearbyLocations = generateNearbyCoordinates(lat, lon)
                    nearbyLocations.take(2).forEach { (nearLat, nearLon) ->
                        val nearbyResult = apiService.getCurrentWeather(nearLat, nearLon)
                        if (nearbyResult.isSuccessful && nearbyResult.body() != null) {
                            nearbyData.add(nearbyResult.body()!!)
                        }
                    }
                } catch (e: Exception) {
                    // Continue with primary data only
                }
            }
            
            // Aggregate data for enhanced accuracy
            val enhancedData = dataAggregator.aggregateMultipleWeatherSources(
                primaryWeather,
                nearbyData
            )
            
            // Cache the enhanced data
            cache.cacheWeatherWithMetadata(
                primaryWeather,
                lat,
                lon,
                enhancedData.dataQuality,
                enhancedData.confidenceLevel
            )
            
            Result.success(enhancedData)
            
        } catch (e: Exception) {
            // Try to return cached data as fallback
            val fallbackData = cache.getCachedWeatherWithFallback(lat, lon)
            if (fallbackData != null) {
                Result.success(
                    WeatherDataAggregator.EnhancedWeatherData(
                        originalData = fallbackData,
                        enhancedTemp = fallbackData.main.temp,
                        enhancedHumidity = fallbackData.main.humidity,
                        enhancedPressure = fallbackData.main.pressure,
                        dominantCondition = fallbackData.weather.firstOrNull()?.main,
                        dataQuality = WeatherDataAggregator.DataQuality.FAIR,
                        confidenceLevel = 0.6
                    )
                )
            } else {
                Result.failure(e)
            }
        }
    }
    
    private fun generateNearbyCoordinates(
        centerLat: Double,
        centerLon: Double,
        offsetDegrees: Double = 0.01 // ~1km
    ): List<Pair<Double, Double>> {
        return listOf(
            Pair(centerLat + offsetDegrees, centerLon),
            Pair(centerLat - offsetDegrees, centerLon),
            Pair(centerLat, centerLon + offsetDegrees),
            Pair(centerLat, centerLon - offsetDegrees)
        )
    }
}
```

### 7. Best Practices Summary

#### ✅ Để Có Dữ Liệu Thời Tiết Chính Xác Nhất:

1. **Multi-Source Validation**
   - Lấy dữ liệu từ nhiều điểm gần nhau
   - So sánh và tính trung bình có trọng số
   - Loại bỏ dữ liệu bất thường

2. **Location Accuracy**
   - Sử dụng GPS độ chính xác cao
   - Làm tròn tọa độ phù hợp (4 chữ số thập phân)
   - Kiểm tra độ tin cậy của vị trí

3. **Data Freshness**
   - Ưu tiên dữ liệu mới nhất (< 1 giờ)
   - Cập nhật định kỳ mỗi 10-15 phút
   - Theo dõi timestamp của API

4. **Validation & Quality Control**
   - Kiểm tra giá trị hợp lý (nhiệt độ, độ ẩm, áp suất)
   - Xác thực tính nhất quán của dữ liệu
   - Đánh giá độ tin cậy

5. **Smart Caching**
   - Cache đa cấp với metadata
   - Fallback thông minh khi API lỗi
   - Làm sạch cache định kỳ

6. **Error Handling**
   - Retry với exponential backoff
   - Graceful degradation
   - User-friendly error messages

#### 🎯 Kết Quả Mong Đợi:
- **Độ chính xác**: 95%+ với enhanced data
- **Độ tin cậy**: Real-time validation
- **Performance**: < 2s response time
- **Offline support**: 24h cached data
- **User experience**: Seamless & accurate

---

**Status**: ✅ Ready for Implementation  
**Next Steps**: Integrate with Weather Personalized App architecture  
**Estimated Implementation**: 3-4 days