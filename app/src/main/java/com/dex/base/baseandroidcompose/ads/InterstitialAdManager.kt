package com.dex.base.baseandroidcompose.ads

import android.app.Activity
import android.content.Context
import com.dex.base.baseandroidcompose.utils.Logger
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration.Companion.minutes

/**
 * Manager class để quản lý InterstitialAd với stack cache
 * Hỗ trợ load, cache và show quảng cáo một cách hiệu quả
 */
class InterstitialAdManager private constructor() {

    companion object {
        private const val TAG = "InterstitialAdManager"
        private const val MAX_CACHE_SIZE = 3 // Số lượng quảng cáo tối đa trong cache
        private const val AD_VALIDITY_DURATION_MINUTES = 10L // Thời gian hiệu lực của quảng cáo (10 phút)
        
        @Volatile
        private var INSTANCE: InterstitialAdManager? = null
        
        fun getInstance(): InterstitialAdManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: InterstitialAdManager().also { INSTANCE = it }
            }
        }
    }

    // Stack để lưu trữ Pair<InterstitialAd, timestamp>
    private val adStack = ConcurrentLinkedQueue<Pair<InterstitialAd, Long>>()
    
    // Callback interface cho việc show quảng cáo
    interface InterstitialAdCallback {
        fun onAdShown()
        fun onAdDismissed()
        fun onAdFailedToShow(error: String)
        fun onAdClicked()
    }

    /**
     * Load InterstitialAd mới nếu cần thiết
     * Kiểm tra cache trước khi load
     */
    fun preloadInterAd(context: Context, onLoadComplete: ((Boolean, String?) -> Unit)? = null) {
        Logger.d("preloadInterAd called")
        
        // Dọn dẹp cache trước
        cleanupExpiredAds()
        
        // Kiểm tra xem có cần load thêm quảng cáo không
        if (adStack.size >= MAX_CACHE_SIZE) {
            Logger.d("Cache đã đầy (${adStack.size}), không load thêm")
            onLoadComplete?.invoke(true, "Cache đã đầy")
            return
        }
        
        // Kiểm tra xem có quảng cáo nào còn hiệu lực không
//        if (hasValidAds()) {
//            Logger.d("Còn quảng cáo hiệu lực trong cache, không load thêm")
//            onLoadComplete?.invoke(true, "Còn quảng cáo hiệu lực")
//            return
//        }
        
        // Load quảng cáo mới
        loadNewInterstitialAd(context, onLoadComplete)
    }

    /**
     * Lấy InterstitialAd từ cache hoặc load mới nếu cần
     */
    fun getInterAd(context: Context, onAdReady: (InterstitialAd?) -> Unit) {
        Logger.d("getInterAd called")
        
        // Dọn dẹp cache trước
        cleanupExpiredAds()
        
        // Tìm quảng cáo còn hiệu lực trong cache
        val validAd = findValidAd()
        if (validAd != null) {
            Logger.d("Trả về quảng cáo từ cache")
            adStack.removeAll { it.first == validAd }
            onAdReady(validAd)
            preloadInterAd(context)
            return
        }
        
        // Nếu cache rỗng, load mới
        Logger.d("Cache rỗng, load quảng cáo mới")
        loadNewInterstitialAd(context) { success, message ->
            if (success) {
                val newAd = findValidAd()
                onAdReady(newAd)
                preloadInterAd(context)
            } else {
                Logger.e("Không thể load quảng cáo mới: $message")
                onAdReady(null)
            }
        }
    }

    /**
     * Show InterstitialAd với callback
     */
    fun showInterAd(activity: Activity, onFinish: (Boolean) -> Unit) {
        Logger.d("showInterAd called")
        
        // Dọn dẹp cache trước
        cleanupExpiredAds()

        getInterAd(activity) { interstitialAd ->
            if (interstitialAd == null) {
                Logger.e("Không thể show quảng cáo, không có quảng cáo hiệu lực")
                onFinish(false)
                return@getInterAd
            }
            showInterstitialAd(activity, interstitialAd, onFinish)
        }


        // Tìm quảng cáo còn hiệu lực
//        val validAd = findValidAd()
//        if (validAd == null) {
//
//            Logger.e("Không có quảng cáo hiệu lực để show")
//            onFinish(false)
//            return
//        }
//
//        // Remove quảng cáo khỏi cache trước khi show
//        adStack.removeAll { it.first == validAd }
//
//        // Show quảng cáo
//        showInterstitialAd(activity, validAd, onFinish)
    }

    /**
     * Kiểm tra xem có quảng cáo nào còn hiệu lực không
     */
    private fun hasValidAds(): Boolean {
        return adStack.any { isAdValid(it.second) }
    }

    /**
     * Tìm quảng cáo còn hiệu lực trong cache
     */
    private fun findValidAd(): InterstitialAd? {
        return adStack.find { isAdValid(it.second) }?.first
    }

    /**
     * Kiểm tra xem quảng cáo có còn hiệu lực không
     */
    private fun isAdValid(timestamp: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val validityDuration = AD_VALIDITY_DURATION_MINUTES.minutes.inWholeMilliseconds
        return (currentTime - timestamp) < validityDuration
    }

    /**
     * Dọn dẹp các quảng cáo đã hết hiệu lực
     */
    private fun cleanupExpiredAds() {
        val initialSize = adStack.size
        adStack.removeAll { !isAdValid(it.second) }
        val removedCount = initialSize - adStack.size
        if (removedCount > 0) {
            Logger.d("Đã dọn dẹp $removedCount quảng cáo hết hiệu lực")
        }
    }

    /**
     * Load quảng cáo Interstitial mới
     */
    private fun loadNewInterstitialAd(context: Context, onLoadComplete: ((Boolean, String?) -> Unit)? = null) {
        Logger.d("Bắt đầu load quảng cáo Interstitial mới")
        
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            context,
            AdManager.ADMOB_INTERSTITIAL_AD_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Logger.d("Quảng cáo Interstitial load thành công")
                    
                    // Thêm vào cache với timestamp
                    val timestamp = System.currentTimeMillis()
                    adStack.offer(Pair(interstitialAd, timestamp))
                    
                    Logger.d("Đã thêm quảng cáo vào cache, kích thước cache: ${adStack.size}")
                    onLoadComplete?.invoke(true, null)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    val errorMessage = "Load quảng cáo thất bại: ${loadAdError.message}"
                    Logger.e(errorMessage)
                    onLoadComplete?.invoke(false, errorMessage)
                }
            }
        )
    }

    /**
     * Show quảng cáo Interstitial
     */
    private fun showInterstitialAd(activity: Activity, interstitialAd: InterstitialAd, onFinish: (Boolean) -> Unit) {
        Logger.d("Bắt đầu show quảng cáo Interstitial")
        
        // Thiết lập callback
        interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Logger.d("Quảng cáo bị đóng bởi user")
                onFinish(true)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                val errorMessage = "Show quảng cáo thất bại: ${adError.message}"
                Logger.e(errorMessage)
                onFinish(false)
            }

            override fun onAdShowedFullScreenContent() {
                Logger.d("Quảng cáo đang hiển thị")
            }

            override fun onAdClicked() {
                Logger.d("User click vào quảng cáo")
            }
        }

        // Show quảng cáo
        interstitialAd.show(activity)
    }

    /**
     * Lấy thông tin cache hiện tại (để debug)
     */
    fun getCacheInfo(): String {
        cleanupExpiredAds()
        return "Cache size: ${adStack.size}, Valid ads: ${adStack.count { isAdValid(it.second) }}"
    }

    /**
     * Xóa toàn bộ cache (để debug hoặc reset)
     */
    fun clearCache() {
        val size = adStack.size
        adStack.clear()
        Logger.d("Đã xóa toàn bộ cache ($size quảng cáo)")
    }
} 