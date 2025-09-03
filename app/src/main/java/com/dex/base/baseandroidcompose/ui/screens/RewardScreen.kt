package com.dex.base.baseandroidcompose.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Task
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

    // Gamification states
    val dailyCheckInStatus by viewModel.dailyCheckInStatus.collectAsState()
    val userStreak by viewModel.userStreak.collectAsState()
    val dailyChallenges by viewModel.dailyChallenges.collectAsState()
    val canCheckInToday by viewModel.canCheckInToday.collectAsState()
    val streakBonus by viewModel.streakBonus.collectAsState()
    val completedChallenges by viewModel.completedChallenges.collectAsState()

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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyBlue)
    ) {
        item {
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
        }

        item {
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
        }

        item {
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
        }

        item {
            // Daily Check-in Card
            DailyCheckInCard(
                canCheckIn = canCheckInToday,
                currentStreak = userStreak?.currentStreak ?: 0,
                longestStreak = userStreak?.longestStreak ?: 0,
                streakBonus = streakBonus,
                onCheckIn = { viewModel.performDailyCheckIn() },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item {
            // Daily Challenges Card
            DailyChallengesCard(
                challenges = dailyChallenges,
                completedCount = viewModel.getTodayCompletedChallenges(),
                totalCount = viewModel.getTodayTotalChallenges(),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DailyCheckInCard(
            canCheckIn: Boolean,
            currentStreak: Int,
            longestStreak: Int,
            streakBonus: Int,
            onCheckIn: () -> Unit,
            modifier: Modifier = Modifier
        ) {
            Card(
                modifier = modifier.fillMaxWidth(),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = PointsBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.daily_check_in),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.daily_check_in_description),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Button(
                            onClick = onCheckIn,
                            enabled = canCheckIn,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canCheckIn) SunYellow else Color.Gray,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (canCheckIn) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.check_in))
                                }
                            } else {
                                Text(stringResource(R.string.checked_in))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Streak Information
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StreakInfoItem(
                            icon = Icons.Default.LocalFireDepartment,
                            label = stringResource(R.string.current_streak),
                            value = "$currentStreak ${stringResource(R.string.days)}",
                            color = if (currentStreak > 0) Color(0xFFFF6B35) else Color.Gray
                        )
                        StreakInfoItem(
                            icon = Icons.Default.Star,
                            label = stringResource(R.string.longest_streak),
                            value = "$longestStreak ${stringResource(R.string.days)}",
                            color = PointsBlue
                        )
                        if (streakBonus > 0) {
                            StreakInfoItem(
                                icon = Icons.Default.Star,
                                label = stringResource(R.string.streak_bonus),
                                value = "+$streakBonus",
                                color = SunYellow
                            )
                        }
                    }
                }
            }
        }

@Composable
fun StreakInfoItem(
            icon: androidx.compose.ui.graphics.vector.ImageVector,
            label: String,
            value: String,
            color: Color,
            modifier: Modifier = Modifier
        ) {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    textAlign = TextAlign.Center
                )
            }
        }

@Composable
fun DailyChallengesCard(
            challenges: List<com.dex.base.baseandroidcompose.data.database.DailyChallengeEntity>,
            completedCount: Int,
            totalCount: Int,
            modifier: Modifier = Modifier
        ) {
            Card(
                modifier = modifier.fillMaxWidth(),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Task,
                                contentDescription = null,
                                tint = PointsBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.daily_challenges),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        Text(
                            text = "$completedCount/$totalCount",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (completedCount == totalCount && totalCount > 0) {
                                Color(0xFF4CAF50)
                            } else {
                                PointsBlue
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (challenges.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_challenges_today),
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        challenges.forEach { challenge ->
                            ChallengeItem(
                                challenge = challenge,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

@Composable
fun ChallengeItem(
            challenge: com.dex.base.baseandroidcompose.data.database.DailyChallengeEntity,
            modifier: Modifier = Modifier
        ) {
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (challenge.isCompleted) {
                        Color(0xFF4CAF50).copy(alpha = 0.1f)
                    } else {
                        Color.Gray.copy(alpha = 0.05f)
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = challenge.challengeTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Text(
                            text = challenge.challengeDescription,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${stringResource(R.string.progress)}: ${challenge.currentProgress}/${challenge.targetValue}",
                            fontSize = 12.sp,
                            color = PointsBlue
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        if (challenge.isCompleted) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = "+${challenge.pointsReward}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SunYellow
                            )
                        }
                    }
                }
            }
        }

@Preview(showBackground = true)
@Composable
fun RewardScreenPreview() {
    BaseAndroidComposeTheme {
        RewardScreen()
    }
}