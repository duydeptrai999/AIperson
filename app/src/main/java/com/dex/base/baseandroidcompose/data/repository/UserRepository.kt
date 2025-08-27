package com.dex.base.baseandroidcompose.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.dex.base.baseandroidcompose.data.models.UserProfile
import com.dex.base.baseandroidcompose.utils.Logger
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing user profile data persistence
 * Uses SharedPreferences with Gson for serialization
 */
@Singleton
class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )
    
    private val gson = Gson()
    
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    
    init {
        loadUserProfile()
    }
    
    /**
     * Save user profile to SharedPreferences
     * Uses IO dispatcher to prevent ANR on main thread
     */
    suspend fun saveUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val jsonString = gson.toJson(userProfile)
                sharedPreferences.edit()
                    .putString(KEY_USER_PROFILE, jsonString)
                    .commit() // Use commit() in IO thread for immediate write
            }
            
            // Update StateFlow on main thread
            _userProfile.value = userProfile
            Logger.d("User profile saved successfully: ${userProfile.id}")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Logger.e("Failed to save user profile: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Load user profile from SharedPreferences
     */
    private fun loadUserProfile() {
        try {
            val jsonString = sharedPreferences.getString(KEY_USER_PROFILE, null)
            if (jsonString != null) {
                val userProfile = gson.fromJson(jsonString, UserProfile::class.java)
                _userProfile.value = userProfile
                Logger.d("User profile loaded successfully: ${userProfile.id}")
            } else {
                Logger.d("No saved user profile found")
                _userProfile.value = null
            }
        } catch (e: JsonSyntaxException) {
            Logger.e("Failed to parse user profile JSON: ${e.message}")
            _userProfile.value = null
        } catch (e: Exception) {
            Logger.e("Failed to load user profile: ${e.message}")
            _userProfile.value = null
        }
    }
    
    /**
     * Get current user profile synchronously
     */
    fun getCurrentUserProfile(): UserProfile? {
        return _userProfile.value
    }
    
    /**
     * Check if user profile exists
     */
    fun hasUserProfile(): Boolean {
        return _userProfile.value != null
    }
    
    /**
     * Update specific fields of user profile
     */
    suspend fun updateUserProfile(
        age: Int? = null,
        occupation: com.dex.base.baseandroidcompose.data.models.Occupation? = null,
        location: com.dex.base.baseandroidcompose.data.models.Location? = null,
        preferences: com.dex.base.baseandroidcompose.data.models.WeatherPreferences? = null,
        pointBalance: Int? = null,
        totalPointsEarned: Int? = null,
        level: Int? = null
    ): Result<Unit> {
        val currentProfile = _userProfile.value
        if (currentProfile == null) {
            Logger.e("Cannot update user profile: No existing profile found")
            return Result.failure(IllegalStateException("No existing user profile"))
        }
        
        val updatedProfile = currentProfile.copy(
            age = age ?: currentProfile.age,
            occupation = occupation ?: currentProfile.occupation,
            location = location ?: currentProfile.location,
            preferences = preferences ?: currentProfile.preferences,
            pointBalance = pointBalance ?: currentProfile.pointBalance,
            totalPointsEarned = totalPointsEarned ?: currentProfile.totalPointsEarned,
            level = level ?: currentProfile.level,
            lastUpdated = System.currentTimeMillis()
        )
        
        return saveUserProfile(updatedProfile)
    }
    
    /**
     * Clear user profile data
     */
    suspend fun clearUserProfile(): Result<Unit> {
        return try {
            sharedPreferences.edit()
                .remove(KEY_USER_PROFILE)
                .apply()
            
            _userProfile.value = null
            Logger.d("User profile cleared successfully")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Logger.e("Failed to clear user profile: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Get user profile creation timestamp
     */
    fun getProfileCreationTime(): Long? {
        return _userProfile.value?.createdAt
    }
    
    /**
     * Get user profile last update timestamp
     */
    fun getProfileLastUpdateTime(): Long? {
        return _userProfile.value?.lastUpdated
    }
    
    /**
     * Check if profile needs update (older than specified days)
     */
    fun isProfileOutdated(maxDaysOld: Int = 30): Boolean {
        val lastUpdate = getProfileLastUpdateTime() ?: return true
        val maxAge = maxDaysOld * 24 * 60 * 60 * 1000L // Convert days to milliseconds
        return (System.currentTimeMillis() - lastUpdate) > maxAge
    }
    
    companion object {
        private const val PREFS_NAME = "weather_app_user_prefs"
        private const val KEY_USER_PROFILE = "user_profile"
    }
}