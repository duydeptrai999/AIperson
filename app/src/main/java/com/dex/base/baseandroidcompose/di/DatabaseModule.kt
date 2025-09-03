package com.dex.base.baseandroidcompose.di

import android.content.Context
import androidx.room.Room
import com.dex.base.baseandroidcompose.data.database.AppDatabase
import com.dex.base.baseandroidcompose.data.database.UserPointsDao
import com.dex.base.baseandroidcompose.data.database.PointTransactionDao
import com.dex.base.baseandroidcompose.data.database.DailyCheckInDao
import com.dex.base.baseandroidcompose.data.database.UserStreakDao
import com.dex.base.baseandroidcompose.data.database.DailyChallengeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "weather_app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun provideUserPointsDao(database: AppDatabase): UserPointsDao {
        return database.userPointsDao()
    }
    
    @Provides
    fun providePointTransactionDao(database: AppDatabase): PointTransactionDao {
        return database.pointTransactionDao()
    }
    
    @Provides
    fun provideDailyCheckInDao(database: AppDatabase): DailyCheckInDao {
        return database.dailyCheckInDao()
    }
    
    @Provides
    fun provideUserStreakDao(database: AppDatabase): UserStreakDao {
        return database.userStreakDao()
    }
    
    @Provides
    fun provideDailyChallengeDao(database: AppDatabase): DailyChallengeDao {
        return database.dailyChallengeDao()
    }
}