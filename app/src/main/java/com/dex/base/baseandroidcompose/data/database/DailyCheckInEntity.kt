package com.dex.base.baseandroidcompose.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity cho hệ thống điểm danh hàng ngày
 */
@Entity(tableName = "daily_check_in")
data class DailyCheckInEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // Format: "userId_YYYY-MM-DD"
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "check_in_date")
    val checkInDate: String, // Format: "YYYY-MM-DD"
    
    @ColumnInfo(name = "is_checked_in")
    val isCheckedIn: Boolean = false,
    
    @ColumnInfo(name = "points_earned")
    val pointsEarned: Int = 0,
    
    @ColumnInfo(name = "streak_day")
    val streakDay: Int = 1,
    
    @ColumnInfo(name = "bonus_multiplier")
    val bonusMultiplier: Float = 1.0f,
    
    @ColumnInfo(name = "check_in_time")
    val checkInTime: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entity cho hệ thống streak (chuỗi ngày liên tiếp)
 */
@Entity(tableName = "user_streak")
data class UserStreakEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "current_streak")
    val currentStreak: Int = 0,
    
    @ColumnInfo(name = "longest_streak")
    val longestStreak: Int = 0,
    
    @ColumnInfo(name = "last_check_in_date")
    val lastCheckInDate: String? = null, // Format: "YYYY-MM-DD"
    
    @ColumnInfo(name = "total_check_ins")
    val totalCheckIns: Int = 0,
    
    @ColumnInfo(name = "streak_bonus_earned")
    val streakBonusEarned: Int = 0,
    
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entity cho hệ thống thử thách hàng ngày
 */
@Entity(tableName = "daily_challenges")
data class DailyChallengeEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // Format: "userId_YYYY-MM-DD_challengeType"
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "challenge_date")
    val challengeDate: String, // Format: "YYYY-MM-DD"
    
    @ColumnInfo(name = "challenge_type")
    val challengeType: String, // "WEATHER_CHECK", "AD_WATCH", "PROFILE_UPDATE", "SHARE_APP"
    
    @ColumnInfo(name = "challenge_title")
    val challengeTitle: String,
    
    @ColumnInfo(name = "challenge_description")
    val challengeDescription: String,
    
    @ColumnInfo(name = "target_value")
    val targetValue: Int, // Số lượng cần hoàn thành
    
    @ColumnInfo(name = "current_progress")
    val currentProgress: Int = 0,
    
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    
    @ColumnInfo(name = "points_reward")
    val pointsReward: Int,
    
    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null,
    
    @ColumnInfo(name = "expires_at")
    val expiresAt: Long, // Thời gian hết hạn challenge
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)