package com.dex.base.baseandroidcompose

import com.dex.base.baseandroidcompose.ads.AdManager
import com.dex.base.baseandroidcompose.utils.Logger
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

object RemoteConfigManager {

    // Thay thế bằng các value remote config của bạn
    var isEnableOldEnhancerApi = false
    var isEnableOldFaceSwapApi = false
    var isEnableOldRemoveBgApi = false
    var isEnableExtraRewardAd = false
    var isEnableHideSelectLanguageButton = false
    var stylePresetRemoteData = ""
    var coverListRemoteData = ""
    var delayShowLanguageSelectButton = 1000L
    var organizationMail = "hananyogev77@gmail.com"
    var imgBbKey = "a26932056ad05d9e72a28018c18444f5"
    var modelsLabKey = "pSzKzlKLmPrKQygqYlXUDR3lAB0Mb3G4YDgr1rmO91H2DhpkMRhrWG5vwsq3"
    var cloudinaryName = "demo"
    var cloudinaryUploadPreset = "ml_default"
    var imgurClientId = "YOUR_IMGUR_CLIENT_ID"
    var MAX_POLLING_ATTEMPTS = 12
    var POLLING_INTERVAL_MS = 10000L
    var TIMEOUT_TO_PROCESS_IN_BACKGROUND = 100000L

    fun startFetchConfig(onFinish: () -> Unit) {
        Logger.d("Start fetch remote config")
        
        try {
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10) // 1 giờ, giảm khi debug
                .build()
            remoteConfig.setConfigSettingsAsync(configSettings)

            // Đặt giá trị mặc định (từ XML hoặc Map)
            //remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

            remoteConfig.fetchAndActivate()
                .addOnCompleteListener { task ->
                    Logger.d("fetch remote finish: ${task.result}, ${task.exception?.message}")
                    if (task.isSuccessful) {
                        Logger.d("Fetch remote config succeeded")
                        try {
                            AdManager.fetchAdId(remoteConfig)
                        } catch (e: Exception) {
                            Logger.e("Error fetching ad IDs: ${e.message}")
                        }
                        
                        try {
                            isEnableOldEnhancerApi = remoteConfig.getBoolean("ENABLE_OLD_API_ENHANCER")
                            isEnableOldFaceSwapApi = remoteConfig.getBoolean("ENABLE_OLD_API_FACE_SWAP")
                            isEnableExtraRewardAd = remoteConfig.getBoolean("ENABLE_EXTRA_REWARD_AD")
                            isEnableOldRemoveBgApi = remoteConfig.getBoolean("ENABLE_OLD_API_REMOVE_BACKGROUND")
                            stylePresetRemoteData = remoteConfig.getString("STYLE_PRESET")
                            coverListRemoteData = remoteConfig.getString("COVER_LIST")
                            delayShowLanguageSelectButton = remoteConfig.getLong("DELAY_SHOW_LANGUAGE_SELECTION_BUTTON")
                            isEnableHideSelectLanguageButton = remoteConfig.getBoolean("ENABLE_HIDE_SELECT_LANGUAGE_BUTTON")
                            organizationMail = remoteConfig.getString("ORGANIZATION_MAIL")

                            imgBbKey = remoteConfig.getString("KEY_IMGBB_API")
                            modelsLabKey = remoteConfig.getString("KEY_MODELS_LAB")
                            cloudinaryName = remoteConfig.getString("CLOUDINARY_NAME")
                            cloudinaryUploadPreset = remoteConfig.getString("CLOUDINARY_UPLOAD_PRESET")
                            imgurClientId = remoteConfig.getString("IMGUR_CLIENT_ID")
                            MAX_POLLING_ATTEMPTS = remoteConfig.getLong("MAX_POLLING_ATTEMPTS").toInt()
                            POLLING_INTERVAL_MS = remoteConfig.getLong("POLLING_INTERVAL_MS")
                            TIMEOUT_TO_PROCESS_IN_BACKGROUND = remoteConfig.getLong("TIMEOUT_TO_PROCESS_IN_BACKGROUND")
                        } catch (e: Exception) {
                            Logger.e("Error parsing remote config values: ${e.message}")
                        }
                    } else {
                        Logger.e("Fetch remote config failed: ${task.exception?.message}")
                        // Fallback: sử dụng giá trị mặc định
                        useDefaultValues()
                    }
                    onFinish.invoke()
                }
                .addOnFailureListener { exception ->
                    Logger.e("Remote config fetch failed with exception: ${exception.message}")
                    // Fallback: sử dụng giá trị mặc định
                    useDefaultValues()
                    onFinish.invoke()
                }
        } catch (e: Exception) {
            Logger.e("Error initializing remote config: ${e.message}")
            // Fallback: sử dụng giá trị mặc định
            useDefaultValues()
            onFinish.invoke()
        }
    }
    
    private fun useDefaultValues() {
        Logger.d("Using default values due to remote config failure")
        // Các giá trị đã được set mặc định ở trên
        // Không cần làm gì thêm
    }
}