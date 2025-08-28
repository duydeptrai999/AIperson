package com.dex.base.baseandroidcompose.ui.viewmodels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dex.base.baseandroidcompose.ads.AdManager
import com.dex.base.baseandroidcompose.utils.Logger
import com.google.android.gms.ads.rewarded.RewardItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * RewardsViewModel - Quản lý state cho màn hình reward
 * Tích hợp với AdManager để xử lý quảng cáo reward
 */
@HiltViewModel
class RewardsViewModel @Inject constructor() : ViewModel() {

    // State flows for UI
    private val _adStatus = MutableStateFlow("Not Ready")
    val adStatus: StateFlow<String> = _adStatus.asStateFlow()

    private val _isWatchingAd = MutableStateFlow(false)
    val isWatchingAd: StateFlow<Boolean> = _isWatchingAd.asStateFlow()

    private val _lastEarnedPoints = MutableStateFlow(0)
    val lastEarnedPoints: StateFlow<Int> = _lastEarnedPoints.asStateFlow()

    private val _totalAdsWatched = MutableStateFlow(0)
    val totalAdsWatched: StateFlow<Int> = _totalAdsWatched.asStateFlow()

    private val _userPoints = MutableStateFlow(1250) // Default points
    val userPoints: StateFlow<Int> = _userPoints.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Khởi tạo ads khi ViewModel được tạo
     */
    fun initializeAds(context: Context) {
        viewModelScope.launch {
            try {
                Logger.d("RewardsViewModel: Initializing ads...")
                _isLoading.value = true
                
                // Preload reward ad
                AdManager.preloadRewardAd(context)
                
                // Update ad status
                updateAdStatus()
                
                _isLoading.value = false
                Logger.d("RewardsViewModel: Ads initialized successfully")
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message
                Logger.e("RewardsViewModel: Failed to initialize ads: ${e.message}")
            }
        }
    }

    /**
     * Cập nhật trạng thái quảng cáo
     */
    private fun updateAdStatus() {
        _adStatus.value = AdManager.getRewardAdStatus()
    }

    /**
     * Kiểm tra xem quảng cáo có sẵn sàng không
     */
    fun isAdReady(): Boolean {
        return AdManager.isRewardAdReady()
    }

    /**
     * Lấy trạng thái hiện tại của quảng cáo
     */
    fun getCurrentAdStatus(): String {
        return AdManager.getRewardAdStatus()
    }

    /**
     * Hiển thị quảng cáo reward
     */
    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: (Int) -> Unit = {}
    ) {
        if (!isAdReady()) {
            Logger.w("RewardsViewModel: Ad not ready, current status: ${_adStatus.value}")
            _errorMessage.value = "Quảng cáo chưa sẵn sàng. Vui lòng thử lại sau."
            return
        }

        viewModelScope.launch {
            try {
                _isWatchingAd.value = true
                _errorMessage.value = null
                
                AdManager.showRewardAd(
                    activity = activity,
                    onUserEarnedReward = { rewardItem ->
                        handleRewardEarned(rewardItem, onRewardEarned)
                    },
                    onAdClosed = {
                        _isWatchingAd.value = false
                        Logger.d("RewardsViewModel: Ad closed, preloading next ad")
                        // Preload next ad
                        AdManager.preloadRewardAd(activity)
                        updateAdStatus()
                    },
                    onAdNotReady = {
                        _isWatchingAd.value = false
                        _errorMessage.value = "Quảng cáo không khả dụng"
                        Logger.e("RewardsViewModel: Ad not ready to show")
                    }
                )
            } catch (e: Exception) {
                _isWatchingAd.value = false
                _errorMessage.value = e.message
                Logger.e("RewardsViewModel: Error showing rewarded ad: ${e.message}")
            }
        }
    }

    /**
     * Xử lý khi người dùng nhận được phần thưởng
     */
    private fun handleRewardEarned(
        rewardItem: RewardItem,
        onRewardEarned: (Int) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val points = rewardItem.amount
                
                // Cập nhật state
                _lastEarnedPoints.value = points
                _totalAdsWatched.value += 1
                _userPoints.value += points
                
                // Callback cho UI
                onRewardEarned(points)
                
                Logger.d("RewardsViewModel: User earned $points points. Total ads watched: ${_totalAdsWatched.value}")
                Logger.d("RewardsViewModel: Total user points: ${_userPoints.value}")
                
            } catch (e: Exception) {
                _errorMessage.value = e.message
                Logger.e("RewardsViewModel: Error handling reward earned: ${e.message}")
            }
        }
    }

    /**
     * Refresh quảng cáo
     */
    fun refreshAd(context: Context) {
        viewModelScope.launch {
            try {
                Logger.d("RewardsViewModel: Refreshing ad")
                AdManager.preloadRewardAd(context)
                updateAdStatus()
            } catch (e: Exception) {
                _errorMessage.value = e.message
                Logger.e("RewardsViewModel: Error refreshing ad: ${e.message}")
            }
        }
    }

    /**
     * Retry loading quảng cáo
     */
    fun retryLoadAd(context: Context) {
        viewModelScope.launch {
            try {
                Logger.d("RewardsViewModel: Retrying to load ad")
                _isLoading.value = true
                AdManager.retryRewardAd(context)
                updateAdStatus()
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message
                Logger.e("RewardsViewModel: Error retrying ad load: ${e.message}")
            }
        }
    }

    /**
     * Force reload quảng cáo
     */
    fun forceReloadAd(context: Context) {
        viewModelScope.launch {
            try {
                Logger.d("RewardsViewModel: Force reloading ad")
                _isLoading.value = true
                AdManager.forceReloadRewardAd(context)
                updateAdStatus()
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message
                Logger.e("RewardsViewModel: Error force reloading ad: ${e.message}")
            }
        }
    }

    /**
     * Reset điểm vừa nhận được
     */
    fun resetLastEarnedPoints() {
        _lastEarnedPoints.value = 0
    }

    /**
     * Clear error message
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Cleanup khi ViewModel bị destroy
     */
    override fun onCleared() {
        super.onCleared()
        Logger.d("RewardsViewModel: Cleared")
    }
}