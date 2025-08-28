package com.dex.base.baseandroidcompose.ui.viewmodels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dex.base.baseandroidcompose.ads.AdManager
import com.dex.base.baseandroidcompose.data.database.UserPointsDao
import com.dex.base.baseandroidcompose.data.database.PointTransactionDao
import com.dex.base.baseandroidcompose.data.database.UserPointsEntity
import com.dex.base.baseandroidcompose.data.database.PointTransactionEntity
import com.dex.base.baseandroidcompose.data.repository.UserRepository
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
class RewardsViewModel @Inject constructor(
    private val userPointsDao: UserPointsDao,
    private val pointTransactionDao: PointTransactionDao,
    private val userRepository: UserRepository
) : ViewModel() {

    // State flows for UI
    private val _adStatus = MutableStateFlow("Not Ready")
    val adStatus: StateFlow<String> = _adStatus.asStateFlow()

    private val _isWatchingAd = MutableStateFlow(false)
    val isWatchingAd: StateFlow<Boolean> = _isWatchingAd.asStateFlow()

    private val _lastEarnedPoints = MutableStateFlow(0)
    val lastEarnedPoints: StateFlow<Int> = _lastEarnedPoints.asStateFlow()

    private val _totalAdsWatched = MutableStateFlow(0)
    val totalAdsWatched: StateFlow<Int> = _totalAdsWatched.asStateFlow()

    private val _userPoints = MutableStateFlow(0)
    val userPoints: StateFlow<Int> = _userPoints.asStateFlow()
    
    private val _pointsFromAds = MutableStateFlow(0)
    val pointsFromAds: StateFlow<Int> = _pointsFromAds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        // Load user points when ViewModel is created
        loadUserPoints()
    }
    
    /**
     * Load điểm người dùng từ database
     */
    private fun loadUserPoints() {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUserProfile()
                val userId = currentUser?.id ?: "default_user"
                
                val userPoints = userPointsDao.getUserPoints(userId)
                if (userPoints != null) {
                    _userPoints.value = userPoints.totalPoints
                    _pointsFromAds.value = userPoints.pointsFromAds
                } else {
                    // Create new user points record
                    val newUserPoints = UserPointsEntity(
                        userId = userId,
                        totalPoints = 0,
                        pointsFromAds = 0
                    )
                    userPointsDao.insertOrUpdateUserPoints(newUserPoints)
                }
                
                Logger.d("RewardsViewModel: User points loaded - Total: ${_userPoints.value}, From Ads: ${_pointsFromAds.value}")
            } catch (e: Exception) {
                Logger.e("RewardsViewModel: Error loading user points: ${e.message}")
                _errorMessage.value = e.message
            }
        }
    }

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
                
                // Save points to database
                savePointsToDatabase(points)
                
                // Update local state
                _lastEarnedPoints.value = points
                _totalAdsWatched.value += 1
                
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
     * Lưu điểm vào Room Database
     */
    private suspend fun savePointsToDatabase(pointsEarned: Int) {
        try {
            val currentUser = userRepository.getCurrentUserProfile()
            val userId = currentUser?.id ?: "default_user"
            
            // Add points from ads to database
            userPointsDao.addPointsFromAds(userId, pointsEarned)
            
            // Create transaction record
            val transaction = PointTransactionEntity(
                userId = userId,
                pointsEarned = pointsEarned,
                transactionType = "AD_REWARD",
                description = "Points earned from watching rewarded ad"
            )
            pointTransactionDao.insertTransaction(transaction)
            
            // Update local state from database
            val updatedUserPoints = userPointsDao.getUserPoints(userId)
            if (updatedUserPoints != null) {
                _userPoints.value = updatedUserPoints.totalPoints
                _pointsFromAds.value = updatedUserPoints.pointsFromAds
            }
            
            Logger.d("RewardsViewModel: Points saved to database - Earned: $pointsEarned, Total: ${_userPoints.value}")
            
        } catch (e: Exception) {
            Logger.e("RewardsViewModel: Error saving points to database: ${e.message}")
            throw e
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