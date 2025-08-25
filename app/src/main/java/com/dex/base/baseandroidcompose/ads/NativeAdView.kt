package com.dex.base.baseandroidcompose.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.dex.base.baseandroidcompose.utils.Helper.dpToPx
import com.dex.base.baseandroidcompose.utils.Logger
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd

@Composable
fun NativeAdView(
    modifier: Modifier = Modifier,
    enableButtonOnTop: Boolean = false,
    enableOutlineButton: Boolean = false,
    onAdLoaded: ((NativeAd?) -> Unit)? = null
) {
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Logger.d("NativeAdView: Loading native ad")
        AdManager.getNativeAd(context) { ad ->
            nativeAd = ad
            Logger.d("NativeAdView: Native ad loaded: ${ad != null}")
            onAdLoaded?.invoke(ad)
        }
    }

    nativeAd?.let { ad ->
        NativeAdContent(
            nativeAd = ad,
            enableButtonOnTop = enableButtonOnTop,
            enableOutlineButton = enableOutlineButton,
            modifier = modifier
        )
    } ?: run {
        // Placeholder khi chưa có ad
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Gray.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading ad...",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

/**
 * Method để refresh native ad mới
 * Destroy native ad hiện tại và load native ad mới từ AdManager
 */
@Composable
fun nativeAdViewWithRefresh(
    modifier: Modifier = Modifier,
    enableButtonOnTop: Boolean = false,
    enableOutlineButton: Boolean = false,
    onAdLoaded: ((NativeAd?) -> Unit)? = null
): NativeAdViewController {
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    val controller = remember { NativeAdViewController() }
    
    // Gán controller để có thể gọi từ bên ngoài
    LaunchedEffect(Unit) {
        controller.setRefreshCallback { 
            Logger.d("NativeAdViewWithRefresh: Refreshing native ad")
            // Destroy native ad hiện tại
            nativeAd?.destroy()
            nativeAd = null
            isLoading = true
            
            // Load native ad mới
            AdManager.getNativeAd(context) { newAd ->
                Logger.d("NativeAdViewWithRefresh: New native ad loaded: ${newAd != null}")
                nativeAd = newAd
                isLoading = false
                onAdLoaded?.invoke(newAd)
            }
        }
    }

    LaunchedEffect(Unit) {
        Logger.d("NativeAdViewWithRefresh: Loading initial native ad")
        isLoading = true
        AdManager.getNativeAd(context) { ad ->
            Logger.d("NativeAdViewWithRefresh: Initial native ad loaded: ${ad != null}")
            nativeAd = ad
            isLoading = false
            onAdLoaded?.invoke(ad)
        }
    }

    // Hiển thị loading state hoặc native ad
    if (isLoading) {
        // Loading placeholder
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Gray.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading ad...",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    } else {
        nativeAd?.let { ad ->
            NativeAdContent(
                nativeAd = ad,
                enableButtonOnTop = enableButtonOnTop,
                enableOutlineButton = enableOutlineButton,
                modifier = modifier
            )
        } ?: run {
            // Placeholder khi chưa có ad
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Gray.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No native ad available",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
    
    return controller
}

/**
 * Controller class để điều khiển NativeAdView từ bên ngoài
 */
class NativeAdViewController {
    private var refreshCallback: (() -> Unit)? = null
    
    fun setRefreshCallback(callback: () -> Unit) {
        refreshCallback = callback
    }
    
    fun showNewNativeAd() {
        Logger.d("NativeAdViewController: showNewNativeAd called")
        refreshCallback?.invoke()
    }
}

@Composable
private fun NativeAdContent(
    nativeAd: NativeAd,
    enableButtonOnTop: Boolean = false,
    enableOutlineButton: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Lấy màu primary từ Compose theme
    val primaryColor = MaterialTheme.colorScheme.primary

    // Sử dụng một AndroidView duy nhất chứa toàn bộ NativeAdView để bind đúng cách
    AndroidView(
        factory = { context ->
            // Tạo NativeAdView container
            com.google.android.gms.ads.nativead.NativeAdView(context).apply {
                // Tạo layout container
                val linearLayout = android.widget.LinearLayout(context).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    setPadding(0, 12, 0, 12) // 0 horizontal, 4dp = 12px vertical
                }

                // Top divider (conditional)
                val divider = android.view.View(context).apply {
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        3 // 1dp = 3px
                    )
                    setBackgroundColor(android.graphics.Color.parseColor("#CCEBEBEB"))
                }
                if (!enableButtonOnTop) {
                    linearLayout.addView(divider)
                }

                // Call to Action Button - Đặt ở top trên cùng nếu enableButtonOnTop = true
                if (enableButtonOnTop) {
                    val ctaButton = android.widget.Button(context).apply {
                        text = nativeAd.callToAction ?: "INSTALL"
                        textSize = 14f
                        setTextColor(context.getColor(android.R.color.white))
                        // Sử dụng màu primary từ Compose theme
                        setBackgroundColor(context.getColor(android.R.color.holo_blue_dark))
                        // Bo cong button
                        background = android.graphics.drawable.GradientDrawable().apply {
                            shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                            cornerRadius = 24f // 8dp = 24px
                            setColor(context.getColor(android.R.color.holo_blue_dark)) // Ensure color is set for drawable
                        }
                        layoutParams = android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            138 // 46dp = 138px
                        ).apply {
                            leftMargin = 24 // 8dp = 24px
                            rightMargin = 24 // 8dp = 24px
                            bottomMargin = 12 // 4dp = 12px
                        }
                    }
                    this.callToActionView = ctaButton
                    linearLayout.addView(ctaButton)
                }

                // Top section với icon, AD label và headline
                val topRow = android.widget.LinearLayout(context).apply {
                    orientation = android.widget.LinearLayout.HORIZONTAL
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        leftMargin = 24 // 8dp = 24px
                        rightMargin = 24 // 8dp = 24px
                        topMargin = 12
                    }
                }

                // App Icon
                val iconView = android.widget.ImageView(context).apply {
                    layoutParams = android.widget.LinearLayout.LayoutParams(162, 162) // 54dp = 162px
                    scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                    background = context.getDrawable(android.R.drawable.ic_menu_gallery) // Fallback background
                    setPadding(4, 4, 4, 4)
                    if (nativeAd.icon != null) {
                        setImageDrawable(nativeAd.icon?.drawable)
                    }
                }
                this.iconView = iconView
                topRow.addView(iconView)

                // Spacer
                val spacer1 = android.view.View(context).apply {
                    layoutParams = android.widget.LinearLayout.LayoutParams(24, 0) // 8dp = 24px
                }
                topRow.addView(spacer1)

                // Right content container
                val rightContent = android.widget.LinearLayout(context).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        0,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                }

                // AD Label + Headline Row
                val labelHeadlineRow = android.widget.LinearLayout(context).apply {
                    orientation = android.widget.LinearLayout.HORIZONTAL
                }

                // AD Label
                val adLabel = android.widget.TextView(context).apply {
                    text = "AD"
                    textSize = 10f
                    setTextColor(android.graphics.Color.BLACK)
                    setBackgroundColor(android.graphics.Color.parseColor("#FFCC00"))
                    setPadding(11, 3, 11, 3) // 3.8dp = 11px, 0.1dp = 0.3px ≈ 3px
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
                labelHeadlineRow.addView(adLabel)

                // Spacer
                val spacer2 = android.view.View(context).apply {
                    layoutParams = android.widget.LinearLayout.LayoutParams(12, 0) // 4dp = 12px
                }
                labelHeadlineRow.addView(spacer2)

                // Headline
                val headlineView = android.widget.TextView(context).apply {
                    text = nativeAd.headline ?: "Flood-It!"
                    textSize = 15f
                    setTextColor(android.graphics.Color.BLACK)
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    maxLines = 1
                    ellipsize = android.text.TextUtils.TruncateAt.END
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }
                this.headlineView = headlineView
                labelHeadlineRow.addView(headlineView)

                rightContent.addView(labelHeadlineRow)

                // Body text - Max 2 dòng
                val bodyView = android.widget.TextView(context).apply {
                    text = nativeAd.body ?: "Install Flood-It! App for free! Free Popular Casual Game"
                    textSize = 12f
                    setTextColor(android.graphics.Color.BLACK)
                    maxLines = 2 // Thay đổi từ 1 dòng thành 2 dòng
                    ellipsize = android.text.TextUtils.TruncateAt.END
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 12 // 4dp = 12px
                    }
                }
                this.bodyView = bodyView
                rightContent.addView(bodyView)

                topRow.addView(rightContent)
                linearLayout.addView(topRow)
                
                // MediaView
                val mediaView = MediaView(context).apply {
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        540 // 180dp = 540px
                    ).apply {
                        topMargin = 18 // 6dp = 18px
                        leftMargin = 24 // 8dp = 24px
                        rightMargin = 24 // 8dp = 24px
                    }
                    setBackgroundColor(android.graphics.Color.parseColor("#F5F5F5"))
                }
                this.mediaView = mediaView
                linearLayout.addView(mediaView)

                // Call to Action Buttons Section
                if (!enableButtonOnTop) {
                    // Container cho các buttons ở bottom
                    val buttonContainer = android.widget.LinearLayout(context).apply {
                        orientation = android.widget.LinearLayout.HORIZONTAL
                        layoutParams = android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            topMargin = 12 // 4dp = 12px
                            leftMargin = 24 // 8dp = 24px
                            rightMargin = if (enableOutlineButton) dpToPx(context, 24f) else 24 // 8dp = 24px
                        }
                    }

                    if (enableOutlineButton) {
                        // Outline Button (style giống IntroScreen) - căn phải với margin
                        
                        // Tạo spacer để đẩy button sang phải
                        val spacer = android.view.View(context).apply {
                            layoutParams = android.widget.LinearLayout.LayoutParams(
                                0,
                                0,
                                1f // weight = 1 để chiếm hết không gian còn lại
                            )
                        }
                        buttonContainer.addView(spacer)
                        
                        val outlineButton = android.widget.Button(context).apply {
                            text = nativeAd.callToAction ?: "INSTALL"
                            textSize = 14f
                            setTextColor(context.getColor(android.R.color.holo_blue_dark))
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                            // Outline style với border
                            background = android.graphics.drawable.GradientDrawable().apply {
                                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                                cornerRadius = 24f // 8dp = 24px
                                setStroke(6, context.getColor(android.R.color.holo_blue_dark)) // 2dp = 6px stroke
                                setColor(android.graphics.Color.TRANSPARENT) // Transparent background
                            }
                            layoutParams = android.widget.LinearLayout.LayoutParams(
                                dpToPx(context, 140f), // 140dp = 420px
                                dpToPx(context, 46f) // 46dp = 138px
                            )
                        }
                        this.callToActionView = outlineButton
                        buttonContainer.addView(outlineButton)
                    } else {
                        // Normal filled button
                        val ctaButton = android.widget.Button(context).apply {
                            text = nativeAd.callToAction ?: "INSTALL"
                            textSize = 14f
                            setTextColor(context.getColor(android.R.color.white))
                            // Sử dụng màu primary từ Compose theme
                            setBackgroundColor(context.getColor(android.R.color.holo_blue_dark))
                            // Bo cong button
                            background = android.graphics.drawable.GradientDrawable().apply {
                                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                                cornerRadius = 24f // 8dp = 24px
                                setColor(context.getColor(android.R.color.holo_blue_dark)) // Ensure color is set for drawable
                            }
                            layoutParams = android.widget.LinearLayout.LayoutParams(
                                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                                138 // 46dp = 138px
                            )
                        }
                        this.callToActionView = ctaButton
                        buttonContainer.addView(ctaButton)
                    }

                    linearLayout.addView(buttonContainer)
                }

                // Add main layout to NativeAdView
                addView(linearLayout)

                // Bind với NativeAd
                setNativeAd(nativeAd)
            }
        },
        modifier = modifier,
        update = { nativeAdView ->
            // Update khi có NativeAd mới
            nativeAdView.setNativeAd(nativeAd)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNativeAdView() {
    MaterialTheme {
        NativeAdView(
            modifier = Modifier.fillMaxWidth(),
            enableButtonOnTop = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNativeAdViewWithButtonOnTop() {
    MaterialTheme {
        NativeAdView(
            modifier = Modifier.fillMaxWidth(),
            enableButtonOnTop = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNativeAdContent() {
    MaterialTheme {
        // Mock native ad for preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(Color.White)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Native Ad Preview",
                color = Color.Gray
            )
        }
    }
} 