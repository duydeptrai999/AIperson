package com.dex.base.baseandroidcompose.ads

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.dex.base.baseandroidcompose.utils.Logger
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.*

//enum class BannerAdSize { DEFAULT, FULL_WIDTH, EXPAND_TOP }

@Composable
fun BannerAdView(
    size: BannerAdSize,
    modifier: Modifier = Modifier.fillMaxWidth(),
    preload: Boolean = true,
) {
    val context = LocalContext.current
    var adView by remember { mutableStateOf<AdView?>(null) }
    val adUnitId = AdManager.ADMOB_BANNER_AD_ID
    when (size) {
        BannerAdSize.DEFAULT -> {
            // TTL + cache bởi BannerAdManager
            LaunchedEffect(preload, adUnitId) {
                if (preload) {
                    BannerAdManager.getInstance()
                        .preloadBannerDefault(context) { success, msg ->
                            Logger.d("preloadBannerDefault -> success: $success - msg: $msg")
                        }
                }
            }
            LaunchedEffect(adUnitId) {
                BannerAdManager.getInstance()
                    .getBannerDefault(context) { ready ->
                        adView = ready
                    }
            }

            adView?.let { v ->
                AndroidView(
                    modifier = modifier,
                    factory = {
                        (v.parent as? ViewGroup)?.removeView(v)
                        v.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        v
                    }
                )
            }
        }

        BannerAdSize.FULL_WIDTH, BannerAdSize.EXPAND_TOP, BannerAdSize.EXPAND_BOTTOM -> {
            // Load trực tiếp Adaptive (không qua manager)
            BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
                val widthDp = maxWidth.value.toInt().coerceAtLeast(200)
                val activity = context.findActivity()

                LaunchedEffect(adUnitId, widthDp, size, activity) {
                    if (activity == null) {
                        Logger.e("Activity null – không thể tính Adaptive size")
                        return@LaunchedEffect
                    }

                    val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, widthDp)
                    val v = AdView(context.applicationContext).apply {
                        this.adUnitId = adUnitId
                        setAdSize(adSize)
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }

                    val reqBuilder = AdRequest.Builder()
                    if (size == BannerAdSize.EXPAND_TOP || size == BannerAdSize.EXPAND_BOTTOM) {
                        val extras = Bundle()
                        extras.putString("collapsible", if (size == BannerAdSize.EXPAND_TOP) "top" else "bottom")
                        reqBuilder.addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()
                    }

                    v.adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            super.onAdLoaded()
                            Logger.d("BannerAdView onAdLoaded - $size, collapsible = ${v.isCollapsible}")
                        }
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            Logger.d("BannerAdView onAdFailedToLoad - $size: $p0")
                        }
                    }
                    v.loadAd(reqBuilder.build())
                    adView = v
                }

                adView?.let { v ->
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(),
                        factory = {
                            (v.parent as? ViewGroup)?.removeView(v)
                            v
                        }
                    )
                } ?: AndroidView(
                    modifier = Modifier.fillMaxWidth(),
                    factory = { View(it) } // placeholder để layout đo width
                )
            }
        }
    }
}

// Helper: lấy Activity từ Context
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}
