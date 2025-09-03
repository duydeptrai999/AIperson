package com.dex.base.baseandroidcompose.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO cho hệ thống điểm danh hàng ngày
 */
@Dao
interface DailyCheckInDao {
    
    @Query("SELECT * FROM daily_check_in WHERE user_id = :userId ORDER BY check_in_date DESC")
    suspend fun getUserCheckIns(userId: String): List<DailyCheckInEntity>
    
    @Query("SELECT * FROM daily_check_in WHERE user_id = :userId ORDER BY check_in_date DESC")
    fun getUserCheckInsFlow(userId: String): Flow<List<DailyCheckInEntity>>
    
    @Query("SELECT * FROM daily_check_in WHERE user_id = :userId AND check_in_date = :date")
    suspend fun getCheckInForDate(userId: String, date: String): DailyCheckInEntity?
    
    @Query("SELECT * FROM daily_check_in WHERE user_id = :userId AND check_in_date = :date")
    fun getCheckInForDateFlow(userId: String, date: String): Flow<DailyCheckInEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: DailyCheckInEntity)
    
    @Update
    suspend fun updateCheckIn(checkIn: DailyCheckInEntity)
    
    @Query("SELECT COUNT(*) FROM daily_check_in WHERE user_id = :userId AND is_checked_in = 1")
    suspend fun getTotalCheckIns(userId: String): Int
    
    @Query("SELECT * FROM daily_check_in WHERE user_id = :userId AND is_checked_in = 1 ORDER BY check_in_date DESC LIMIT 7")
    suspend fun getLastSevenCheckIns(userId: String): List<DailyCheckInEntity>
    
    @Query("DELETE FROM daily_check_in WHERE user_id = :userId")
    suspend fun deleteUserCheckIns(userId: String)
}

/**
 * DAO cho hệ thống streak
 */
@Dao
interface UserStreakDao {
    
    @Query("SELECT * FROM user_streak WHERE user_id = :userId")
    suspend fun getUserStreak(userId: String): UserStreakEntity?
    
    @Query("SELECT * FROM user_streak WHERE user_id = :userId")
    fun getUserStreakFlow(userId: String): Flow<UserStreakEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStreak(streak: UserStreakEntity)
    
    @Update
    suspend fun updateStreak(streak: UserStreakEntity)
    
    @Query("UPDATE user_streak SET current_streak = :newStreak, last_check_in_date = :date, total_check_ins = total_check_ins + 1, last_updated = :timestamp WHERE user_id = :userId")
    suspend fun updateStreakCount(userId: String, newStreak: Int, date: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_streak SET longest_streak = :longestStreak WHERE user_id = :userId AND longest_streak < :longestStreak")
    suspend fun updateLongestStreak(userId: String, longestStreak: Int)
    
    @Query("UPDATE user_streak SET current_streak = 0, last_updated = :timestamp WHERE user_id = :userId")
    suspend fun resetStreak(userId: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("DELETE FROM user_streak WHERE user_id = :userId")
    suspend fun deleteUserStreak(userId: String)
}

/**
 * DAO cho hệ thống thử thách hàng ngày
 */
@Dao
interface DailyChallengeDao {
    
    @Query("SELECT * FROM daily_challenges WHERE user_id = :userId ORDER BY challenge_date DESC")
    suspend fun getUserChallenges(userId: String): List<DailyChallengeEntity>
    
    @Query("SELECT * FROM daily_challenges WHERE user_id = :userId ORDER BY challenge_date DESC")
    fun getUserChallengesFlow(userId: String): Flow<List<DailyChallengeEntity>>
    
    @Query("SELECT * FROM daily_challenges WHERE user_id = :userId AND challenge_date = :date AND is_completed = 0")
    suspend fun getActiveChallengesForDate(userId: String, date: String): List<DailyChallengeEntity>
    
    @Query("SELECT * FROM daily_challenges WHERE user_id = :userId AND challenge_date = :date")
    fun getChallengesForDateFlow(userId: String, date: String): Flow<List<DailyChallengeEntity>>
    
    @Query("SELECT * FROM daily_challenges WHERE user_id = :userId AND challenge_type = :type AND challenge_date = :date")
    suspend fun getChallengeByType(userId: String, type: String, date: String): DailyChallengeEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: DailyChallengeEntity)
    
    @Update
    suspend fun updateChallenge(challenge: DailyChallengeEntity)
    
    @Query("UPDATE daily_challenges SET current_progress = :progress, is_completed = :isCompleted, completed_at = :completedAt WHERE id = :challengeId")
    suspend fun updateChallengeProgress(challengeId: String, progress: Int, isCompleted: Boolean, completedAt: Long?)
    
    @Query("SELECT COUNT(*) FROM daily_challenges WHERE user_id = :userId AND is_completed = 1")
    suspend fun getTotalCompletedChallenges(userId: String): Int
    
    @Query("DELETE FROM daily_challenges WHERE user_id = :userId AND expires_at < :currentTime")
    suspend fun deleteExpiredChallenges(userId: String, currentTime: Long)
    
    @Query("DELETE FROM daily_challenges WHERE user_id = :userId")
    suspend fun deleteUserChallenges(userId: String)
}