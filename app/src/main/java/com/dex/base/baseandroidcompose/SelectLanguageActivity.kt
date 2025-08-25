package com.dex.base.baseandroidcompose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.dex.base.baseandroidcompose.ads.AdManager
import com.dex.base.baseandroidcompose.data.LanguageData
import com.dex.base.baseandroidcompose.ui.screens.SelectLanguageScreen
import com.dex.base.baseandroidcompose.ui.theme.BaseAndroidComposeTheme
import com.dex.base.baseandroidcompose.utils.Logger

class SelectLanguageActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        Logger.d("SelectLanguageActivity: onCreate")
        
        setContent {
            BaseAndroidComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SelectLanguageScreen(
                        onLanguageSelected = { language ->
                            handleLanguageSelected(language)
                        },
                        onCheckClicked = {
                            handleCheckButtonClicked()
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun handleLanguageSelected(language: LanguageData) {
        Logger.d("SelectLanguageActivity: Language selected: ${language.displayName} (${language.languageCode})")
        
        // Lưu language được chọn vào SharedPreferences hoặc database
        // Có thể sử dụng MyPref hoặc tạo method riêng
        
        // Preload native ad khi user tương tác
        AdManager.preloadNativeAd(this) { success, message ->
            if (success) {
                Logger.d("SelectLanguageActivity: Native ad preloaded successfully: $message")
            } else {
                Logger.e("SelectLanguageActivity: Failed to preload native ad: $message")
            }
        }
    }

    private fun handleInstallClicked() {
        Logger.d("SelectLanguageActivity: Install button clicked")
        
        // Xử lý logic khi user click INSTALL
        // Có thể chuyển màn hình hoặc thực hiện action khác
        
        // Ví dụ: Chuyển về MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleCheckButtonClicked() {
        Logger.d("SelectLanguageActivity: Check button clicked, navigating to Intro")
        
        // Navigate đến IntroActivity
        val intent = Intent(this, MyIntroActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        Logger.d("SelectLanguageActivity: onResume")
        
        // Preload native ad khi activity resume
        AdManager.preloadNativeAd(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d("SelectLanguageActivity: onDestroy")
    }
} 