package com.dex.base.baseandroidcompose.di

import com.dex.base.baseandroidcompose.data.ai.WeatherCompatibilityEngine
import com.dex.base.baseandroidcompose.data.repository.WeatherRepository
import com.dex.base.baseandroidcompose.data.api.AIHealthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
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
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)  // Giảm timeout để phát hiện lỗi sớm hơn
            .readTimeout(120, TimeUnit.SECONDS)    // Tăng read timeout cho AI processing
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)        // Tự động retry khi connection fail
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://ai.dreamapi.net/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideAIHealthService(retrofit: Retrofit): AIHealthService {
        return retrofit.create(AIHealthService::class.java)
    }
}