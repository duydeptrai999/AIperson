package com.dex.base.baseandroidcompose.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dex.base.baseandroidcompose.ads.NativeAdViewController
import com.dex.base.baseandroidcompose.ads.nativeAdViewWithRefresh
import com.dex.base.baseandroidcompose.data.IntroSlides
import com.dex.base.baseandroidcompose.utils.Logger

@Composable
fun IntroScreen(
    onIntroComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSlideIndex by remember { mutableStateOf(0) }
    val slides = IntroSlides.slides
    
    // Controller để điều khiển NativeAdView
    var nativeAdController by remember { mutableStateOf<NativeAdViewController?>(null) }

    LaunchedEffect(currentSlideIndex) {
        Logger.d("IntroScreen: Current slide: ${currentSlideIndex + 1}/${slides.size}")
        
        // Gọi showNewNativeAd khi slide thay đổi (trừ slide đầu tiên)
        if (currentSlideIndex > 0 && nativeAdController != null) {
            Logger.d("IntroScreen: Refreshing native ad for slide ${currentSlideIndex + 1}")
            nativeAdController?.showNewNativeAd()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Intro Content Section với Image, Gradient và Text overlay
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Background Image - Full height
            Image(
                painter = painterResource(id = slides[currentSlideIndex].imageResId),
                contentDescription = "Intro ${currentSlideIndex + 1}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay - Nửa dưới từ trong suốt đến trắng 100%
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.White.copy(alpha = 0.3f),
                                Color.White.copy(alpha = 0.7f),
                                Color.White
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            // Text Content overlay - Bottom aligned
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                // Title ở trên
                Text(
                    text = stringResource(id = slides[currentSlideIndex].titleResId),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description ở dưới
                Text(
                    text = stringResource(id = slides[currentSlideIndex].descriptionResId),
                    fontSize = 16.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Page Indicator and Next Button Row - Tách riêng ra khỏi box Intro Content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Page Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(slides.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentSlideIndex) {
                                    Color(0xFF0099cc) // Primary color
                                } else {
                                    Color(0xFFE0E0E0) // Light gray
                                }
                            )
                    )
                }
            }

            // Next Button
            if (currentSlideIndex < slides.size - 1) {
                OutlinedButton(
                    onClick = {
                        currentSlideIndex++
                        Logger.d("IntroScreen: Next button clicked, moving to slide ${currentSlideIndex + 1}")
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        width = 2.dp,
                        color = Color(0xFF0099cc)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF0099cc)
                    ),
                    modifier = Modifier.width(140.dp).height(46.dp)
                ) {
                    Text(
                        text = "NEXT",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Last slide - Complete button
                OutlinedButton(
                    onClick = {
                        Logger.d("IntroScreen: Complete button clicked")
                        onIntroComplete()
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        width = 2.dp,
                        color = Color(0xFF0099cc)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF0099cc)
                    ),
                    modifier = Modifier.width(140.dp).height(46.dp)
                ) {
                    Text(
                        text = "Next",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Native Ad Section - Full width với enableOutlineButton và refresh capability
        val controller = nativeAdViewWithRefresh(
            modifier = Modifier.fillMaxWidth(),
            enableButtonOnTop = false,
            enableOutlineButton = true,
            onAdLoaded = { nativeAd ->
                Logger.d("IntroScreen: Native ad loaded: ${nativeAd != null}")
            }
        )
        
        // Lưu controller để có thể gọi showNewNativeAd
        LaunchedEffect(controller) {
            nativeAdController = controller
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIntroScreen() {
    MaterialTheme {
        IntroScreen(
            onIntroComplete = {}
        )
    }
} 