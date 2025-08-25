package com.dex.base.baseandroidcompose.data.models

/**
 * Weather compatibility analysis result
 */
data class WeatherCompatibility(
    val date: String,
    val weatherData: WeatherData,
    val userProfile: UserProfile,
    val compatibilityScore: Float, // 0.0 to 100.0
    val pointsEarned: Int,
    val reasoning: List<String>,
    val recommendations: List<Recommendation>,
    val factors: CompatibilityFactors,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        /**
         * Calculate points based on compatibility score
         */
        fun calculatePoints(score: Float): Int {
            return when {
                score >= 90f -> 10
                score >= 70f -> 7
                score >= 50f -> 5
                score >= 30f -> 3
                else -> 1
            }
        }
        
        /**
         * Get score color based on compatibility
         */
        fun getScoreColor(score: Float): ScoreColor {
            return when {
                score >= 80f -> ScoreColor.EXCELLENT
                score >= 60f -> ScoreColor.GOOD
                score >= 40f -> ScoreColor.AVERAGE
                score >= 20f -> ScoreColor.POOR
                else -> ScoreColor.BAD
            }
        }
        
        /**
         * Get score description
         */
        fun getScoreDescription(score: Float): String {
            return when {
                score >= 90f -> "Thời tiết hoàn hảo"
                score >= 70f -> "Thời tiết tốt"
                score >= 50f -> "Thời tiết ổn"
                score >= 30f -> "Thời tiết không tốt"
                else -> "Thời tiết xấu"
            }
        }
    }
}

/**
 * Detailed factors affecting compatibility score
 */
data class CompatibilityFactors(
    val temperatureScore: Float,
    val humidityScore: Float,
    val windScore: Float,
    val visibilityScore: Float,
    val comfortScore: Float,
    val healthScore: Float,
    val activityScore: Float,
    val occupationScore: Float,
    val ageScore: Float
)

/**
 * Recommendation for user based on weather
 */
data class Recommendation(
    val type: RecommendationType,
    val title: String,
    val description: String,
    val priority: RecommendationPriority,
    val icon: String
)

enum class RecommendationType {
    CLOTHING,
    ACTIVITY,
    HEALTH,
    SAFETY,
    COMFORT
}

enum class RecommendationPriority {
    HIGH,
    MEDIUM,
    LOW
}

enum class ScoreColor {
    EXCELLENT, // Green
    GOOD,      // Light Green
    AVERAGE,   // Yellow
    POOR,      // Orange
    BAD        // Red
}

/**
 * Daily AI insights for user
 */
data class DailyAIInsights(
    val date: String,
    val greeting: String,
    val weatherSummary: String,
    val personalizedTips: List<String>,
    val healthAdvice: String,
    val activitySuggestions: List<String>,
    val pointsEarned: Int,
    val streakDays: Int,
    val achievements: List<Achievement>
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val pointsReward: Int,
    val unlockedAt: Long
)

/**
 * Weather forecast data
 */
data class WeatherForecast(
    val date: String,
    val dayOfWeek: String,
    val tempMin: Double,
    val tempMax: Double,
    val description: String,
    val icon: String,
    val precipitationChance: Int,
    val compatibilityScore: Float
)

/**
 * Quick stats for dashboard
 */
data class QuickStats(
    val todayScore: Float,
    val weeklyAverage: Float,
    val pointsToday: Int,
    val totalPoints: Int,
    val currentStreak: Int,
    val bestStreak: Int
)