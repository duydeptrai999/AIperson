package com.dex.base.baseandroidcompose.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for point transactions
 */
@Dao
interface PointTransactionDao {
    
    @Query("SELECT * FROM point_transactions WHERE user_id = :userId ORDER BY timestamp DESC")
    suspend fun getUserTransactions(userId: String): List<PointTransactionEntity>
    
    @Query("SELECT * FROM point_transactions WHERE user_id = :userId ORDER BY timestamp DESC")
    fun getUserTransactionsFlow(userId: String): Flow<List<PointTransactionEntity>>
    
    @Insert
    suspend fun insertTransaction(transaction: PointTransactionEntity)
    
    @Query("SELECT COUNT(*) FROM point_transactions WHERE user_id = :userId AND transaction_type = 'AD_REWARD' AND timestamp > :since")
    suspend fun getAdRewardCountSince(userId: String, since: Long): Int
    
    @Query("DELETE FROM point_transactions WHERE user_id = :userId")
    suspend fun deleteUserTransactions(userId: String)
}