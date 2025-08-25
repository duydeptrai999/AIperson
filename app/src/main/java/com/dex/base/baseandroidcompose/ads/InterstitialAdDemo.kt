package com.dex.base.baseandroidcompose.ads

import android.app.Activity
import android.content.Context
import com.dex.base.baseandroidcompose.utils.Logger

/**
 * Demo class để test InterstitialAdManager
 * Cung cấp các ví dụ sử dụng và test các tính năng
 */
class InterstitialAdDemo {

//    companion object {
//        private const val TAG = "InterstitialAdDemo"
//    }
//
//    /**
//     * Demo cách sử dụng cơ bản
//     */
//    fun demoBasicUsage(activity: Activity) {
//        Logger.d(TAG, "=== Demo Basic Usage ===")
//
//        // 1. Load quảng cáo
//        AdManager.loadInterAd(activity) { success, message ->
//            if (success) {
//                Logger.d(TAG, "Load quảng cáo thành công: $message")
//
//                // 2. Show quảng cáo sau khi load thành công
//                showInterstitialAd(activity)
//            } else {
//                Logger.e(TAG, "Load quảng cáo thất bại: $message")
//            }
//        }
//    }
//
//    /**
//     * Demo cách sử dụng nâng cao với cache management
//     */
//    fun demoAdvancedUsage(activity: Activity) {
//        Logger.d(TAG, "=== Demo Advanced Usage ===")
//
//        // 1. Kiểm tra cache hiện tại
//        val cacheInfo = AdManager.getInterstitialCacheInfo()
//        Logger.d(TAG, "Cache info: $cacheInfo")
//
//        // 2. Load nhiều quảng cáo để test cache
//        repeat(3) { index ->
//            AdManager.loadInterAd(activity) { success, message ->
//                Logger.d(TAG, "Load quảng cáo $index: ${if (success) "thành công" else "thất bại"} - $message")
//
//                // Kiểm tra cache sau mỗi lần load
//                val currentCacheInfo = AdManager.getInterstitialCacheInfo()
//                Logger.d(TAG, "Cache info sau lần load $index: $currentCacheInfo")
//            }
//        }
//    }
//    /**
//     * Demo cách show quảng cáo với callback
//     */
//    private fun showInterstitialAd(activity: Activity) {
//        Logger.d(TAG, "Bắt đầu show quảng cáo")
//
//        AdManager.showInterAd(activity) { success ->
//            if (success) {
//                Logger.d(TAG, "Quảng cáo đã được đóng bởi user")
//                // Có thể thực hiện logic tiếp theo ở đây
//                // Ví dụ: chuyển màn hình, tiếp tục game, etc.
//            } else {
//                Logger.e(TAG, "Show quảng cáo thất bại")
//                // Xử lý khi show quảng cáo thất bại
//            }
//        }
//    }
//
//    /**
//     * Demo cách sử dụng getInterAd
//     */
//    fun demoGetInterAd(activity: Activity) {
//        Logger.d(TAG, "=== Demo Get Inter Ad ===")
//
//        AdManager.getInterAd(activity) { interstitialAd ->
//            if (interstitialAd != null) {
//                Logger.d(TAG, "Đã lấy được quảng cáo từ cache hoặc load mới")
//                // Có thể show ngay hoặc lưu để sử dụng sau
//                showInterstitialAd(activity)
//            } else {
//                Logger.e(TAG, "Không thể lấy được quảng cáo")
//            }
//        }
//    }
//
//    /**
//     * Demo cache management
//     */
//    fun demoCacheManagement(activity: Activity) {
//        Logger.d(TAG, "=== Demo Cache Management ===")
//
//        // 1. Kiểm tra cache hiện tại
//        val initialCacheInfo = AdManager.getInterstitialCacheInfo()
//        Logger.d(TAG, "Cache ban đầu: $initialCacheInfo")
//
//        // 2. Load thêm quảng cáo
//        AdManager.loadInterAd(activity) { success, message ->
//            if (success) {
//                val currentCacheInfo = AdManager.getInterstitialCacheInfo()
//                Logger.d(TAG, "Cache sau khi load: $currentCacheInfo")
//            }
//        }
//
//        // 3. Xóa cache (chỉ dùng để test)
//        // AdManager.clearInterstitialCache()
//        // Logger.d(TAG, "Đã xóa cache")
//    }
//
//    /**
//     * Demo sử dụng trong game hoặc app flow
//     */
//    fun demoGameFlow(activity: Activity) {
//        Logger.d(TAG, "=== Demo Game Flow ===")
//
//        // Giả lập game flow: level complete -> show quảng cáo -> next level
//        Logger.d(TAG, "Level 1 completed!")
//
//        // Load quảng cáo trước
//        AdManager.loadInterAd(activity) { success, message ->
//            if (success) {
//                Logger.d(TAG, "Quảng cáo đã sẵn sàng cho level tiếp theo")
//
//                // Giả lập chuyển level
//                Logger.d(TAG, "Chuyển sang Level 2...")
//
//                // Show quảng cáo
//                AdManager.showInterAd(activity) { adClosed ->
//                    if (adClosed) {
//                        Logger.d(TAG, "Bắt đầu Level 2!")
//                        // Logic bắt đầu level mới
//                    } else {
//                        Logger.d(TAG, "Quảng cáo bị lỗi, bắt đầu Level 2 trực tiếp")
//                        // Logic bắt đầu level mới
//                    }
//                }
//            } else {
//                Logger.d(TAG, "Không thể load quảng cáo, bắt đầu Level 2 trực tiếp")
//                // Logic bắt đầu level mới
//            }
//        }
//    }
//
//    /**
//     * Demo error handling
//     */
//    fun demoErrorHandling(activity: Activity) {
//        Logger.d(TAG, "=== Demo Error Handling ===")
//
//        // Test với context null (sẽ gây lỗi)
//        try {
//            AdManager.loadInterAd(null as Context?) { success, message ->
//                Logger.d(TAG, "Kết quả load với context null: success=$success, message=$message")
//            }
//        } catch (e: Exception) {
//            Logger.e(TAG, "Exception khi load với context null: ${e.message}")
//        }
//
//        // Test show quảng cáo khi cache rỗng
//        AdManager.clearInterstitialCache()
//        AdManager.showInterAd(activity) { success ->
//            Logger.d(TAG, "Kết quả show khi cache rỗng: success=$success")
//        }
//    }
} 