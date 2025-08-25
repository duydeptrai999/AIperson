package com.quicktest.mathquiz.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dex.base.baseandroidcompose.ui.theme.QuickTestTheme
import com.dex.base.baseandroidcompose.utils.HighScoreManager
import com.dex.base.baseandroidcompose.utils.PlayerScore

@Composable
fun ResultScreen(
    playerName: String,
    score: Int,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val highScoreManager = remember { HighScoreManager(context) }
    var showLeaderboard by remember { mutableStateOf(false) }
    
    // Save player score when screen loads
    LaunchedEffect(Unit) {
        highScoreManager.saveScore(playerName, score)
    }
    
    val players by highScoreManager.topScores.collectAsState(initial = emptyList())
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Congratulations Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Trophy",
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFFFFD700) // Gold color
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Congratulations!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = playerName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Score",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "Your Score: $score/5",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val percentage = (score * 100) / 5
                Text(
                    text = "($percentage%)",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
        
        // Performance Message
        val performanceMessage = when (score) {
            5 -> "Perfect! You're a math genius! 🎉"
            4 -> "Excellent work! Almost perfect! 👏"
            3 -> "Good job! Keep practicing! 👍"
            2 -> "Not bad! You can do better! 💪"
            1 -> "Keep trying! Practice makes perfect! 📚"
            else -> "Don't give up! Try again! 🌟"
        }
        
        Text(
            text = performanceMessage,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    showLeaderboard = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Leaderboard",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "View Leaderboard",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Button(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Finish",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
    
    // Leaderboard Dialog
    if (showLeaderboard) {
        Dialog(onDismissRequest = { showLeaderboard = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Leaderboard",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        TextButton(
                            onClick = { showLeaderboard = false }
                        ) {
                            Text("Close")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (players.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No scores yet!",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    } else {
                        LazyColumn {
                            items(players.take(10)) { player ->
                                LeaderboardItem(
                                    playerScore = player,
                                    rank = players.indexOf(player) + 1,
                                    isCurrentPlayer = player.name == playerName && 
                                                    player.score == score &&
                                                    System.currentTimeMillis() - player.timestamp < 5000 // Recent score
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardItem(
    playerScore: PlayerScore,
    rank: Int,
    isCurrentPlayer: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentPlayer) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentPlayer) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank
                Text(
                    text = "#$rank",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (rank) {
                         1 -> androidx.compose.ui.graphics.Color(0xFFFFD700) // Gold
                         2 -> androidx.compose.ui.graphics.Color(0xFFC0C0C0) // Silver
                         3 -> androidx.compose.ui.graphics.Color(0xFFCD7F32) // Bronze
                         else -> MaterialTheme.colorScheme.onSurface
                     },
                    modifier = Modifier.width(40.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Player name
                Text(
                    text = playerScore.name,
                    fontSize = 16.sp,
                    fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Medium,
                    color = if (isCurrentPlayer) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Score
            Text(
                text = "${playerScore.score}/5",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCurrentPlayer) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    QuickTestTheme {
        ResultScreen(
            playerName = "Preview Player",
            score = 4,
            onFinish = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenLowScorePreview() {
    QuickTestTheme {
        ResultScreen(
            playerName = "Test Player",
            score = 1,
            onFinish = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenPerfectScorePreview() {
    QuickTestTheme {
        ResultScreen(
            playerName = "Perfect Player",
            score = 5,
            onFinish = { }
        )
    }
}