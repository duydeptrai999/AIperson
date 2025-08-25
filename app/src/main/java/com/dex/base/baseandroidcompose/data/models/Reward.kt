package com.dex.base.baseandroidcompose.data.models

/**
 * Reward system data models
 */
data class Reward(
    val id: String,
    val type: RewardType,
    val title: String,
    val description: String,
    val content: String,
    val pointCost: Int,
    val category: RewardCategory,
    val icon: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val priority: Int = 0,
    val validUntil: Long? = null
)

enum class RewardType {
    WEATHER_TIP,
    HEALTH_ADVICE,
    ACTIVITY_SUGGESTION,
    FASHION_TIP,
    PREMIUM_FEATURE,
    ACHIEVEMENT_BADGE,
    DISCOUNT_COUPON,
    EXCLUSIVE_CONTENT
}

enum class RewardCategory {
    DAILY_TIPS("Mẹo hàng ngày", "💡"),
    HEALTH_WELLNESS("Sức khỏe", "🏥"),
    OUTDOOR_ACTIVITIES("Hoạt động ngoài trời", "🏃"),
    FASHION_STYLE("Thời trang", "👕"),
    PREMIUM_FEATURES("Tính năng cao cấp", "⭐"),
    ACHIEVEMENTS("Thành tích", "🏆"),
    SPECIAL_OFFERS("Ưu đãi đặc biệt", "🎁"),
    SEASONAL_CONTENT("Nội dung theo mùa", "🌸");
    
    constructor(displayName: String, emoji: String) {
        this.displayName = displayName
        this.emoji = emoji
    }
    
    val displayName: String
    val emoji: String
}

/**
 * User's reward history and statistics
 */
data class RewardHistory(
    val userId: String,
    val totalRewardsUnlocked: Int,
    val totalPointsSpent: Int,
    val favoriteCategory: RewardCategory,
    val recentRewards: List<UnlockedReward>,
    val streakDays: Int,
    val lastClaimDate: String
)

data class UnlockedReward(
    val reward: Reward,
    val unlockedAt: Long,
    val pointsSpent: Int
)

/**
 * Point transaction record
 */
data class PointTransaction(
    val id: String,
    val userId: String,
    val type: TransactionType,
    val points: Int,
    val description: String,
    val relatedRewardId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class TransactionType {
    EARNED_DAILY,
    EARNED_BONUS,
    SPENT_REWARD,
    EARNED_ACHIEVEMENT,
    EARNED_STREAK
}

/**
 * Level system for gamification
 */
data class UserLevel(
    val level: Int,
    val title: String,
    val minPoints: Int,
    val maxPoints: Int,
    val benefits: List<String>,
    val icon: String
) {
    companion object {
        fun getLevels(): List<UserLevel> {
            return listOf(
                UserLevel(1, "Người mới", 0, 99, listOf("Truy cập cơ bản"), "🌱"),
                UserLevel(2, "Người dùng", 100, 299, listOf("Thêm mẹo thời tiết"), "🌿"),
                UserLevel(3, "Chuyên gia", 300, 599, listOf("Dự báo 7 ngày", "Thông báo nâng cao"), "🌳"),
                UserLevel(4, "Bậc thầy", 600, 999, listOf("AI cá nhân hóa nâng cao", "Phân tích chi tiết"), "🏆"),
                UserLevel(5, "Huyền thoại", 1000, Int.MAX_VALUE, listOf("Tất cả tính năng", "Ưu tiên hỗ trợ"), "👑")
            )
        }
        
        fun getLevelFromPoints(points: Int): UserLevel {
            return getLevels().lastOrNull { points >= it.minPoints } ?: getLevels().first()
        }
        
        fun getProgressToNextLevel(currentPoints: Int): Float {
            val currentLevel = getLevelFromPoints(currentPoints)
            val nextLevel = getLevels().find { it.level > currentLevel.level }
            
            return if (nextLevel != null) {
                val progress = currentPoints - currentLevel.minPoints
                val total = nextLevel.minPoints - currentLevel.minPoints
                progress.toFloat() / total.toFloat()
            } else {
                1.0f // Max level reached
            }
        }
    }
}

/**
 * Daily reward check-in system
 */
data class DailyCheckIn(
    val date: String,
    val isCheckedIn: Boolean,
    val pointsEarned: Int,
    val bonusMultiplier: Float = 1.0f,
    val streakDay: Int
)