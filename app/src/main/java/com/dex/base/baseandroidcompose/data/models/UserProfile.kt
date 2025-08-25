package com.dex.base.baseandroidcompose.data.models

/**
 * User profile data for AI personalization
 */
data class UserProfile(
    val id: String,
    val age: Int,
    val location: Location,
    val occupation: Occupation,
    val preferences: WeatherPreferences,
    val pointBalance: Int,
    val totalPointsEarned: Int,
    val level: Int,
    val createdAt: Long,
    val lastUpdated: Long
) {
    companion object {
        fun getDefaultProfile(): UserProfile {
            return UserProfile(
                id = "default_user",
                age = 25,
                location = Location(
                    city = "Ho Chi Minh City",
                    country = "Vietnam",
                    latitude = 10.8231,
                    longitude = 106.6297,
                    timezone = "Asia/Ho_Chi_Minh"
                ),
                occupation = Occupation.OFFICE_WORKER,
                preferences = WeatherPreferences(),
                pointBalance = 0,
                totalPointsEarned = 0,
                level = 1,
                createdAt = System.currentTimeMillis(),
                lastUpdated = System.currentTimeMillis()
            )
        }
    }
}

data class Location(
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String
)

enum class Occupation(
    val displayName: String,
    val outdoorFactor: Float, // 0.0 to 1.0, higher means more affected by weather
    val healthSensitivity: Float // 0.0 to 1.0, higher means more health-conscious
) {
    OFFICE_WORKER("Nhân viên văn phòng", 0.3f, 0.5f),
    OUTDOOR_WORKER("Công nhân ngoài trời", 1.0f, 0.8f),
    HEALTHCARE("Y tế", 0.4f, 0.9f),
    EDUCATION("Giáo dục", 0.5f, 0.6f),
    STUDENT("Học sinh/Sinh viên", 0.6f, 0.4f),
    RETIRED("Nghỉ hưu", 0.4f, 0.8f),
    FREELANCER("Tự do", 0.5f, 0.5f),
    DRIVER("Tài xế", 0.8f, 0.6f),
    CONSTRUCTION("Xây dựng", 1.0f, 0.7f),
    SALES("Kinh doanh", 0.7f, 0.5f),
    OTHER("Khác", 0.5f, 0.5f)
}

data class WeatherPreferences(
    val preferredTemperatureRange: TemperatureRange = TemperatureRange(20.0, 28.0),
    val preferredHumidityRange: HumidityRange = HumidityRange(40, 70),
    val windSensitivity: Float = 0.5f, // 0.0 to 1.0
    val rainSensitivity: Float = 0.7f, // 0.0 to 1.0
    val sunlightPreference: Float = 0.6f, // 0.0 to 1.0
    val airQualitySensitivity: Float = 0.8f // 0.0 to 1.0
)

data class TemperatureRange(
    val min: Double, // Celsius
    val max: Double  // Celsius
)

data class HumidityRange(
    val min: Int, // Percentage
    val max: Int  // Percentage
)

/**
 * Age categories for AI personalization
 */
enum class AgeCategory {
    CHILD,      // 0-12
    TEEN,       // 13-17
    YOUNG_ADULT, // 18-25
    ADULT,      // 26-60
    SENIOR;     // 60+
    
    companion object {
        fun fromAge(age: Int): AgeCategory {
            return when (age) {
                in 0..12 -> CHILD
                in 13..17 -> TEEN
                in 18..25 -> YOUNG_ADULT
                in 26..60 -> ADULT
                else -> SENIOR
            }
        }
    }
}