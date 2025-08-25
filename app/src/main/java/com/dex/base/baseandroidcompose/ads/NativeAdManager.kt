package com.dex.base.baseandroidcompose.ads

import android.content.Context
import com.dex.base.baseandroidcompose.utils.Logger
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration.Companion.minutes

/**
 * Manager class để quản lý NativeAd với cache
 * Hỗ trợ load, cache và hiển thị quảng cáo Native một cách hiệu quả
 */
class NativeAdManager private constructor() {

    companion object {
        private const val TAG = "NativeAdManager"
        private const val MAX_CACHE_SIZE = 2 // Số lượng quảng cáo tối đa trong cache
        private const val AD_VALIDITY_DURATION_MINUTES = 15L // Thời gian hiệu lực của quảng cáo (15 phút)
        
        @Volatile
        private var INSTANCE: NativeAdManager? = null
        
        fun getInstance(): NativeAdManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NativeAdManager().also { INSTANCE = it }
            }
        }
    }

    // Cache để lưu trữ Pair<NativeAd, timestamp>
    private val adCache = ConcurrentLinkedQueue<Pair<NativeAd, Long>>()

    /**
     * Preload NativeAd mới nếu cần thiết
     */
    fun preloadNativeAd(context: Context, onLoadComplete: ((Boolean, String?) -> Unit)? = null) {
        Logger.d("preloadNativeAd called")
        
        // Dọn dẹp cache trước
        cleanupExpiredAds()
        
        // Kiểm tra xem có cần load thêm quảng cáo không
        if (adCache.size >= MAX_CACHE_SIZE) {
            Logger.d("Cache đã đầy (${adCache.size}), không load thêm")
            onLoadComplete?.invoke(true, "Cache đã đầy")
            return
        }
        
        // Kiểm tra xem có quảng cáo nào còn hiệu lực không
        if (hasValidAds()) {
            Logger.d("Còn quảng cáo hiệu lực trong cache, không load thêm")
            onLoadComplete?.invoke(true, "Còn quảng cáo hiệu lực")
            return
        }
        
        // Load quảng cáo mới
        loadNewNativeAd(context, onLoadComplete)
    }

    /**
     * Lấy NativeAd từ cache hoặc load mới nếu cần
     */
    fun getNativeAd(context: Context, onAdReady: (NativeAd?) -> Unit) {
        Logger.d("getNativeAd called")
        
        // Dọn dẹp cache trước
        cleanupExpiredAds()
        
        // Tìm quảng cáo còn hiệu lực trong cache
        val validAd = findValidAd()
        if (validAd != null) {
            Logger.d("Trả về quảng cáo từ cache")
            adCache.removeAll { it.first == validAd }
            preloadNativeAd(context)
            onAdReady(validAd)
            return
        }
        
        // Nếu cache rỗng, load mới
        Logger.d("Cache rỗng, load quảng cáo mới")
        loadNewNativeAd(context) { success, message ->
            if (success) {
                val newAd = findValidAd()
                onAdReady(newAd)
                preloadNativeAd(context)
            } else {
                Logger.e("Không thể load quảng cáo mới: $message")
                onAdReady(null)
            }
        }
    }

    /**
     * Kiểm tra xem có quảng cáo nào còn hiệu lực không
     */
    private fun hasValidAds(): Boolean {
        return adCache.any { isAdValid(it.second) }
    }

    /**
     * Tìm quảng cáo còn hiệu lực trong cache
     */
    private fun findValidAd(): NativeAd? {
        return adCache.find { isAdValid(it.second) }?.first
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
        val initialSize = adCache.size
        adCache.removeAll { !isAdValid(it.second) }
        val removedCount = initialSize - adCache.size
        if (removedCount > 0) {
            Logger.d("Đã dọn dẹp $removedCount quảng cáo hết hiệu lực")
        }
    }

    /**
     * Load quảng cáo Native mới
     */
    private fun loadNewNativeAd(context: Context, onLoadComplete: ((Boolean, String?) -> Unit)? = null) {
        Logger.d("Bắt đầu load quảng cáo Native mới")
        
        val adRequest = AdRequest.Builder().build()
        
        val nativeAdOptions = NativeAdOptions.Builder()
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
            .setRequestCustomMuteThisAd(true)
            .setVideoOptions(
                com.google.android.gms.ads.VideoOptions.Builder()
                    .setStartMuted(true)
                    .build()
            )
            .build()

        com.google.android.gms.ads.AdLoader.Builder(context, AdManager.ADMOB_NATIVE_AD_ID)
            .forNativeAd { nativeAd: NativeAd ->
                Logger.d("Quảng cáo Native load thành công")
                
                // Thêm vào cache với timestamp
                val timestamp = System.currentTimeMillis()
                adCache.offer(Pair(nativeAd, timestamp))
                
                Logger.d("Đã thêm quảng cáo vào cache, kích thước cache: ${adCache.size}")
                onLoadComplete?.invoke(true, null)
            }
            .withAdListener(object : com.google.android.gms.ads.AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    val errorMessage = "Load quảng cáo Native thất bại: ${loadAdError.message}"
                    Logger.e(errorMessage)
                    onLoadComplete?.invoke(false, errorMessage)
                }
            })
            .withNativeAdOptions(nativeAdOptions)
            .build()
            .loadAd(adRequest)
    }

    /**
     * Lấy thông tin cache hiện tại (để debug)
     */
    fun getCacheInfo(): String {
        cleanupExpiredAds()
        return "Cache size: ${adCache.size}, Valid ads: ${adCache.count { isAdValid(it.second) }}"
    }

    /**
     * Xóa toàn bộ cache (để debug hoặc reset)
     */
    fun clearCache() {
        val size = adCache.size
        adCache.clear()
        Logger.d("Đã xóa toàn bộ cache ($size quảng cáo)")
    }

    /**
     * Lấy số lượng quảng cáo còn hiệu lực
     */
    fun getValidAdCount(): Int {
        cleanupExpiredAds()
        return adCache.count { isAdValid(it.second) }
    }
} 