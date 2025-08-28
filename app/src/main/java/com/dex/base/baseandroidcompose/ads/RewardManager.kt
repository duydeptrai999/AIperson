package com.dex.base.baseandroidcompose.ads

import android.app.Activity
import android.content.Context
import com.dex.base.baseandroidcompose.utils.Logger
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * RewardManager - Quản lý quảng cáo reward với preload functionality
 * Singleton pattern để đảm bảo chỉ có một instance duy nhất
 */
class RewardManager private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: RewardManager? = null

        fun getInstance(): RewardManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RewardManager().also { INSTANCE = it }
            }
        }
    }

    // State management
    private var rewardedAd: RewardedAd? = null
    private val isLoading = AtomicBoolean(false)
    private val isShowing = AtomicBoolean(false)
    private var retryCount = 0
    private val maxRetryCount = 3
    
    // Callbacks
    private var onAdLoadedCallback: (() -> Unit)? = null
    private var onAdFailedToLoadCallback: ((String) -> Unit)? = null
    private var onUserEarnedRewardCallback: ((RewardItem) -> Unit)? = null
    private var onAdClosedCallback: (() -> Unit)? = null
    private var onAdNotReadyCallback: (() -> Unit)? = null

    /**
     * Khởi tạo RewardManager
     */
    fun initialize(context: Context) {
        Logger.d("RewardManager: Initializing...")
        preloadRewardedAd(context)
    }

    /**
     * Preload quảng cáo reward để sẵn sàng hiển thị
     */
    fun preloadRewardedAd(context: Context) {
        if (isLoading.get()) {
            Logger.d("RewardManager: Ad already loading")
            return
        }

        if (rewardedAd != null) {
            Logger.d("RewardManager: Ad already loaded")
            return
        }

        isLoading.set(true)
        Logger.d("RewardManager: Starting to load rewarded ad...")

        val adRequest = AdRequest.Builder().build()
        
        RewardedAd.load(
            context,
            AdManager.ADMOB_REWARDED_AD_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    isLoading.set(false)
                    rewardedAd = null
                    retryCount++
                    
                    Logger.e("RewardManager: Failed to load ad - ${adError.message}")
                    onAdFailedToLoadCallback?.invoke(adError.message)
                    
                    // Retry loading with exponential backoff
                    if (retryCount <= maxRetryCount) {
                        val delayTime = (AdManager.DELAY_TIME_RELOAD_AD * retryCount).toLong()
                        Logger.d("RewardManager: Retrying in ${delayTime}ms (attempt $retryCount/$maxRetryCount)")
                        
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(delayTime)
                            preloadRewardedAd(context)
                        }
                    }
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    isLoading.set(false)
                    rewardedAd = ad
                    retryCount = 0
                    
                    Logger.d("RewardManager: Ad loaded successfully")
                    onAdLoadedCallback?.invoke()
                    
                    setupAdCallbacks()
                }
            }
        )
    }

    /**
     * Setup callbacks cho quảng cáo
     */
    private fun setupAdCallbacks() {
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                Logger.d("RewardManager: Ad clicked")
            }

            override fun onAdDismissedFullScreenContent() {
                Logger.d("RewardManager: Ad dismissed")
                isShowing.set(false)
                rewardedAd = null
                onAdClosedCallback?.invoke()
                
                // Preload next ad
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000) // Wait 1 second before preloading next ad
                    preloadRewardedAd(rewardedAd?.let { 
                        // Get context from current ad if available
                        null
                    } ?: return@launch)
                }
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Logger.e("RewardManager: Failed to show ad - ${adError.message}")
                isShowing.set(false)
                rewardedAd = null
                onAdNotReadyCallback?.invoke()
            }

            override fun onAdImpression() {
                Logger.d("RewardManager: Ad impression recorded")
            }

            override fun onAdShowedFullScreenContent() {
                Logger.d("RewardManager: Ad showed full screen")
                isShowing.set(true)
            }
        }
    }

    /**
     * Hiển thị quảng cáo reward
     */
    fun showRewardedAd(
        activity: Activity,
        onUserEarnedReward: (RewardItem) -> Unit,
        onAdClosed: () -> Unit = {},
        onAdNotReady: () -> Unit = {}
    ) {
        if (isShowing.get()) {
            Logger.w("RewardManager: Ad is already showing")
            return
        }

        rewardedAd?.let { ad ->
            Logger.d("RewardManager: Showing rewarded ad")
            
            ad.show(activity) { rewardItem ->
                Logger.d("RewardManager: User earned reward - ${rewardItem.amount} ${rewardItem.type}")
                onUserEarnedReward(rewardItem)
                onUserEarnedRewardCallback?.invoke(rewardItem)
            }
            
            // Update callbacks
            onAdClosedCallback = onAdClosed
            onAdNotReadyCallback = onAdNotReady
            
        } ?: run {
            Logger.w("RewardManager: No ad available to show")
            onAdNotReady()
            onAdNotReadyCallback?.invoke()
        }
    }

    /**
     * Kiểm tra xem quảng cáo có sẵn sàng không
     */
    fun isAdReady(): Boolean {
        return rewardedAd != null && !isLoading.get() && !isShowing.get()
    }

    /**
     * Kiểm tra xem có đang load quảng cáo không
     */
    fun isAdLoading(): Boolean {
        return isLoading.get()
    }

    /**
     * Lấy trạng thái hiện tại của quảng cáo
     */
    fun getAdStatus(): String {
        return when {
            isShowing.get() -> "Showing"
            isLoading.get() -> "Loading"
            rewardedAd != null -> "Ready"
            else -> "Not Ready"
        }
    }

    /**
     * Set callback khi quảng cáo load thành công
     */
    fun setOnAdLoadedCallback(callback: () -> Unit) {
        onAdLoadedCallback = callback
    }

    /**
     * Set callback khi quảng cáo load thất bại
     */
    fun setOnAdFailedToLoadCallback(callback: (String) -> Unit) {
        onAdFailedToLoadCallback = callback
    }

    /**
     * Retry loading quảng cáo
     */
    fun retryLoadingAd(context: Context) {
        Logger.d("RewardManager: Retrying to load ad...")
        retryCount = 0
        rewardedAd = null
        preloadRewardedAd(context)
    }

    /**
     * Force reload quảng cáo (reset tất cả state)
     */
    fun forceReload(context: Context) {
        Logger.d("RewardManager: Force reloading ad...")
        destroy()
        retryCount = 0
        preloadRewardedAd(context)
    }

    /**
     * Destroy RewardManager và clean up resources
     */
    fun destroy() {
        Logger.d("RewardManager: Destroying...")
        rewardedAd = null
        isLoading.set(false)
        isShowing.set(false)
        retryCount = 0
        
        // Clear callbacks
        onAdLoadedCallback = null
        onAdFailedToLoadCallback = null
        onUserEarnedRewardCallback = null
        onAdClosedCallback = null
        onAdNotReadyCallback = null
    }
}