package com.dex.base.baseandroidcompose.data.ai

import com.dex.base.baseandroidcompose.data.models.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * AI Engine for calculating weather compatibility score
 */
class WeatherCompatibilityEngine {
    
    /**
     * Calculate weather compatibility score for user
     */
    fun calculateCompatibility(
        weatherData: WeatherData,
        userProfile: UserProfile
    ): WeatherCompatibility {
        
        val factors = calculateFactors(weatherData, userProfile)
        val score = calculateOverallScore(factors, userProfile)
        val points = WeatherCompatibility.calculatePoints(score)
        val reasoning = generateReasoning(factors, userProfile, weatherData)
        val recommendations = generateRecommendations(weatherData, userProfile, score)
        
        return WeatherCompatibility(
            date = getCurrentDate(),
            weatherData = weatherData,
            userProfile = userProfile,
            compatibilityScore = score,
            pointsEarned = points,
            reasoning = reasoning,
            recommendations = recommendations,
            factors = factors
        )
    }
    
    /**
     * Calculate detailed compatibility factors
     */
    private fun calculateFactors(
        weatherData: WeatherData,
        userProfile: UserProfile
    ): CompatibilityFactors {
        
        val temperatureScore = calculateTemperatureScore(weatherData, userProfile)
        val humidityScore = calculateHumidityScore(weatherData, userProfile)
        val windScore = calculateWindScore(weatherData, userProfile)
        val visibilityScore = calculateVisibilityScore(weatherData)
        val comfortScore = calculateComfortScore(weatherData, userProfile)
        val healthScore = calculateHealthScore(weatherData, userProfile)
        val activityScore = calculateActivityScore(weatherData, userProfile)
        val occupationScore = calculateOccupationScore(weatherData, userProfile)
        val ageScore = calculateAgeScore(weatherData, userProfile)
        
        return CompatibilityFactors(
            temperatureScore = temperatureScore,
            humidityScore = humidityScore,
            windScore = windScore,
            visibilityScore = visibilityScore,
            comfortScore = comfortScore,
            healthScore = healthScore,
            activityScore = activityScore,
            occupationScore = occupationScore,
            ageScore = ageScore
        )
    }
    
    /**
     * Calculate temperature compatibility score
     */
    private fun calculateTemperatureScore(
        weatherData: WeatherData,
        userProfile: UserProfile
    ): Float {
        val temp = weatherData.temperature
        val preferences = userProfile.preferences
        val idealRange = preferences.preferredTemperatureRange
        
        return when {
            temp in idealRange.min..idealRange.max -> 100f
            temp < idealRange.min -> {
                val diff = idealRange.min - temp
                max(0.0, 100.0 - (diff * 5.0)).toFloat() // -5 points per degree below
            }
            temp > idealRange.max -> {
                val diff = temp - idealRange.max
                max(0.0, 100.0 - (diff * 4.0)).toFloat() // -4 points per degree above
            }
            else -> 50f
        }
    }
    
    /**
     * Calculate humidity compatibility score
     */
    private fun calculateHumidityScore(
        weatherData: WeatherData,
        userProfile: UserProfile
    ): Float {
        val humidity = weatherData.humidity
        val preferences = userProfile.preferences
        val idealRange = preferences.preferredHumidityRange
        
        return when {
            humidity in idealRange.min..idealRange.max -> 100f
            humidity < idealRange.min -> {
                val diff = idealRange.min - humidity
                max(0f, 100f - (diff * 2f)) // -2 points per % below
            }
            humidity > idealRange.max -> {
                val diff = humidity - idealRange.max
                max(0f, 100f - (diff * 1.5f)) // -1.5 points per % above
            }
            else -> 50f
        }
    }
    
    /**
     * Calculate wind compatibility score
     */
    private fun calculateWindScore(
        weatherData: WeatherData,
        userProfile: UserProfile
    ): Float {
        val windSpeed = weatherData.windSpeed
        val sensitivity = userProfile.preferences.windSensitivity
        
        val baseScore = when {
            windSpeed <= 5 -> 100f // Light breeze
            windSpeed <= 10 -> 80f // Moderate breeze
            windSpeed <= 15 -> 60f // Fresh breeze
            windSpeed <= 20 -> 40f // Strong breeze
            else -> 20f // Very strong wind
        }
        
        // Adjust based on user sensitivity
        return baseScore * (1f - sensitivity * 0.3f)
    }
    
    /**
     * Calculate visibility score
     */
    private fun calculateVisibilityScore(weatherData: WeatherData): Float {
        val visibility = weatherData.visibility
        
        return when {
            visibility >= 10000 -> 100f // Excellent visibility
            visibility >= 5000 -> 80f // Good visibility
            visibility >= 2000 -> 60f // Moderate visibility
            visibility >= 1000 -> 40f // Poor visibility
            else -> 20f // Very poor visibility
        }
    }
    
