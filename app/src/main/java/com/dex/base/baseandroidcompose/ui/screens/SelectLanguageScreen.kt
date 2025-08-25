package com.dex.base.baseandroidcompose.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dex.base.baseandroidcompose.ads.NativeAdView
import com.dex.base.baseandroidcompose.data.LanguageData
import com.dex.base.baseandroidcompose.utils.Logger

@Composable
fun SelectLanguageScreen(
    onLanguageSelected: (LanguageData) -> Unit,
    onCheckClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf<LanguageData?>(null) }

    LaunchedEffect(Unit) {
        Logger.d("SelectLanguageScreen: Loading native ad")
        // Preload native ad khi screen được tạo
        // Note: Không thể gọi AdManager.preloadNativeAd() ở đây vì không có context
        // Sẽ được gọi từ SplashActivity
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header với padding
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Language",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Check button chỉ hiện khi user đã select language
            if (selectedLanguage != null) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Confirm",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onCheckClicked() }
                )
            }
        }

        // Language List với padding
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(LanguageData.values()) { language ->
                LanguageItem(
                    language = language,
                    isSelected = language == selectedLanguage,
                    onClick = {
                        selectedLanguage = language
                        onLanguageSelected(language)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Install Button

//        Spacer(modifier = Modifier.height(24.dp))

        // Native Ad - Full width không có padding
        NativeAdView(
            modifier = Modifier.fillMaxWidth(),
            enableButtonOnTop = false
        )
    }
}

@Composable
fun LanguageItem(
    language: LanguageData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFFFF6B35) else Color(0xFFE0E0E0)
    val backgroundColor = if (isSelected) Color(0xFFFFF8F5) else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(
            width = 2.dp,
            color = borderColor
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag Icon
            Image(
                painter = painterResource(id = language.flagIconId),
                contentDescription = "${language.displayName} flag",
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Language Name
            Text(
                text = language.displayName,
                fontSize = 18.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color(0xFF333333) else Color(0xFF666666)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Bỏ tích - chỉ giữ lại viền màu để thể hiện item được chọn
        }
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

@Preview(showBackground = true)
@Composable
fun PreviewLanguageItem() {
    MaterialTheme {
        LanguageItem(
            language = LanguageData.ENGLISH,
            isSelected = true,
            onClick = {}
        )
    }
} 