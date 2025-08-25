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
     * Calculate detailed compatibility factors with pressure consideration
     */
    private fun calculateFactors(
        weatherData: WeatherData,
        userProfile: UserProfile
    ): CompatibilityFactors {
        
        val temperatureScore = calculateTemperatureScore(weatherData, userProfile)
        val humidityScore = calculateHumidityScore(weatherData, userProfile)
        val windScore = calculateWindScore(weatherData, userProfile)
        val visibilityScore = calculateVisibilityScore(weatherData)
        val pressureScore = calculatePressureScore(weatherData, userProfile)
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
            pressureScore = pressureScore,
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
     * Calculate occupation-specific score with enhanced logic
     */
    private fun calculateOccupationScore(
        weatherData: WeatherData,
        userProfile: UserProfile
    ): Float {
        val occupation = userProfile.occupation
        val temp = weatherData.temperature
        val humidity = weatherData.humidity
        val windSpeed = weatherData.windSpeed
        val visibility = weatherData.visibility
        val pressure = weatherData.pressure
        val description = weatherData.description.lowercase()
        
        return when (occupation) {
            Occupation.OUTDOOR_WORKER, Occupation.CONSTRUCTION -> {
                // Outdoor workers are highly affected by weather conditions
                var score = 100f
                
                // Temperature impact (critical for outdoor work)
                when {
                    temp < 5 -> score -= 50f // Too cold for safe work
                    temp < 10 -> score -= 30f // Cold, reduced efficiency
                    temp > 35 -> score -= 45f // Too hot, safety risk
                    temp > 30 -> score -= 25f // Hot, reduced productivity
                    temp in 15.0..25.0 -> score += 15f // Ideal working temperature
                }
                
                // Wind impact (safety critical)
                when {
                    windSpeed > 20 -> score -= 60f // Dangerous for construction
                    windSpeed > 15 -> score -= 35f // Difficult working conditions
                    windSpeed > 10 -> score -= 15f // Moderate impact
                    windSpeed < 5 -> score += 10f // Calm conditions bonus
                }
                
                // Visibility impact (safety)
                when {
                    visibility < 1000 -> score -= 40f // Poor visibility, safety risk
                    visibility < 5000 -> score -= 20f // Reduced visibility
                    visibility > 10000 -> score += 10f // Excellent visibility
                }
                
                // Weather condition impact
                when {
                    "mưa" in description || "rain" in description -> {
                        when {
                            "cường độ nặng" in description || "heavy" in description -> score -= 70f
                            "vừa" in description || "moderate" in description -> score -= 40f
                            "nhẹ" in description || "light" in description -> score -= 20f
                        }
                    }
                    "tuyết" in description || "snow" in description -> score -= 50f
                    "sương mù" in description || "fog" in description -> score -= 30f
                    "nắng" in description || "clear" in description -> score += 10f
                }
                
                // Humidity impact (comfort and safety)
                when {
                    humidity > 90 -> score -= 25f // Very humid, uncomfortable
                    humidity > 80 -> score -= 15f // High humidity
                    humidity in 40..70 -> score += 5f // Comfortable range
                }
                
                max(0f, score)
            }
            Occupation.HEALTHCARE -> {
                // Healthcare workers care about comfort and health factors
                var score = 100f
                
                // Temperature comfort (affects patient care quality)
                when {
                    temp < 10 -> score -= 20f // Cold affects dexterity
                    temp > 30 -> score -= 25f // Heat affects concentration
                    temp in 18.0..24.0 -> score += 10f // Optimal working temperature
                }
                
                // Pressure sensitivity (affects patients and staff)
                when {
                    pressure < 990 -> score -= 15f // Low pressure affects health
                    pressure < 1000 -> score -= 8f // Moderate pressure drop
                    pressure > 1020 -> score += 5f // Stable high pressure
                }
                
                // Humidity impact (hygiene and comfort)
                when {
                    humidity > 80 -> score -= 20f // High humidity, discomfort
                    humidity < 30 -> score -= 15f // Too dry, respiratory issues
                    humidity in 40..60 -> score += 8f // Ideal for health environment
                }
                
                // Weather stability bonus
                if (windSpeed < 10 && "clear" in description || "nắng" in description) {
                    score += 5f // Stable conditions good for health
                }
                
                max(0f, score)
            }
            Occupation.OFFICE_WORKER -> {
                // Office workers mainly affected by commute and mood
                var score = 100f
                
                // Commute conditions
                when {
                    "mưa cường độ nặng" in description || "heavy rain" in description -> score -= 30f
                    "mưa" in description || "rain" in description -> score -= 15f
                    windSpeed > 15 -> score -= 20f // Difficult commute
                    visibility < 2000 -> score -= 25f // Poor visibility for driving
                }
                
                // Comfort for indoor work
                when {
                    temp in 20.0..26.0 -> score += 8f // Comfortable indoor climate
                    humidity in 40..65 -> score += 5f // Good indoor humidity
                }
                
                // Mood factors (weather affects productivity)
                when {
                    "nắng" in description || "clear" in description -> score += 10f // Sunny boosts mood
                    pressure < 995 -> score -= 10f // Low pressure affects mood
                }
                
                max(0f, score)
            }
            else -> {
                // Default calculation for other occupations
                var score = 75f
                
                // Basic comfort adjustments
                when {
                    temp < 5 || temp > 35 -> score -= 20f
                    temp in 18.0..28.0 -> score += 10f
                }
                
                if (humidity > 85 || humidity < 25) {
                    score -= 10f
                }
                
                if (windSpeed > 20) {
                    score -= 15f
                }
                
                max(0f, score)
            }
        }
    }
    
    /**
     * Calculate age-specific score with enhanced personalization
     */
    private fun calculateAgeScore(
        weatherData: WeatherData,
        userProfile: UserProfile
    ): Float {
        val ageCategory = AgeCategory.fromAge(userProfile.age)
        val temp = weatherData.temperature
        val humidity = weatherData.humidity
        val windSpeed = weatherData.windSpeed
        val pressure = weatherData.pressure
        
        return when (ageCategory) {
            AgeCategory.CHILD, AgeCategory.SENIOR -> {
                // Children and seniors are more sensitive to extreme weather
                var score = 100f
                
                // Temperature sensitivity (enhanced)
                when {
                    temp < 8 -> score -= 40f // Very cold
                    temp < 15 -> score -= 25f // Cold
                    temp > 35 -> score -= 40f // Very hot
                    temp > 30 -> score -= 25f // Hot
                    temp in 20.0..26.0 -> score += 10f // Ideal range bonus
                }
                
                // Wind sensitivity (enhanced)
                when {
                    windSpeed > 15 -> score -= 30f // Strong wind
                    windSpeed > 10 -> score -= 15f // Moderate wind
                    windSpeed < 3 -> score += 5f // Calm weather bonus
                }
                
                // Humidity sensitivity (enhanced)
                when {
                    humidity > 80 -> score -= 20f // Very humid
                    humidity > 70 -> score -= 10f // Humid
                    humidity < 30 -> score -= 15f // Too dry
                    humidity in 40..60 -> score += 5f // Comfortable humidity
                }
                
                // Pressure sensitivity (new)
                if (pressure < 1000) {
                    score -= 10f // Low pressure can affect health
                }
                
                max(0f, score)
            }
            AgeCategory.TEEN, AgeCategory.YOUNG_ADULT -> {
                // Young people are more adaptable but still have preferences
                var score = 100f
                
                // Temperature tolerance (enhanced)
                when {
                    temp < -5 -> score -= 30f // Extreme cold
                    temp < 5 -> score -= 15f // Very cold
                    temp > 40 -> score -= 30f // Extreme heat
                    temp > 35 -> score -= 15f // Very hot
                    temp in 18.0..28.0 -> score += 10f // Preferred range
                }
                
                // Activity-friendly conditions
                if (windSpeed < 8 && humidity < 75) {
                    score += 5f // Good for outdoor activities
                }
                max(0f, score)
            }
            AgeCategory.ADULT -> {
                // Adults have moderate sensitivity with work considerations
                var score = 100f
                
                // Temperature comfort (enhanced)
                when {
                    temp < 0 -> score -= 35f // Extreme cold
                    temp < 10 -> score -= 20f // Cold
                    temp > 38 -> score -= 35f // Extreme heat
                    temp > 32 -> score -= 20f // Hot
                    temp in 16.0..28.0 -> score += 8f // Work-friendly range
                }
                
                // Work-life balance considerations
                if (humidity > 85) {
                    score -= 15f // High humidity affects productivity
                }
                
                if (windSpeed > 12) {
                    score -= 12f // Strong wind affects commute
                }
                
                // Pressure sensitivity for adults (stress-related)
                if (pressure < 995) {
                    score -= 8f // Low pressure can affect mood and energy
                }
                
                max(0f, score)
            }
        }
    }
    
    /**
     * Calculate pressure score based on atmospheric pressure
     */
    private fun calculatePressureScore(
        weatherData: WeatherData,
        userProfile: UserProfile
    ): Float {
        val pressure = weatherData.pressure
        val ageCategory = AgeCategory.fromAge(userProfile.age)
        val occupation = userProfile.occupation
        
        // Ideal pressure range: 1013-1020 hPa
        val idealPressure = 1016.5f
        val pressureDiff = abs(pressure - idealPressure)
        
        var score = when {
            pressureDiff <= 3f -> 100f // Excellent pressure
            pressureDiff <= 7f -> 85f  // Good pressure
            pressureDiff <= 15f -> 70f // Fair pressure
            pressureDiff <= 25f -> 50f // Poor pressure
            else -> 30f // Very poor pressure
        }
        
        // Age-based adjustments for pressure sensitivity
        when (ageCategory) {
            AgeCategory.SENIOR -> {
                // Seniors are more sensitive to pressure changes
                if (pressureDiff > 10f) score -= 15f
                if (pressure < 1000f || pressure > 1030f) score -= 10f
            }
            AgeCategory.CHILD -> {
                // Children may be affected by extreme pressure
                if (pressureDiff > 15f) score -= 10f
            }
            else -> {
                // Adults generally less affected
                if (pressureDiff > 20f) score -= 5f
            }
        }
        
        // Occupation-based adjustments
        when (occupation) {
            Occupation.HEALTHCARE -> {
                // Healthcare workers need stable conditions
                if (pressureDiff > 12f) score -= 8f
            }
            Occupation.OUTDOOR_WORKER, Occupation.CONSTRUCTION -> {
                // Outdoor workers affected by pressure changes
                if (pressureDiff > 15f) score -= 10f
                if (pressure < 1005f) score -= 5f // Low pressure affects outdoor work
            }
            else -> {
                // Minimal impact for office workers
            }
        }
        
        return max(0f, min(100f, score))
    }
    
    /**
     * Calculate enhanced overall compatibility score with pressure consideration
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
            factors.ageScore * weights.age +
            factors.pressureScore * weights.pressure
        ) / weights.total
        
        return min(100f, max(0f, weightedScore))
    }
    
    /**
     * Get enhanced scoring weights based on user profile with age consideration
     */
    private fun getWeights(userProfile: UserProfile): ScoreWeights {
        val ageCategory = AgeCategory.fromAge(userProfile.age)
        val occupation = userProfile.occupation
        
        // Base weights by occupation
        val baseWeights = when (occupation) {
            Occupation.OUTDOOR_WORKER, Occupation.CONSTRUCTION -> {
                ScoreWeights(0.20f, 0.14f, 0.20f, 0.14f, 0.07f, 0.05f, 0.10f, 0.25f, 0.05f, 0.08f)
            }
            Occupation.HEALTHCARE -> {
                ScoreWeights(0.14f, 0.14f, 0.09f, 0.10f, 0.12f, 0.25f, 0.05f, 0.19f, 0.10f, 0.12f)
            }
            Occupation.OFFICE_WORKER -> {
                ScoreWeights(0.14f, 0.10f, 0.09f, 0.14f, 0.19f, 0.09f, 0.07f, 0.12f, 0.10f, 0.06f)
            }
            else -> {
                // Default balanced weights
                ScoreWeights(0.16f, 0.11f, 0.11f, 0.10f, 0.14f, 0.11f, 0.10f, 0.11f, 0.08f, 0.08f)
            }
        }
        
        // Age-based weight adjustments
        return when (ageCategory) {
            AgeCategory.CHILD, AgeCategory.SENIOR -> {
                // Increase health and safety factors for vulnerable groups
                ScoreWeights(
                    baseWeights.temperature + 0.03f,
                    baseWeights.humidity - 0.03f,
                    baseWeights.wind + 0.02f,
                    baseWeights.visibility - 0.02f,
                    baseWeights.comfort - 0.04f,
                    baseWeights.health + 0.05f,
                    baseWeights.activity - 0.03f,
                    baseWeights.occupation - 0.06f,
                    baseWeights.age + 0.08f,
                    baseWeights.pressure + 0.04f
                )
            }
            AgeCategory.TEEN, AgeCategory.YOUNG_ADULT -> {
                // Increase activity and comfort factors for active groups
                ScoreWeights(
                    baseWeights.temperature + 0.02f,
                    baseWeights.humidity - 0.02f,
                    baseWeights.wind - 0.01f,
                    baseWeights.visibility,
                    baseWeights.comfort + 0.04f,
                    baseWeights.health - 0.03f,
                    baseWeights.activity + 0.06f,
                    baseWeights.occupation - 0.04f,
                    baseWeights.age - 0.02f,
                    baseWeights.pressure - 0.02f
                )
            }
            AgeCategory.ADULT -> {
                // Increase occupation and balance factors for working adults
                ScoreWeights(
                    baseWeights.temperature - 0.02f,
                    baseWeights.humidity - 0.01f,
                    baseWeights.wind - 0.01f,
                    baseWeights.visibility,
                    baseWeights.comfort + 0.02f,
                    baseWeights.health + 0.02f,
                    baseWeights.activity - 0.02f,
                    baseWeights.occupation + 0.04f,
                    baseWeights.age - 0.02f,
                    baseWeights.pressure + 0.01f
                )
            }
        }
    }
    
    /**
     * Generate enhanced reasoning for the compatibility score
     */
    private fun generateReasoning(
        factors: CompatibilityFactors,
        userProfile: UserProfile,
        weatherData: WeatherData
    ): List<String> {
        val reasoning = mutableListOf<String>()
        val temp = weatherData.temperature.toInt()
        val humidity = weatherData.humidity
        val windSpeed = weatherData.windSpeed.toInt()
        val description = weatherData.description
        val ageCategory = AgeCategory.fromAge(userProfile.age)
        val occupation = userProfile.occupation
        
        // Temperature reasoning with personalization
        when {
            factors.temperatureScore > 85f -> {
                when (ageCategory) {
                    AgeCategory.CHILD, AgeCategory.SENIOR -> 
                        reasoning.add("Nhiệt độ ${temp}°C rất lý tưởng cho sức khỏe của bạn")
                    AgeCategory.TEEN, AgeCategory.YOUNG_ADULT -> 
                        reasoning.add("Nhiệt độ ${temp}°C hoàn hảo cho các hoạt động ngoài trời")
                    AgeCategory.ADULT -> 
                        reasoning.add("Nhiệt độ ${temp}°C thoải mái cho công việc và sinh hoạt")
                }
            }
            factors.temperatureScore < 40f -> {
                when {
                    temp < 10 -> reasoning.add("Nhiệt độ ${temp}°C quá lạnh, cần giữ ấm cơ thể")
                    temp > 32 -> reasoning.add("Nhiệt độ ${temp}°C quá nóng, cần tránh nắng và uống nhiều nước")
                    else -> reasoning.add("Nhiệt độ ${temp}°C không phù hợp với hoạt động thường ngày")
                }
            }
            factors.temperatureScore < 70f -> {
                reasoning.add("Nhiệt độ ${temp}°C cần điều chỉnh trang phục cho phù hợp")
            }
        }
        
        // Humidity reasoning with health considerations
        when {
            factors.humidityScore > 80f -> {
                reasoning.add("Độ ẩm ${humidity}% thoải mái, không gây khô da hay khó thở")
            }
            factors.humidityScore < 50f -> {
                when {
                    humidity > 85 -> reasoning.add("Độ ẩm ${humidity}% cao, có thể gây bức bí và khó chịu")
                    humidity < 30 -> reasoning.add("Độ ẩm ${humidity}% thấp, cần bổ sung nước và dưỡng ẩm da")
                    else -> reasoning.add("Độ ẩm ${humidity}% ảnh hưởng đến cảm giác thoải mái")
                }
            }
        }
        
        // Wind reasoning with activity impact
        when {
            factors.windScore > 80f -> {
                reasoning.add("Gió nhẹ ${windSpeed} m/s tạo cảm giác dễ chịu")
            }
            factors.windScore < 60f -> {
                when {
                    windSpeed > 15 -> reasoning.add("Gió mạnh ${windSpeed} m/s có thể ảnh hưởng đến di chuyển")
                    windSpeed > 10 -> reasoning.add("Gió vừa ${windSpeed} m/s cần chú ý khi ra ngoài")
                }
            }
        }
        
        // Weather condition specific reasoning
        when {
            "mưa cường độ nặng" in description.lowercase() -> {
                reasoning.add("Mưa lớn - nên ở trong nhà và tránh di chuyển không cần thiết")
            }
            "mưa" in description.lowercase() -> {
                reasoning.add("Có mưa - cần mang ô và mặc áo mưa khi ra ngoài")
            }
            "nắng" in description.lowercase() || "clear" in description.lowercase() -> {
                reasoning.add("Trời nắng đẹp - thời gian tuyệt vời cho các hoạt động ngoài trời")
            }
            "sương mù" in description.lowercase() || "fog" in description.lowercase() -> {
                reasoning.add("Có sương mù - cần cẩn thận khi lái xe và di chuyển")
            }
        }
        
        // Occupation-specific reasoning with detailed analysis
        when (occupation) {
            Occupation.OUTDOOR_WORKER, Occupation.CONSTRUCTION -> {
                when {
                    factors.occupationScore > 80f -> 
                        reasoning.add("Điều kiện làm việc ngoài trời rất thuận lợi và an toàn")
                    factors.occupationScore < 50f -> 
                        reasoning.add("Thời tiết khó khăn cho công việc ngoài trời, cần biện pháp bảo vệ")
                    else -> 
                        reasoning.add("Điều kiện làm việc ngoài trời chấp nhận được với chuẩn bị phù hợp")
                }
            }
            Occupation.HEALTHCARE -> {
                when {
                    factors.occupationScore > 80f -> 
                        reasoning.add("Thời tiết ổn định, tốt cho sức khỏe bệnh nhân và nhân viên y tế")
                    factors.occupationScore < 60f -> 
                        reasoning.add("Thời tiết có thể ảnh hưởng đến sức khỏe, cần chú ý bệnh nhân nhạy cảm")
                }
            }
            Occupation.OFFICE_WORKER -> {
                when {
                    factors.occupationScore > 80f -> 
                        reasoning.add("Thời tiết thuận lợi cho việc đi lại và làm việc")
                    factors.occupationScore < 60f -> 
                        reasoning.add("Thời tiết có thể ảnh hưởng đến việc di chuyển đến công sở")
                }
            }
            else -> {
                if (factors.occupationScore < 60f) {
                    reasoning.add("Thời tiết có thể ảnh hưởng đến hoạt động công việc hàng ngày")
                }
            }
        }
        
        // Age-specific health considerations
        when (ageCategory) {
            AgeCategory.CHILD, AgeCategory.SENIOR -> {
                if (factors.ageScore < 70f) {
                    reasoning.add("Cần đặc biệt chú ý sức khỏe trong điều kiện thời tiết này")
                }
            }
            AgeCategory.TEEN, AgeCategory.YOUNG_ADULT -> {
                if (factors.ageScore > 85f) {
                    reasoning.add("Thời tiết tuyệt vời cho các hoạt động thể thao và giải trí")
                }
            }
            AgeCategory.ADULT -> {
                if (factors.ageScore < 60f) {
                    reasoning.add("Thời tiết có thể ảnh hưởng đến năng suất làm việc")
                }
            }
        }
        
        // Pressure impact reasoning
        if (weatherData.pressure < 1000) {
            reasoning.add("Áp suất thấp có thể gây mệt mỏi và đau đầu cho một số người")
        }
        
        // Visibility safety reasoning
        if (weatherData.visibility < 5000) {
            reasoning.add("Tầm nhìn hạn chế, cần cẩn thận khi lái xe và di chuyển")
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
data class ScoreWeights(
    val temperature: Float,
    val humidity: Float,
    val wind: Float,
    val visibility: Float,
    val comfort: Float,
    val health: Float,
    val activity: Float,
    val occupation: Float,
    val age: Float,
    val pressure: Float
) {
    val total: Float = temperature + humidity + wind + visibility + comfort + health + activity + occupation + age + pressure
}