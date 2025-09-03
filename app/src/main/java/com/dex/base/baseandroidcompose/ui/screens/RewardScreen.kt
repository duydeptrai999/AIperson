package com.dex.base.baseandroidcompose.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val pointsFromAds by viewModel.pointsFromAds.collectAsState()
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

        Spacer(modifier = Modifier.height(16.dp))
    }
}