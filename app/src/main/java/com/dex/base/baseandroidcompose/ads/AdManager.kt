package com.dex.base.baseandroidcompose.ads

import android.app.Activity
import android.content.Context
import com.dex.base.baseandroidcompose.utils.Helper
import com.dex.base.baseandroidcompose.utils.Logger
import com.dex.base.baseandroidcompose.utils.MyPref
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform

object AdManager {

    // real ad
    var ADMOB_APP_ID = "ca-app-pub-4183647288183037~2782349072"
    var ADMOB_BANNER_AD_ID = "ca-app-pub-9821898502051437/1958618303"
    var ADMOB_INTERSTITIAL_AD_ID = "ca-app-pub-9821898502051437/9645536639"
    var ADMOB_REWARDED_AD_ID = "ca-app-pub-9821898502051437/8635691382"
    var ADMOB_APP_OPEN_AD_ID = "ca-app-pub-9821898502051437/8332454968"
    var ADMOB_NATIVE_VIDEO_AD_ID = "ca-app-pub-9821898502051437/7710456674"
    var ADMOB_NATIVE_AD_ID = "ca-app-pub-9821898502051437/7710456674"

    // test
//    var ADMOB_APP_ID = "ca-app-pub-4183647288183037~2782349072"
//    var ADMOB_BANNER_AD_ID = "ca-app-pub-3940256099942544/9214589741"
//    var ADMOB_INTERSTITIAL_AD_ID = "ca-app-pub-3940256099942544/1033173712"
//    var ADMOB_REWARDED_AD_ID = "ca-app-pub-3940256099942544/5224354917"
//    var ADMOB_APP_OPEN_AD_ID = "ca-app-pub-3940256099942544/9257395921"
//    var ADMOB_NATIVE_VIDEO_AD_ID = "ca-app-pub-3940256099942544/2247696110"
//    var ADMOB_NATIVE_AD_ID = "ca-app-pub-3940256099942544/2247696110"

    private var shouldShowOpenAd = true
    private var isEnableOpenAd = true

    var RETRY_TIME_RELOAD_AD = 1
    var DELAY_TIME_RELOAD_AD = 2000

    // Managers
    private val interstitialAdManager = InterstitialAdManager.getInstance()
    private val nativeAdManager = NativeAdManager.getInstance()

    fun fetchAdId(remoteConfig: FirebaseRemoteConfig) {
        ADMOB_APP_ID = remoteConfig.getString("ADMOB_APP_ID")
        Logger.d("ADMOB_APP_ID = $ADMOB_APP_ID")
        ADMOB_BANNER_AD_ID = remoteConfig.getString("ADMOB_BANNER_AD_ID")
        Logger.d("ADMOB_BANNER_AD_ID = $ADMOB_BANNER_AD_ID")
        ADMOB_INTERSTITIAL_AD_ID = remoteConfig.getString("ADMOB_INTERSTITIAL_AD_ID")
        Logger.d("ADMOB_INTERSTITIAL_AD_ID = $ADMOB_INTERSTITIAL_AD_ID")
        ADMOB_REWARDED_AD_ID = remoteConfig.getString("ADMOB_REWARDED_AD_ID")
        Logger.d("ADMOB_REWARDED_AD_ID = $ADMOB_REWARDED_AD_ID")
        ADMOB_APP_OPEN_AD_ID = remoteConfig.getString("ADMOB_APP_OPEN_AD_ID")
        Logger.d("ADMOB_APP_OPEN_AD_ID = $ADMOB_APP_OPEN_AD_ID")
        ADMOB_NATIVE_VIDEO_AD_ID = remoteConfig.getString("ADMOB_NATIVE_VIDEO_AD_ID")
        Logger.d("ADMOB_NATIVE_VIDEO_AD_ID = $ADMOB_NATIVE_VIDEO_AD_ID")
        ADMOB_NATIVE_AD_ID = remoteConfig.getString("ADMOB_NATIVE_AD_ID")
        Logger.d("ADMOB_NATIVE_AD_ID = $ADMOB_NATIVE_AD_ID")
        DELAY_TIME_RELOAD_AD = remoteConfig.getLong("DELAY_TIME_RELOAD_AD").toInt()
        RETRY_TIME_RELOAD_AD = remoteConfig.getLong("RETRY_TIME_RELOAD_AD").toInt()
        isEnableOpenAd = remoteConfig.getBoolean("ENABLE_OPEN_AD")
    }

