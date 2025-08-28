package com.dex.base.baseandroidcompose.di

import android.content.Context
import androidx.room.Room
import com.dex.base.baseandroidcompose.data.database.AppDatabase
import com.dex.base.baseandroidcompose.data.database.UserPointsDao
import com.dex.base.baseandroidcompose.data.database.PointTransactionDao
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
}