    /**
     * Calculate comfort score based on feels-like temperature
     */
    private fun calculateComfortScore(
        weatherData: WeatherData,
        userProfile: UserProfile
    ): Float {
        val feelsLike = weatherData.feelsLike
        val actualTemp = weatherData.temperature
        val diff = abs(feelsLike - actualTemp)
        
        return when {
            diff <= 2 -> 100f // Very comfortable
            diff <= 5 -> 80f // Comfortable
            diff <= 8 -> 60f // Somewhat comfortable
            diff <= 12 -> 40f // Uncomfortable
            else -> 20f // Very uncomfortable
        }
    }
    
    /**
     * Calculate health score based on weather conditions
     */
    private fun calculateHealthScore(
        weatherData: WeatherData,
        userProfile: UserProfile
    ): Float {
        val occupation = userProfile.occupation
        val healthSensitivity = occupation.healthSensitivity
        
        var score = 100f
        
        // Extreme temperatures affect health
        if (weatherData.temperature < 5 || weatherData.temperature > 35) {
            score -= 20f * healthSensitivity
        }
        
        // High humidity affects health
        if (weatherData.humidity > 80) {
            score -= 15f * healthSensitivity
        }
        
        // Strong winds affect health
        if (weatherData.windSpeed > 15) {
            score -= 10f * healthSensitivity
        }
        
        return max(0f, score)
    }
    
    /**
     * Calculate activity score based on outdoor conditions
     */
    private fun calculateActivityScore(
        weatherData: WeatherData,
        userProfile: UserProfile
    ): Float {
        val occupation = userProfile.occupation
        val outdoorFactor = occupation.outdoorFactor
        
        var score = 100f
        
        // Temperature affects outdoor activities
        val temp = weatherData.temperature
        if (temp < 10 || temp > 30) {
            score -= 20f * outdoorFactor
        }
        
        // Wind affects outdoor activities
        if (weatherData.windSpeed > 10) {
            score -= 15f * outdoorFactor
        }
        
        // Poor visibility affects activities
        if (weatherData.visibility < 5000) {
            score -= 10f * outdoorFactor
        }
        
        return max(0f, score)
    }
    
    /**
     * Calculate occupation-specific score
     */
    private fun calculateOccupationScore(
        weatherData: WeatherData,
        userProfile: UserProfile
    ): Float {
        val occupation = userProfile.occupation
        
        return when (occupation) {
            Occupation.OUTDOOR_WORKER, Occupation.CONSTRUCTION -> {
                // Outdoor workers need good weather conditions
                val tempScore = if (weatherData.temperature in 15.0..28.0) 100f else 50f
                val windScore = if (weatherData.windSpeed < 12) 100f else 30f
                val visibilityScore = if (weatherData.visibility > 8000) 100f else 40f
                (tempScore + windScore + visibilityScore) / 3f
            }
            Occupation.HEALTHCARE -> {
                // Healthcare workers care about air quality and comfort
                val comfortScore = calculateComfortScore(weatherData, userProfile)
                val healthScore = calculateHealthScore(weatherData, userProfile)
                (comfortScore + healthScore) / 2f
            }
            Occupation.OFFICE_WORKER -> {
                // Office workers care about commute conditions
                val visibilityScore = calculateVisibilityScore(weatherData)
                val windScore = if (weatherData.windSpeed < 15) 80f else 50f
                (visibilityScore + windScore) / 2f
            }
            else -> 75f // Default score for other occupations
        }
    }
    
    /**
     * Calculate age-specific score
     */
    private fun calculateAgeScore(
        weatherData: WeatherData,
        userProfile: UserProfile
    ): Float {
        val ageCategory = AgeCategory.fromAge(userProfile.age)
        
        return when (ageCategory) {
            AgeCategory.CHILD, AgeCategory.SENIOR -> {
                // Children and seniors are more sensitive to extreme weather
                var score = 100f
                if (weatherData.temperature < 10 || weatherData.temperature > 32) {
                    score -= 30f
                }
                if (weatherData.windSpeed > 12) {
                    score -= 20f
                }
                if (weatherData.humidity > 75) {
                    score -= 15f
                }
                max(0f, score)
            }
            AgeCategory.TEEN, AgeCategory.YOUNG_ADULT -> {
                // Young people are more adaptable
                var score = 100f
                if (weatherData.temperature < 0 || weatherData.temperature > 38) {
                    score -= 20f
                }
                max(0f, score)
            }
            AgeCategory.ADULT -> {
                // Adults have moderate sensitivity
                var score = 100f
                if (weatherData.temperature < 5 || weatherData.temperature > 35) {
                    score -= 25f
                }
                max(0f, score)
            }
        }
    }
    
    /**
     * Calculate overall compatibility score
     */
    private fun calculateOverallScore(
        factors: CompatibilityFactors,
        userProfile: UserProfile
    ): Float {
        val weights = getWeights(userProfile)
        
        val weightedScore = (
            factors.temperatureScore * weights.temperature +
            factors.humidityScore * weights.humidity +
            factors.windScore * weights.wind +
            factors.visibilityScore * weights.visibility +
            factors.comfortScore * weights.comfort +
            factors.healthScore * weights.health +
            factors.activityScore * weights.activity +
            factors.occupationScore * weights.occupation +
            factors.ageScore * weights.age
        ) / weights.total
        
        return min(100f, max(0f, weightedScore))
    }
    
