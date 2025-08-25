package com.dex.base.baseandroidcompose.di

import com.dex.base.baseandroidcompose.data.ai.WeatherCompatibilityEngine
import com.dex.base.baseandroidcompose.data.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideWeatherRepository(): WeatherRepository {
        return WeatherRepository()
    }
    
    @Provides
    @Singleton
    fun provideWeatherCompatibilityEngine(): WeatherCompatibilityEngine {
        return WeatherCompatibilityEngine()
    }
}