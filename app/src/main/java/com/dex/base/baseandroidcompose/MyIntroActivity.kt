package com.dex.base.baseandroidcompose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.dex.base.baseandroidcompose.ui.screens.IntroScreen
import com.dex.base.baseandroidcompose.ui.theme.BaseAndroidComposeTheme
import com.dex.base.baseandroidcompose.utils.Logger

class MyIntroActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        Logger.d("MyIntroActivity: onCreate")
        
        setContent {
            BaseAndroidComposeTheme {
                IntroScreen(
                    onIntroComplete = {
                        Logger.d("MyIntroActivity: Intro completed, navigating to main")
                        navigateToMain()
                    }
                )
            }
        }
    }
    
    private fun navigateToMain() {
        // TODO: Navigate to main screen after intro completion
        Logger.d("MyIntroActivity: Navigating to main screen")
        // For now, just finish this activity
        finish()
    }
} 