    /**
     * Get weights based on user profile
     */
    private fun getWeights(userProfile: UserProfile): ScoreWeights {
        val occupation = userProfile.occupation
        val ageCategory = AgeCategory.fromAge(userProfile.age)
        
        return when {
            occupation.outdoorFactor > 0.8f -> {
                // Outdoor workers prioritize weather conditions
                ScoreWeights(0.25f, 0.15f, 0.20f, 0.15f, 0.10f, 0.05f, 0.05f, 0.03f, 0.02f)
            }
            occupation.healthSensitivity > 0.8f -> {
                // Health-sensitive occupations prioritize health factors
                ScoreWeights(0.20f, 0.15f, 0.10f, 0.10f, 0.15f, 0.20f, 0.05f, 0.03f, 0.02f)
            }
            ageCategory in listOf(AgeCategory.CHILD, AgeCategory.SENIOR) -> {
                // Age-sensitive groups prioritize comfort and health
                ScoreWeights(0.20f, 0.15f, 0.15f, 0.10f, 0.20f, 0.15f, 0.02f, 0.01f, 0.02f)
            }
            else -> {
                // Default balanced weights
                ScoreWeights(0.20f, 0.15f, 0.15f, 0.10f, 0.15f, 0.10f, 0.10f, 0.03f, 0.02f)
            }
        }
    }
    
    /**
     * Generate reasoning for the compatibility score
     */
    private fun generateReasoning(
        factors: CompatibilityFactors,
        userProfile: UserProfile,
        weatherData: WeatherData
    ): List<String> {
        val reasoning = mutableListOf<String>()
        
        // Temperature reasoning
        if (factors.temperatureScore > 80f) {
            reasoning.add("Nhiệt độ ${weatherData.temperature.toInt()}°C rất phù hợp với bạn")
        } else if (factors.temperatureScore < 50f) {
            reasoning.add("Nhiệt độ ${weatherData.temperature.toInt()}°C không lý tưởng cho hoạt động")
        }
        
        // Humidity reasoning
        if (factors.humidityScore < 60f) {
            reasoning.add("Độ ẩm ${weatherData.humidity}% có thể gây khó chịu")
        }
        
        // Wind reasoning
        if (factors.windScore < 70f) {
            reasoning.add("Gió mạnh ${weatherData.windSpeed.toInt()} m/s ảnh hưởng đến hoạt động")
        }
        
        // Occupation-specific reasoning
        when (userProfile.occupation) {
            Occupation.OUTDOOR_WORKER -> {
                reasoning.add("Điều kiện thời tiết ảnh hưởng trực tiếp đến công việc ngoài trời")
            }
            Occupation.HEALTHCARE -> {
                reasoning.add("Thời tiết ảnh hưởng đến sức khỏe và khả năng làm việc")
            }
            else -> {}
        }
        
        return reasoning.take(3) // Limit to 3 main reasons
    }
    
    /**
     * Generate recommendations based on weather and user profile
     */
    private fun generateRecommendations(
        weatherData: WeatherData,
        userProfile: UserProfile,
        score: Float
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()
        
        // Temperature recommendations
        if (weatherData.temperature < 15) {
            recommendations.add(
                Recommendation(
                    type = RecommendationType.CLOTHING,
                    title = "Mặc ấm",
                    description = "Thời tiết lạnh, nên mặc áo khoác và giữ ấm cơ thể",
                    priority = RecommendationPriority.HIGH,
                    icon = "🧥"
                )
            )
        } else if (weatherData.temperature > 30) {
            recommendations.add(
                Recommendation(
                    type = RecommendationType.CLOTHING,
                    title = "Mặc mát",
                    description = "Thời tiết nóng, nên mặc quần áo thoáng mát",
                    priority = RecommendationPriority.HIGH,
                    icon = "👕"
                )
            )
        }
        
        // Wind recommendations
        if (weatherData.windSpeed > 15) {
            recommendations.add(
                Recommendation(
                    type = RecommendationType.SAFETY,
                    title = "Cẩn thận với gió mạnh",
                    description = "Gió mạnh, hạn chế hoạt động ngoài trời",
                    priority = RecommendationPriority.MEDIUM,
                    icon = "💨"
                )
            )
        }
        
        // Activity recommendations
        if (score > 80f) {
            recommendations.add(
                Recommendation(
                    type = RecommendationType.ACTIVITY,
                    title = "Thời tiết tuyệt vời",
                    description = "Thích hợp cho các hoạt động ngoài trời",
                    priority = RecommendationPriority.LOW,
                    icon = "🌞"
                )
            )
        }
        
        return recommendations.take(3) // Limit to 3 recommendations
    }
    
    /**
     * Get current date string
     */
    private fun getCurrentDate(): String {
        return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())
    }
}

/**
 * Weights for different score factors
 */
private data class ScoreWeights(
    val temperature: Float,
    val humidity: Float,
    val wind: Float,
    val visibility: Float,
    val comfort: Float,
    val health: Float,
    val activity: Float,
    val occupation: Float,
    val age: Float
) {
    val total: Float = temperature + humidity + wind + visibility + comfort + health + activity + occupation + age
}