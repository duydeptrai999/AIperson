package com.dex.base.baseandroidcompose

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dex.base.baseandroidcompose.ads.AdManager
import com.dex.base.baseandroidcompose.ads.BannerAdManager
import com.dex.base.baseandroidcompose.ads.GoogleMobileAdsConsentManager
import com.dex.base.baseandroidcompose.data.LanguageData
import com.dex.base.baseandroidcompose.ui.screens.IntroScreen
import com.dex.base.baseandroidcompose.ui.screens.SelectLanguageScreen
import com.dex.base.baseandroidcompose.ui.theme.BaseAndroidComposeTheme
import com.dex.base.baseandroidcompose.ui.theme.QuickTestTheme
import com.dex.base.baseandroidcompose.utils.Helper
import com.dex.base.baseandroidcompose.utils.Logger
import com.dex.base.baseandroidcompose.utils.MyPref
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MySplashActivity : ComponentActivity() {
    
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    private var isLoading = true
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    
    // Screen states
    private var currentScreen by mutableStateOf<Screen>(Screen.SPLASH)
    private var selectedLanguage by mutableStateOf<LanguageData?>(null)
    
    sealed class Screen {
        object SPLASH : Screen()
        object SELECT_LANGUAGE : Screen()
        object INTRO : Screen()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        Logger.d("onCreate: Starting splash screen")
        
        setContent {
            BaseAndroidComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        Screen.SPLASH -> SplashScreen(
                            isLoading = isLoading,
                            onAdClosed = { navigateToSelectLanguage() },
                            modifier = Modifier.padding(innerPadding)
                        )
                        Screen.SELECT_LANGUAGE -> SelectLanguageScreen(
                            onLanguageSelected = { language -> handleLanguageSelected(language) },
                            onCheckClicked = { navigateToIntro() },
                            modifier = Modifier.padding(innerPadding)
                        )
                        Screen.INTRO -> IntroScreen(
                            onIntroComplete = { 
                                // Lưu trạng thái đã hiển thị intro
                                MyPref.putBoolean(this@MySplashActivity, MyPref.DISPLAYED_INTRO, true)
                                Logger.d("Intro completed, saved DISPLAYED_INTRO = true")
                                navigateToMain() 
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
        checkInternetConnection()
    }

    private fun initApp() {
        FirebaseApp.initializeApp(this)
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent("custom_open_app", null)

        Logger.d("Google Mobile Ads SDK Version: " + MobileAds.getVersion())

        googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(applicationContext)
        googleMobileAdsConsentManager.gatherConsent(this) { error ->
            if (error != null) {
                // Consent not obtained in current session.
                Logger.d("${error.errorCode}: ${error.message}")
            }

            if (googleMobileAdsConsentManager.canRequestAds) {
                initializeMobileAdsSdk()
            }

            if (googleMobileAdsConsentManager.isPrivacyOptionsRequired) {
                // Regenerate the options menu to include a privacy setting.
                invalidateOptionsMenu()
            }
        }
    }

    private fun initializeMobileAdsSdk() {
        Logger.d("initializeMobileAdsSdk")
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        // Set your test devices.
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

        // [START initialize_sdk]
        CoroutineScope(Dispatchers.IO).launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MySplashActivity) {}
            // [START_EXCLUDE silent]

            RemoteConfigManager.startFetchConfig {
                // Xử lý load và show quảng cáo sau khi fetch remote config thành công
                handleRemoteConfigComplete()
            }
        }
        // [END initialize_sdk]
    }

    /**
     * Xử lý sau khi fetch remote config hoàn thành
     */
    private fun handleRemoteConfigComplete() {
        Logger.d("Remote config completed, starting ad flow")
        
        // Chuyển về main thread để xử lý UI
        runOnUiThread {
            // Bắt đầu load quảng cáo Interstitial
            startInterstitialAdFlow()
        }
    }

    /**
     * Bắt đầu flow load và show quảng cáo Interstitial
     */
    private fun startInterstitialAdFlow() {
        Logger.d("Starting interstitial ad flow")
        
        // Load quảng cáo Interstitial
        AdManager.loadInterAd(this) { success, message ->
            if (success) {
                Logger.d("Splash - Interstitial ad loaded successfully: $message")
                
                // Show quảng cáo sau khi load thành công
                showInterstitialAd()
            } else {
                Logger.e("Failed to load interstitial ad: $message")
                // Nếu load thất bại, chuyển màn hình sau delay
                navigateToSelectLanguageAfterDelay()
            }
        }
        AdManager.preloadNativeAd(this) { success, message ->
            Logger.d("Splash - preloadNativeAd: $success - $message")
        }
        BannerAdManager.getInstance().preloadBannerDefault(this)
    }

    /**
     * Show quảng cáo Interstitial sử dụng AdManager
     */
    private fun showInterstitialAd() {
        Logger.d("Showing interstitial ad using AdManager")
        
        AdManager.showInterAd(this) { success ->
            if (success) {
                Logger.d("Interstitial ad closed by user, navigating to select language")
                // User đã đóng quảng cáo, chuyển màn hình
                navigateToSelectLanguage()
            } else {
                Logger.e("Failed to show interstitial ad, navigating to select language")
                // Quảng cáo bị lỗi, chuyển màn hình
                navigateToSelectLanguage()
            }
        }
    }

    /**
     * Chuyển màn hình sau delay nếu có lỗi
     */
    private fun navigateToSelectLanguageAfterDelay() {
        Logger.d("Navigating to select language after delay due to ad failure")
        // Chuyển màn hình sau 2 giây nếu quảng cáo thất bại
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            navigateToSelectLanguage()
        }, 2000)
    }
    
    private fun navigateToSelectLanguage() {
        Logger.d("navigateToSelectLanguage: Moving to SelectLanguageScreen")
        
        // Kiểm tra xem đã hiển thị intro chưa
        val hasDisplayedIntro = MyPref.getBoolean(this, MyPref.DISPLAYED_INTRO, false)
        if (hasDisplayedIntro) {
            Logger.d("Intro already displayed, going directly to MainActivity")
            navigateToMain()
        } else {
            Logger.d("First time, showing SelectLanguageScreen")
            currentScreen = Screen.SELECT_LANGUAGE
        }
        isLoading = false
    }
    
    private fun navigateToIntro() {
        Logger.d("navigateToIntro: Moving to IntroScreen")
        currentScreen = Screen.INTRO
    }
    
    private fun navigateToMain() {
        Logger.d("navigateToMain: Starting MainActivity")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleLanguageSelected(language: LanguageData) {
        Logger.d("Language selected: ${language.displayName} (${language.languageCode})")
        selectedLanguage = language
        
        // Preload native ad khi user tương tác
        AdManager.preloadNativeAd(this) { success, message ->
            if (success) {
                Logger.d("Native ad preloaded successfully: $message")
            } else {
                Logger.e("Failed to preload native ad: $message")
            }
        }
    }

    private fun handleInstallClicked() {
        Logger.d("Install button clicked")
        // Có thể xử lý logic khác nếu cần
    }

    private fun checkInternetConnection() {
        if (Helper.isInternetAvailable(this)) {
            initApp()
        } else {
            showNoInternetDialog()
        }
    }

    private fun showNoInternetDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.no_internet_title)
            .setMessage(R.string.no_internet_message)
            .setCancelable(false)
            .setPositiveButton(R.string.retry) { dialog, _ ->
                dialog.dismiss()
                checkInternetConnection()
            }
            .setNegativeButton(R.string.exit) { _, _ ->
                finish()
            }
            .create()
        dialog.show()
    }
}

