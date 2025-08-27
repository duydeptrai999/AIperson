package com.dex.base.baseandroidcompose.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dex.base.baseandroidcompose.data.models.UserProfile
import com.dex.base.baseandroidcompose.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadUserProfile()
    }

    /**
     * Load user profile from repository
     */
    private fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Collect from repository's StateFlow
                userRepository.userProfile.collect { profile ->
                    _userProfile.value = profile
                    _error.value = null
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    /**
     * Save user profile
     */
    fun saveUserProfile(userProfile: UserProfile) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = userRepository.saveUserProfile(userProfile)
                if (result.isSuccess) {
                    _userProfile.value = userProfile
                    _error.value = null
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to save user profile"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update user points
     */
    fun updateUserPoints(points: Int) {
        viewModelScope.launch {
            try {
                val currentProfile = _userProfile.value
                if (currentProfile != null) {
                    val updatedProfile = currentProfile.copy(
                        pointBalance = currentProfile.pointBalance + points,
                        totalPointsEarned = currentProfile.totalPointsEarned + points
                    )
                    userRepository.saveUserProfile(updatedProfile)
                    _userProfile.value = updatedProfile
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * Clear user profile
     */
    fun clearUserProfile() {
        viewModelScope.launch {
            try {
                val result = userRepository.clearUserProfile()
                if (result.isSuccess) {
                    _userProfile.value = null
                    _error.value = null
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to clear user profile"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * Refresh user profile
     */
    fun refreshUserProfile() {
        loadUserProfile()
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Check if user profile exists
     */
    fun hasUserProfile(): Boolean {
        return _userProfile.value != null
    }

    /**
     * Get current user profile (nullable)
     */
    fun getCurrentUserProfile(): UserProfile? {
        return _userProfile.value
    }
}