    fun initAdsAndUmp(activity: Activity) {
        Logger.d("initAdsAndUmp")
        // Initialize the Google Mobile Ads SDK
        MobileAds.initialize(
            activity,
            OnInitializationCompleteListener { initializationStatus: InitializationStatus? ->
                Logger.d("Google Mobile Ads SDK initialized.")
                if (Helper.isDebugMode()) {
                    val testDeviceIds = listOf(
                        "5A7C435CDB44595BA4614DED50558B78",
                        "3EBFDB3B44A5FAFCB59C24481C7FC32E",
                        AdRequest.DEVICE_ID_EMULATOR
                    )
                    val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
                    MobileAds.setRequestConfiguration(configuration)
                    Logger.d("Đã thiết lập cấu hình cho DEBUG mode với test devices")
                } else {
                    Logger.d("Đã thiết lập cấu hình cho RELEASE mode với quảng cáo thật")
                }
                
                // Khởi tạo InterstitialAdManager sau khi SDK đã sẵn sàng
                initInterstitialAds(activity)
                
                // Khởi tạo NativeAdManager sau khi SDK đã sẵn sàng
                initNativeAds(activity)
            })

        // Create a ConsentRequestParameters object
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        // Request consent information
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        Logger.d("consentInformation: $consentInformation")
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                // The consent information state was updated.
                // You are now ready to check if a form is available.
                Logger.d("OnConsentInfoUpdateSuccessListener")

                val consentFormAvailable = consentInformation.isConsentFormAvailable
                Logger.d(" consentFormAvailable: $consentFormAvailable")
                if (consentInformation.isConsentFormAvailable) {
                    loadConsentForm(activity)
                }
            },
            { formError: FormError ->
                // Handle the error.
                Logger.e("OnConsentInfoUpdateFailureListener: " + formError.message)
            })
    }

    /**
     * Khởi tạo InterstitialAds
     */
    private fun initInterstitialAds(context: Context) {
        Logger.d("Khởi tạo InterstitialAds")
        // Load một số quảng cáo ban đầu để cache
        interstitialAdManager.preloadInterAd(context) { success, message ->
            if (success) {
                Logger.d("Khởi tạo InterstitialAds thành công: $message")
            } else {
                Logger.e("Khởi tạo InterstitialAds thất bại: $message")
            }
        }
    }

    /**
     * Khởi tạo NativeAds
     */
    private fun initNativeAds(context: Context) {
        Logger.d("Khởi tạo NativeAds")
        // Load một số quảng cáo ban đầu để cache
        nativeAdManager.preloadNativeAd(context) { success, message ->
            if (success) {
                Logger.d("Khởi tạo NativeAds thành công: $message")
            } else {
                Logger.e("Khởi tạo NativeAds thất bại: $message")
            }
        }
    }

    // ==================== INTERSTITIAL AD METHODS ====================

    /**
     * Load InterstitialAd
     */
    fun loadInterAd(context: Context, onLoadComplete: ((Boolean, String?) -> Unit)? = null) {
        interstitialAdManager.preloadInterAd(context, onLoadComplete)
    }

    /**
     * Get InterstitialAd
     */
    fun getInterAd(context: Context, onAdReady: (InterstitialAd?) -> Unit) {
        interstitialAdManager.getInterAd(context, onAdReady)
    }

    /**
     * Show InterstitialAd
     */
    fun showInterAd(activity: Activity, onFinish: (Boolean) -> Unit) {
        interstitialAdManager.showInterAd(activity, onFinish)
    }

    /**
     * Lấy thông tin cache InterstitialAd
     */
    fun getInterstitialCacheInfo(): String {
        return interstitialAdManager.getCacheInfo()
    }

    /**
     * Xóa cache InterstitialAd
     */
    fun clearInterstitialCache() {
        interstitialAdManager.clearCache()
    }

    // ==================== NATIVE AD METHODS ====================

    /**
     * Preload NativeAd
     */
    fun preloadNativeAd(context: Context, onLoadComplete: ((Boolean, String?) -> Unit)? = null) {
        nativeAdManager.preloadNativeAd(context, onLoadComplete)
    }

    /**
     * Get NativeAd
     */
    fun getNativeAd(context: Context, onAdReady: (NativeAd?) -> Unit) {
        nativeAdManager.getNativeAd(context, onAdReady)
    }

    /**
     * Lấy thông tin cache NativeAd
     */
    fun getNativeAdCacheInfo(): String {
        return nativeAdManager.getCacheInfo()
    }

    /**
     * Xóa cache NativeAd
     */
    fun clearNativeAdCache() {
        nativeAdManager.clearCache()
    }

    /**
     * Lấy số lượng NativeAd còn hiệu lực
     */
    fun getNativeAdCount(): Int {
        return nativeAdManager.getValidAdCount()
    }

    fun isEnableOpenAd() = isEnableOpenAd
    fun isShouldShowOpenAd() = shouldShowOpenAd
    fun setShouldShowOpenAdStatus(status: Boolean) {
        shouldShowOpenAd = status
    }

    private fun loadConsentForm(activity: Activity) {
        Logger.d("loadConsentForm")
        UserMessagingPlatform.loadConsentForm(
            activity,
            { consentForm: ConsentForm ->
                // Show the consent form.
                Logger.d("Show the consent form")
                consentForm.show(
                    activity,
                    ConsentForm.OnConsentFormDismissedListener { formError: FormError? ->
                        // Handle dismissal by reloading form if necessary.
                        Logger.d("Consent form dismissed.")
                    })
            },
            { formError: FormError ->
                // Handle the error.
                Logger.d("Failed to load consent form: " + formError.message)
            })
    }


}