@Composable
fun SplashScreen(
    isLoading: Boolean,
    onAdClosed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section - để trống để tạo khoảng cách với status bar
            Spacer(modifier = Modifier.height(20.dp))
            
            // Center section with app icon and name - căn giữa top và bottom
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // App icon
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(28.dp)),
                    contentScale = ContentScale.Fit
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // App name
                Text(
                    text = "Base Android Compose",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(56.dp))
                
                // Subtitle hoặc description (tùy chọn)
//                Text(
//                    text = "Welcome to Math Quiz",
//                    fontSize = 18.sp,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    textAlign = TextAlign.Center
//                )
            }
            
            // Bottom section with progress bar and disclaimer
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                // Progress bar
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    
//                    Spacer(modifier = Modifier.height(16.dp))
                    
//                    Text(
//                        text = "Loading...",
//                        fontSize = 16.sp,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Disclaimer text
                Text(
                    text = "This action may contain ads",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

//@Composable
//fun IntroScreen(
//    onIntroComplete: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var currentSlideIndex by remember { mutableStateOf(0) }
//    val slides = IntroSlides.slides
//
//    LaunchedEffect(currentSlideIndex) {
//        Logger.d("IntroScreen: Current slide: ${currentSlideIndex + 1}/${slides.size}")
//    }
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .background(Color.White)
//    ) {
//        // Intro Content Section với Image, Gradient và Text overlay
//        Box(
//            modifier = Modifier
//                .weight(1f)
//                .fillMaxWidth()
//        ) {
//            // Background Image - Full height
//            Image(
//                painter = painterResource(id = slides[currentSlideIndex].imageResId),
//                contentDescription = "Intro ${currentSlideIndex + 1}",
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Crop
//            )
//
//            // Gradient overlay - Nửa dưới từ trong suốt đến trắng 100%
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(
//                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
//                            colors = listOf(
//                                Color.Transparent,
//                                Color.Transparent,
//                                Color.White.copy(alpha = 0.3f),
//                                Color.White.copy(alpha = 0.7f),
//                                Color.White
//                            ),
//                            startY = 0f,
//                            endY = Float.POSITIVE_INFINITY
//                        )
//                    )
//            )
//
//            // Text Content overlay - Bottom aligned
//            Column(
//                modifier = Modifier
//                    .align(Alignment.BottomStart)
//                    .fillMaxWidth()
//                    .padding(horizontal = 24.dp, vertical = 32.dp)
//            ) {
//                // Title ở trên
//                Text(
//                    text = stringResource(id = slides[currentSlideIndex].titleResId),
//                    fontSize = 24.sp, // Giảm từ 28sp xuống 24sp
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Black,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                // Description ở dưới
//                Text(
//                    text = stringResource(id = slides[currentSlideIndex].descriptionResId),
//                    fontSize = 16.sp,
//                    color = Color.Black,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(32.dp))
//
//                // Page Indicator and Next Button Row
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    // Page Indicator
//                    Row(
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        repeat(slides.size) { index ->
//                            Box(
//                                modifier = Modifier
//                                    .size(8.dp)
//                                    .clip(CircleShape)
//                                    .background(
//                                        if (index == currentSlideIndex) {
//                                            Color(0xFF0099cc) // Primary color
//                                        } else {
//                                            Color(0xFFE0E0E0) // Light gray
//                                        }
//                                    )
//                            )
//                        }
//                    }
//
//                    // Next Button
//                    if (currentSlideIndex < slides.size - 1) {
//                        OutlinedButton(
//                            onClick = {
//                                currentSlideIndex++
//                                Logger.d("IntroScreen: Next button clicked, moving to slide ${currentSlideIndex + 1}")
//                            },
//                            shape = RoundedCornerShape(8.dp),
//                            border = BorderStroke(
//                                width = 2.dp,
//                                color = Color(0xFF0099cc)
//                            ),
//                            colors = ButtonDefaults.outlinedButtonColors(
//                                contentColor = Color(0xFF0099cc)
//                            ),
//                            modifier = Modifier.width(140.dp).height(46.dp)
//                        ) {
//                            Text(
//                                text = "NEXT",
//                                fontSize = 14.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//                        }
//                    } else {
//                        // Last slide - Complete button
//                        OutlinedButton(
//                            onClick = {
//                                Logger.d("IntroScreen: Complete button clicked")
//                                onIntroComplete()
//                            },
//                            shape = RoundedCornerShape(8.dp),
//                            border = BorderStroke(
//                                width = 2.dp,
//                                color = Color(0xFF0099cc)
//                            ),
//                            colors = ButtonDefaults.outlinedButtonColors(
//                                contentColor = Color(0xFF0099cc)
//                            ),
//                            modifier = Modifier.width(140.dp).height(46.dp)
//                        ) {
//                            Text(
//                                text = "Next",
//                                fontSize = 14.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//                        }
//                    }
//                }
//            }
//        }
//
//        // Native Ad Section - Full width với enableOutlineButton
//        NativeAdView(
//            modifier = Modifier.fillMaxWidth(),
//            enableButtonOnTop = false,
//            enableOutlineButton = true
//        )
//    }
//}

@Preview(showBackground = true)
@Composable
fun PreviewSplashScreen() {
    QuickTestTheme {
        SplashScreen(
            isLoading = true,
            onAdClosed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSelectLanguageScreen() {
    MaterialTheme {
        SelectLanguageScreen(
            onLanguageSelected = {},
            onCheckClicked = {}
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewIntroScreen() {
//    MaterialTheme {
//        IntroScreen(
//            onIntroComplete = {}
//        )
//    }
//}