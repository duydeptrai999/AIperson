package com.dex.base.baseandroidcompose.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_points")
data class UserPointsEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "total_points")
    val totalPoints: Int = 0,
    
    @ColumnInfo(name = "points_from_ads")
    val pointsFromAds: Int = 0,
    
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "point_transactions")
data class PointTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "points_earned")
    val pointsEarned: Int,
    
    @ColumnInfo(name = "transaction_type")
    val transactionType: String,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)