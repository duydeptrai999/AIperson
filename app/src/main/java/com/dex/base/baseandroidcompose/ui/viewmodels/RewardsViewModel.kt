package com.dex.base.baseandroidcompose.ui.viewmodels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dex.base.baseandroidcompose.ads.AdManager
import com.dex.base.baseandroidcompose.data.database.*
import com.dex.base.baseandroidcompose.data.repository.UserRepository
import com.dex.base.baseandroidcompose.utils.Logger
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random
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
    private val dailyCheckInDao: DailyCheckInDao,
    private val userStreakDao: UserStreakDao,
    private val dailyChallengeDao: DailyChallengeDao,
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

    // Gamification States
    private val _dailyCheckInStatus = MutableStateFlow<DailyCheckInEntity?>(null)
    val dailyCheckInStatus: StateFlow<DailyCheckInEntity?> = _dailyCheckInStatus.asStateFlow()

    private val _userStreak = MutableStateFlow<UserStreakEntity?>(null)
    val userStreak: StateFlow<UserStreakEntity?> = _userStreak.asStateFlow()

    private val _dailyChallenges = MutableStateFlow<List<DailyChallengeEntity>>(emptyList())
    val dailyChallenges: StateFlow<List<DailyChallengeEntity>> = _dailyChallenges.asStateFlow()

    private val _canCheckInToday = MutableStateFlow(false)
    val canCheckInToday: StateFlow<Boolean> = _canCheckInToday.asStateFlow()

    private val _streakBonus = MutableStateFlow(0)
    val streakBonus: StateFlow<Int> = _streakBonus.asStateFlow()

    private val _completedChallenges = MutableStateFlow(0)
    val completedChallenges: StateFlow<Int> = _completedChallenges.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        // Load user points when ViewModel is created
        loadUserPoints()
        loadGamificationData()
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

    // ==================== GAMIFICATION FUNCTIONS ====================

    /**
     * Load tất cả dữ liệu gamification
     */
    private fun loadGamificationData() {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUserProfile()
                val userId = currentUser?.id ?: "default_user"
                val today = dateFormat.format(Date())

                // Load daily check-in status
                loadDailyCheckInStatus(userId, today)
                
                // Load user streak
                loadUserStreak(userId)
                
                // Load daily challenges
                loadDailyChallenges(userId, today)
                
                Logger.d("RewardsViewModel: Gamification data loaded successfully")
            } catch (e: Exception) {
                Logger.e("RewardsViewModel: Error loading gamification data: ${e.message}")
                _errorMessage.value = e.message
            }
        }
    }

    /**
     * Load trạng thái điểm danh hôm nay
     */
    private suspend fun loadDailyCheckInStatus(userId: String, date: String) {
        val checkIn = dailyCheckInDao.getCheckInForDate(userId, date)
        _dailyCheckInStatus.value = checkIn
        _canCheckInToday.value = checkIn?.isCheckedIn != true
    }

    /**
     * Load thông tin streak của user
     */
    private suspend fun loadUserStreak(userId: String) {
        var streak = userStreakDao.getUserStreak(userId)
        if (streak == null) {
            // Tạo streak mới cho user
            streak = UserStreakEntity(userId = userId)
            userStreakDao.insertOrUpdateStreak(streak)
        }
        _userStreak.value = streak
        
        // Tính streak bonus
        val bonus = calculateStreakBonus(streak.currentStreak)
        _streakBonus.value = bonus
    }

    /**
     * Load các thử thách hàng ngày
     */
    private suspend fun loadDailyChallenges(userId: String, date: String) {
        // Xóa các challenge đã hết hạn
        dailyChallengeDao.deleteExpiredChallenges(userId, System.currentTimeMillis())
        
        var challenges = dailyChallengeDao.getActiveChallengesForDate(userId, date)
        
        // Nếu chưa có challenge cho hôm nay, tạo mới
        if (challenges.isEmpty()) {
            challenges = generateDailyChallenges(userId, date)
            challenges.forEach { challenge ->
                dailyChallengeDao.insertChallenge(challenge)
            }
        }
        
        _dailyChallenges.value = challenges
        
        // Đếm số challenge đã hoàn thành
        val completed = dailyChallengeDao.getTotalCompletedChallenges(userId)
        _completedChallenges.value = completed
    }

    /**
     * Thực hiện điểm danh hàng ngày
     */
    fun performDailyCheckIn() {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUserProfile()
                val userId = currentUser?.id ?: "default_user"
                val today = dateFormat.format(Date())
                val checkInId = "${userId}_$today"

                // Kiểm tra xem đã điểm danh chưa
                val existingCheckIn = dailyCheckInDao.getCheckInForDate(userId, today)
                if (existingCheckIn?.isCheckedIn == true) {
                    _errorMessage.value = "Bạn đã điểm danh hôm nay rồi!"
                    return@launch
                }

                // Cập nhật streak
                val streak = updateUserStreak(userId, today)
                
                // Tính điểm thưởng
                val basePoints = 10
                val streakMultiplier = calculateStreakMultiplier(streak.currentStreak)
                val totalPoints = (basePoints * streakMultiplier).toInt()

                // Tạo bản ghi điểm danh
                val checkIn = DailyCheckInEntity(
                    id = checkInId,
                    userId = userId,
                    checkInDate = today,
                    isCheckedIn = true,
                    pointsEarned = totalPoints,
                    streakDay = streak.currentStreak,
                    bonusMultiplier = streakMultiplier
                )

                dailyCheckInDao.insertCheckIn(checkIn)
                _dailyCheckInStatus.value = checkIn
                _canCheckInToday.value = false

                // Cập nhật điểm
                savePointsToDatabase(totalPoints, "DAILY_CHECK_IN", "Daily check-in reward (Day ${streak.currentStreak})")
                
                // Cập nhật challenge liên quan
                updateChallengeProgress(userId, "DAILY_CHECK_IN", 1)

                Logger.d("RewardsViewModel: Daily check-in completed - Points: $totalPoints, Streak: ${streak.currentStreak}")
                
            } catch (e: Exception) {
                Logger.e("RewardsViewModel: Error performing daily check-in: ${e.message}")
                _errorMessage.value = e.message
            }
        }
    }

    /**
     * Cập nhật streak của user
     */
    private suspend fun updateUserStreak(userId: String, today: String): UserStreakEntity {
        var streak = userStreakDao.getUserStreak(userId) ?: UserStreakEntity(userId = userId)
        
        val yesterday = dateFormat.format(Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))
        
        when {
            streak.lastCheckInDate == null -> {
                // Lần đầu điểm danh
                streak = streak.copy(
                    currentStreak = 1,
                    longestStreak = maxOf(1, streak.longestStreak),
                    lastCheckInDate = today,
                    totalCheckIns = streak.totalCheckIns + 1
                )
            }
            streak.lastCheckInDate == yesterday -> {
                // Điểm danh liên tiếp
                val newStreak = streak.currentStreak + 1
                streak = streak.copy(
                    currentStreak = newStreak,
                    longestStreak = maxOf(newStreak, streak.longestStreak),
                    lastCheckInDate = today,
                    totalCheckIns = streak.totalCheckIns + 1
                )
            }
            else -> {
                // Đã bỏ lỡ ngày hôm qua, reset streak
                streak = streak.copy(
                    currentStreak = 1,
                    lastCheckInDate = today,
                    totalCheckIns = streak.totalCheckIns + 1
                )
            }
        }
        
        userStreakDao.insertOrUpdateStreak(streak)
        _userStreak.value = streak
        
        val bonus = calculateStreakBonus(streak.currentStreak)
        _streakBonus.value = bonus
        
        return streak
    }

    /**
     * Tính toán streak multiplier
     */
    private fun calculateStreakMultiplier(streakDays: Int): Float {
        return when {
            streakDays >= 30 -> 3.0f
            streakDays >= 14 -> 2.5f
            streakDays >= 7 -> 2.0f
            streakDays >= 3 -> 1.5f
            else -> 1.0f
        }
    }

    /**
     * Tính toán streak bonus points
     */
    private fun calculateStreakBonus(streakDays: Int): Int {
        return when {
            streakDays >= 30 -> 100
            streakDays >= 14 -> 50
            streakDays >= 7 -> 25
            streakDays >= 3 -> 10
            else -> 0
        }
    }

    /**
     * Tạo các thử thách hàng ngày
     */
    private fun generateDailyChallenges(userId: String, date: String): List<DailyChallengeEntity> {
        val challenges = mutableListOf<DailyChallengeEntity>()
        val expiresAt = System.currentTimeMillis() + 24 * 60 * 60 * 1000 // 24 giờ

        // Challenge 1: Điểm danh hàng ngày
        challenges.add(
            DailyChallengeEntity(
                id = "${userId}_${date}_DAILY_CHECK_IN",
                userId = userId,
                challengeDate = date,
                challengeType = "DAILY_CHECK_IN",
                challengeTitle = "Điểm danh hàng ngày",
                challengeDescription = "Thực hiện điểm danh để nhận điểm thưởng",
                targetValue = 1,
                pointsReward = 15,
                expiresAt = expiresAt
            )
        )

        // Challenge 2: Xem quảng cáo
        challenges.add(
            DailyChallengeEntity(
                id = "${userId}_${date}_WATCH_ADS",
                userId = userId,
                challengeDate = date,
                challengeType = "WATCH_ADS",
                challengeTitle = "Xem quảng cáo",
                challengeDescription = "Xem 3 quảng cáo để nhận thưởng",
                targetValue = 3,
                pointsReward = 25,
                expiresAt = expiresAt
            )
        )

        // Challenge 3: Kiểm tra thời tiết (random)
        if (Random.nextFloat() < 0.7f) {
            challenges.add(
                DailyChallengeEntity(
                    id = "${userId}_${date}_WEATHER_CHECK",
                    userId = userId,
                    challengeDate = date,
                    challengeType = "WEATHER_CHECK",
                    challengeTitle = "Kiểm tra thời tiết",
                    challengeDescription = "Mở ứng dụng và kiểm tra thời tiết 5 lần",
                    targetValue = 5,
                    pointsReward = 20,
                    expiresAt = expiresAt
                )
            )
        }

        return challenges
    }

    /**
     * Cập nhật tiến độ challenge
     */
    fun updateChallengeProgress(userId: String, challengeType: String, progress: Int) {
        viewModelScope.launch {
            try {
                val today = dateFormat.format(Date())
                val challenge = dailyChallengeDao.getChallengeByType(userId, challengeType, today)
                
                if (challenge != null && !challenge.isCompleted) {
                    val newProgress = minOf(challenge.currentProgress + progress, challenge.targetValue)
                    val isCompleted = newProgress >= challenge.targetValue
                    val completedAt = if (isCompleted) System.currentTimeMillis() else null
                    
                    dailyChallengeDao.updateChallengeProgress(
                        challenge.id,
                        newProgress,
                        isCompleted,
                        completedAt
                    )
                    
                    if (isCompleted) {
                        // Thưởng điểm cho challenge hoàn thành
                        savePointsToDatabase(
                            challenge.pointsReward,
                            "CHALLENGE_COMPLETED",
                            "Completed challenge: ${challenge.challengeTitle}"
                        )
                        
                        Logger.d("RewardsViewModel: Challenge completed - ${challenge.challengeTitle}, Points: ${challenge.pointsReward}")
                    }
                    
                    // Reload challenges
                    loadDailyChallenges(userId, today)
                }
            } catch (e: Exception) {
                Logger.e("RewardsViewModel: Error updating challenge progress: ${e.message}")
            }
        }
    }

    /**
     * Lưu điểm vào database với transaction type tùy chỉnh
     */
    private suspend fun savePointsToDatabase(pointsEarned: Int, transactionType: String, description: String) {
        try {
            val currentUser = userRepository.getCurrentUserProfile()
            val userId = currentUser?.id ?: "default_user"
            
            // Add points to database
            userPointsDao.addPointsFromAds(userId, pointsEarned)
            
            // Create transaction record
            val transaction = PointTransactionEntity(
                userId = userId,
                pointsEarned = pointsEarned,
                transactionType = transactionType,
                description = description
            )
            pointTransactionDao.insertTransaction(transaction)
            
            // Update local state from database
            val updatedUserPoints = userPointsDao.getUserPoints(userId)
            if (updatedUserPoints != null) {
                _userPoints.value = updatedUserPoints.totalPoints
                _pointsFromAds.value = updatedUserPoints.pointsFromAds
            }
            
            Logger.d("RewardsViewModel: Points saved - Type: $transactionType, Earned: $pointsEarned, Total: ${_userPoints.value}")
            
        } catch (e: Exception) {
            Logger.e("RewardsViewModel: Error saving points to database: ${e.message}")
            throw e
        }
    }

    /**
     * Lấy thông tin streak hiện tại
     */
    fun getCurrentStreak(): Int {
        return _userStreak.value?.currentStreak ?: 0
    }

    /**
     * Lấy longest streak
     */
    fun getLongestStreak(): Int {
        return _userStreak.value?.longestStreak ?: 0
    }

    /**
     * Kiểm tra xem có thể điểm danh hôm nay không
     */
    fun canCheckInToday(): Boolean {
        return _canCheckInToday.value
    }

    /**
     * Lấy số challenge đã hoàn thành hôm nay
     */
    fun getTodayCompletedChallenges(): Int {
        return _dailyChallenges.value.count { it.isCompleted }
    }

    /**
     * Lấy tổng số challenge hôm nay
     */
    fun getTodayTotalChallenges(): Int {
        return _dailyChallenges.value.size
    }



    /**
     * Cleanup khi ViewModel bị destroy
     */
    override fun onCleared() {
        super.onCleared()
        Logger.d("RewardsViewModel: Cleared")
    }
}