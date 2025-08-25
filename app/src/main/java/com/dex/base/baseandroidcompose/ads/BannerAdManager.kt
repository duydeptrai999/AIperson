package com.dex.base.baseandroidcompose.ads

import android.content.Context
import android.view.ViewGroup
import com.dex.base.baseandroidcompose.utils.Logger
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration.Companion.minutes

/**
 * Manager class để quản lý Banner (DEFAULT 320x50) với cache + TTL
 * Hỗ trợ preload, cache và lấy quảng cáo Banner DEFAULT hiệu quả
 */
class BannerAdManager private constructor() {

    companion object {
        private const val TAG = "BannerAdManager"
        private const val MAX_CACHE_SIZE = 2 // Số banner tối đa trong cache
        private const val AD_VALIDITY_DURATION_MINUTES = 15L // TTL: 15 phút

        @Volatile
        private var INSTANCE: BannerAdManager? = null

        fun getInstance(): BannerAdManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BannerAdManager().also { INSTANCE = it }
            }
        }
    }

    // Cache lưu trữ Pair<AdView, timestamp>
    private val adCache = ConcurrentLinkedQueue<Pair<AdView, Long>>()
    private val adUnitId = AdManager.ADMOB_BANNER_AD_ID

    /**
     * Preload Banner DEFAULT (320x50) nếu cần thiết
     */
    fun preloadBannerDefault(context: Context, onLoadComplete: ((Boolean, String?) -> Unit)? = null) {
        Logger.d("$TAG preloadBannerDefault called")

        // Dọn cache hết hạn
        cleanupExpiredAds()

        // Đã đủ số lượng
        if (adCache.size >= MAX_CACHE_SIZE) {
            Logger.d("$TAG Cache đã đầy (${adCache.size}), không load thêm")
            onLoadComplete?.invoke(true, "Cache đã đầy")
            return
        }

        // Đã có banner còn hiệu lực → không load thêm
        if (hasValidAds()) {
            Logger.d("$TAG Còn banner hiệu lực trong cache, không load thêm")
            onLoadComplete?.invoke(true, "Còn quảng cáo hiệu lực")
            return
        }

        // Load mới
        loadNewBannerDefault(context, adUnitId, onLoadComplete)
    }

    /**
     * Lấy Banner DEFAULT từ cache hoặc load mới nếu cần
     */
    fun getBannerDefault(context: Context, onAdReady: (AdView?) -> Unit) {
        Logger.d("$TAG getBannerDefault called")

        // Dọn cache hết hạn
        cleanupExpiredAds()

        // Tìm banner còn hiệu lực
        val validAd = findValidAd()
        if (validAd != null) {
            Logger.d("$TAG Trả về banner từ cache")
            // tách khỏi parent cũ rồi trả ra
            (validAd.parent as? ViewGroup)?.removeView(validAd)
            adCache.removeAll { it.first == validAd }
            // Chủ động warmup tiếp
            preloadBannerDefault(context)
            onAdReady(validAd)
            return
        }

        // Cache rỗng → load mới
        Logger.d("$TAG Cache rỗng, load banner mới")
        loadNewBannerDefault(context, adUnitId) { success, message ->
            if (success) {
                val newAd = findValidAd()
                (newAd?.parent as? ViewGroup)?.removeView(newAd)
                onAdReady(newAd)
                preloadBannerDefault(context)
            } else {
                Logger.e("$TAG Không thể load banner mới: $message")
                onAdReady(null)
            }
        }
    }

    /**
     * Có banner nào còn hiệu lực không?
     */
    private fun hasValidAds(): Boolean {
        return adCache.any { isAdValid(it.second) }
    }

    /**
     * Tìm banner còn hiệu lực
     */
    private fun findValidAd(): AdView? {
        return adCache.find { isAdValid(it.second) }?.first
    }

    /**
     * Kiểm tra TTL
     */
    private fun isAdValid(timestamp: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val validityDuration = AD_VALIDITY_DURATION_MINUTES.minutes.inWholeMilliseconds
        return (currentTime - timestamp) < validityDuration
    }

    /**
     * Dọn các banner hết hiệu lực (và destroy để tránh leak)
     */
    private fun cleanupExpiredAds() {
        val initial = adCache.size
        val iterator = adCache.iterator()
        var removed = 0
        while (iterator.hasNext()) {
            val (ad, ts) = iterator.next()
            if (!isAdValid(ts)) {
                (ad.parent as? ViewGroup)?.removeView(ad)
                ad.destroy()
                iterator.remove()
                removed++
            }
        }
        if (removed > 0) Logger.d("$TAG Đã dọn dẹp $removed banner hết hiệu lực (from $initial)")
    }

    /**
     * Load banner DEFAULT mới
     */
    private fun loadNewBannerDefault(
        context: Context,
        adUnitId: String,
        onLoadComplete: ((Boolean, String?) -> Unit)? = null
    ) {
        Logger.d("$TAG Bắt đầu load banner DEFAULT mới")

        val adView = AdView(context.applicationContext).apply {
            this.adUnitId = adUnitId
            setAdSize(AdSize.BANNER)
        }

        val adRequest = AdRequest.Builder().build()

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Logger.d("$TAG Banner DEFAULT load thành công")
                val timestamp = System.currentTimeMillis()
                adCache.offer(Pair(adView, timestamp))
                Logger.d("$TAG Đã thêm vào cache, size: ${adCache.size}")
                onLoadComplete?.invoke(true, null)
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                val err = "Load banner DEFAULT thất bại: ${loadAdError.code} - ${loadAdError.message}"
                Logger.e("$TAG $err")
                onLoadComplete?.invoke(false, err)
            }
        }

        adView.loadAd(adRequest)
    }

    /**
     * Debug info
     */
    fun getCacheInfo(): String {
        cleanupExpiredAds()
        return "Cache size: ${adCache.size}, Valid ads: ${adCache.count { isAdValid(it.second) }}"
    }

    /**
     * Xóa toàn bộ cache
     */
    fun clearCache() {
        val size = adCache.size
        adCache.forEach { (ad, _) ->
            (ad.parent as? ViewGroup)?.removeView(ad)
            ad.destroy()
        }
        adCache.clear()
        Logger.d("$TAG Đã xóa toàn bộ cache ($size banner)")
    }

    /**
     * Số lượng banner còn hiệu lực
     */
    fun getValidAdCount(): Int {
        cleanupExpiredAds()
        return adCache.count { isAdValid(it.second) }
    }
}
