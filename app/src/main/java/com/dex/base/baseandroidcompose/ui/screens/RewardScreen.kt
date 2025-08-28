package com.dex.base.baseandroidcompose.ui.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dex.base.baseandroidcompose.R
import com.dex.base.baseandroidcompose.ui.theme.BaseAndroidComposeTheme
import com.dex.base.baseandroidcompose.ui.theme.*
import com.dex.base.baseandroidcompose.ui.viewmodels.RewardsViewModel

data class Reward(
    val id: String,
    val title: String,
    val description: String,
    val pointsCost: Int,
    val icon: ImageVector,
    val isAvailable: Boolean = true,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardScreen(
    onBackClick: () -> Unit = {},
    viewModel: RewardsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as Activity
    
    // Collect states from ViewModel
    val userPoints by viewModel.userPoints.collectAsState()
    val adStatus by viewModel.adStatus.collectAsState()
    val isWatchingAd by viewModel.isWatchingAd.collectAsState()
    val lastEarnedPoints by viewModel.lastEarnedPoints.collectAsState()
    val totalAdsWatched by viewModel.totalAdsWatched.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // Initialize ads when screen loads
    LaunchedEffect(Unit) {
        viewModel.initializeAds(context)
    }
    
    // Show snackbar for earned points
    LaunchedEffect(lastEarnedPoints) {
        if (lastEarnedPoints > 0) {
            // Points earned notification will be handled by UI
        }
    }
    
    val sampleRewards = remember {
        listOf(
            Reward(
                id = "1",
                title = "Premium Weather Forecast",
                description = "7-day detailed weather forecast with hourly updates",
                pointsCost = 500,
                icon = Icons.Default.Star,
                category = "Premium Features"
            ),
            Reward(
                id = "2",
                title = "Custom Weather Alerts",
                description = "Personalized weather notifications based on your preferences",
                pointsCost = 300,
                icon = Icons.Default.Star,
                category = "Notifications"
            ),
            Reward(
                id = "3",
                title = "Weather Widget Pack",
                description = "Beautiful home screen widgets with weather information",
                pointsCost = 800,
                icon = Icons.Default.Star,
                category = "Widgets"
            ),
            Reward(
                id = "4",
                title = "Ad-Free Experience",
                description = "Remove all advertisements for 30 days",
                pointsCost = 1000,
                icon = Icons.Default.Star,
                category = "Premium Features"
            ),
            Reward(
                id = "5",
                title = "Weather History Access",
                description = "Access to historical weather data and trends",
                pointsCost = 600,
                icon = Icons.Default.Star,
                category = "Data Access"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyBlue)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.rewards_title),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = DeepSkyBlue
            )
        )

        // Points Balance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = CloudWhite
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.your_points),
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = userPoints.toString(),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = PointsBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.points_available),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                if (lastEarnedPoints > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "+$lastEarnedPoints points earned!",
                        fontSize = 14.sp,
                        color = SunYellow,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Reward Ad Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = CloudWhite
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Watch Ad for Points",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Earn points by watching reward ads",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Status: $adStatus",
                            fontSize = 12.sp,
                            color = if (viewModel.isAdReady()) {
                                PointsBlue
                            } else {
                                Color.Red
                            }
                        )
                        if (totalAdsWatched > 0) {
                            Text(
                                text = "Ads watched: $totalAdsWatched",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    
                    Column {
                        Button(
                            onClick = {
                                viewModel.showRewardedAd(activity) { points ->
                                    // Points earned callback handled by ViewModel
                                }
                            },
                            enabled = viewModel.isAdReady() && !isWatchingAd && !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SunYellow,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.width(120.dp)
                        ) {
                            if (isWatchingAd || isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.Black
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Watch")
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedButton(
                            onClick = {
                                viewModel.refreshAd(context)
                            },
                            enabled = !isLoading && !isWatchingAd,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = PointsBlue
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.width(120.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Refresh")
                        }
                    }
                }
                
                // Error message
                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Red.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = error,
                                fontSize = 12.sp,
                                color = Color.Red,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = { viewModel.clearErrorMessage() }
                            ) {
                                Text("Dismiss", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Rewards List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.available_rewards),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(sampleRewards) { reward ->
                RewardItem(
                    reward = reward,
                    userPoints = userPoints,
                    onRedeemClick = {
                        // Handle redeem logic
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun RewardItem(
    reward: Reward,
    userPoints: Int,
    onRedeemClick: () -> Unit
) {
    val canAfford = userPoints >= reward.pointsCost
    val cardColor = if (canAfford) CloudWhite else Color.Gray.copy(alpha = 0.3f)
    val textColor = if (canAfford) Color.Black else Color.Gray

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (canAfford) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reward Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (canAfford) RewardGold.copy(alpha = 0.2f)
                        else Color.Gray.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = reward.icon,
                    contentDescription = null,
                    tint = if (canAfford) RewardGold else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Reward Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reward.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reward.description,
                    fontSize = 14.sp,
                    color = textColor.copy(alpha = 0.7f),
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = PointsBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${reward.pointsCost} ${stringResource(R.string.points)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = PointsBlue
                    )
                }
            }

            // Redeem Button
            Button(
                onClick = onRedeemClick,
                enabled = canAfford && reward.isAvailable,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canAfford) SunYellow else Color.Gray,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = if (canAfford) stringResource(R.string.redeem)
                    else stringResource(R.string.need_more_points),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}