package com.dex.base.baseandroidcompose.ads

import android.app.Activity
import com.dex.base.baseandroidcompose.utils.Logger

/**
 * Demo class để test NativeAd system
 * Cung cấp các ví dụ sử dụng và test cases
 */
class NativeAdDemo {

    companion object {
        private const val TAG = "NativeAdDemo"
    }

    /**
     * Demo cách sử dụng cơ bản
     */
    fun demoBasicUsage(activity: Activity) {
        Logger.d("=== Demo Basic Usage ===")
        
        // 1. Preload native ad
        AdManager.preloadNativeAd(activity) { success, message ->
            if (success) {
                Logger.d("Preload thành công: $message")
                
                // 2. Lấy native ad
                getNativeAd(activity)
            } else {
                Logger.e("Preload thất bại: $message")
            }
        }
    }

    /**
     * Demo cách lấy native ad
     */
    private fun getNativeAd(activity: Activity) {
        AdManager.getNativeAd(activity) { nativeAd ->
            if (nativeAd != null) {
                Logger.d("Đã lấy được native ad: ${nativeAd.headline}")
                Logger.d("Body: ${nativeAd.body}")
                Logger.d("Call to action: ${nativeAd.callToAction}")
            } else {
                Logger.e("Không thể lấy được native ad")
            }
        }
    }

    /**
     * Demo cache management
     */
    fun demoCacheManagement(activity: Activity) {
        Logger.d("=== Demo Cache Management ===")
        
        // 1. Kiểm tra cache hiện tại
        val cacheInfo = AdManager.getNativeAdCacheInfo()
        Logger.d("Cache info: $cacheInfo")
        
        // 2. Preload nhiều native ad
        repeat(3) { index ->
            AdManager.preloadNativeAd(activity) { success, message ->
                Logger.d("Preload $index: ${if (success) "thành công" else "thất bại"} - $message")
                
                // Kiểm tra cache sau mỗi lần preload
                val currentCacheInfo = AdManager.getNativeAdCacheInfo()
                Logger.d("Cache info sau lần preload $index: $currentCacheInfo")
            }
        }
    }

    /**
     * Demo performance và timing
     */
    fun demoPerformanceAndTiming(activity: Activity) {
        Logger.d("=== Demo Performance & Timing ===")
        
        val startTime = System.currentTimeMillis()
        
        // Preload native ad
        AdManager.preloadNativeAd(activity) { success, message ->
            val loadTime = System.currentTimeMillis() - startTime
            Logger.d("Native ad preload took ${loadTime}ms: ${if (success) "✅ Success" else "❌ Failed"}")
            
            if (success) {
                // Test get native ad performance
                val getStartTime = System.currentTimeMillis()
                
                AdManager.getNativeAd(activity) { nativeAd ->
                    val getTime = System.currentTimeMillis() - getStartTime
                    val totalTime = System.currentTimeMillis() - startTime
                    
                    Logger.d("Get native ad took ${getTime}ms, total time: ${totalTime}ms")
                    Logger.d("Result: ${if (nativeAd != null) "✅ Success" else "❌ Failed"}")
                }
            }
        }
    }

    /**
     * Demo error handling
     */
    fun demoErrorHandling(activity: Activity) {
        Logger.d("=== Demo Error Handling ===")
        
        try {
            // Test với context null (sẽ gây lỗi)
            Logger.d("Testing with null context...")
            // Note: Không thể test với null context vì AdManager yêu cầu non-null context
            Logger.d("Skipping null context test due to type safety")
        } catch (e: Exception) {
            Logger.e("Exception with null context: ${e.message}")
        }
        
        // Test cache info
        val cacheInfo = AdManager.getNativeAdCacheInfo()
        Logger.d("Current cache info: $cacheInfo")
        
        // Test clear cache
        AdManager.clearNativeAdCache()
        Logger.d("Cache cleared")
        
        val newCacheInfo = AdManager.getNativeAdCacheInfo()
        Logger.d("Cache info after clear: $newCacheInfo")
    }

    /**
     * Demo integration với UI
     */
    fun demoUIIntegration(activity: Activity) {
        Logger.d("=== Demo UI Integration ===")
        
        Logger.d("1. Preload native ad for UI")
        AdManager.preloadNativeAd(activity) { success, message ->
            if (success) {
                Logger.d("✅ Native ad ready for UI")
                Logger.d("2. UI có thể sử dụng NativeAdView Composable")
                Logger.d("3. Chỉ cần gọi: NativeAdView(modifier = Modifier.fillMaxWidth())")
            } else {
                Logger.e("❌ Native ad not ready for UI")
            }
        }
    }

    /**
     * Demo multiple ads scenario
     */
    fun demoMultipleAds(activity: Activity) {
        Logger.d("=== Demo Multiple Ads ===")
        
        // Preload multiple ads
        repeat(2) { index ->
            AdManager.preloadNativeAd(activity) { success, message ->
                Logger.d("Preload ad $index: ${if (success) "✅ Success" else "❌ Failed"}")
                
                if (index == 1) {
                    // Kiểm tra tổng số ads
                    val adCount = AdManager.getNativeAdCount()
                    Logger.d("Total valid ads: $adCount")
                    
                    // Test get multiple ads
                    repeat(3) { getIndex ->
                        AdManager.getNativeAd(activity) { nativeAd ->
                            Logger.d("Get ad $getIndex: ${if (nativeAd != null) "✅ Success" else "❌ Failed"}")
                        }
                    }
                }
            }
        }
    }
} 