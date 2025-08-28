package com.dex.base.baseandroidcompose.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for user points operations
 */
@Dao
interface UserPointsDao {
    
    @Query("SELECT * FROM user_points WHERE user_id = :userId")
    suspend fun getUserPoints(userId: String): UserPointsEntity?
    
    @Query("SELECT * FROM user_points WHERE user_id = :userId")
    fun getUserPointsFlow(userId: String): Flow<UserPointsEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserPoints(userPoints: UserPointsEntity)
    
    @Update
    suspend fun updateUserPoints(userPoints: UserPointsEntity)
    
    @Query("UPDATE user_points SET total_points = total_points + :points, points_from_ads = points_from_ads + :points, last_updated = :timestamp WHERE user_id = :userId")
    suspend fun addPointsFromAd(userId: String, points: Int, timestamp: Long = System.currentTimeMillis())
    
    @Query("DELETE FROM user_points WHERE user_id = :userId")
    suspend fun deleteUserPoints(userId: String)
    
    @Query("UPDATE user_points SET points_from_ads = points_from_ads + :points, total_points = total_points + :points, last_updated = :timestamp WHERE user_id = :userId")
    suspend fun addPointsFromAds(userId: String, points: Int, timestamp: Long = System.currentTimeMillis())
}