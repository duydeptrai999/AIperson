package com.dex.base.baseandroidcompose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.dex.base.baseandroidcompose.ads.BannerAdSize
import com.dex.base.baseandroidcompose.ads.BannerAdView
import com.dex.base.baseandroidcompose.utils.HighScoreManager
import com.dex.base.baseandroidcompose.utils.PlayerScore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStartGame: (String) -> Unit
) {
    val context = LocalContext.current
    val highScoreManager = remember { HighScoreManager(context) }
    
    var showNameDialog by remember { mutableStateOf(false) }
    var playerName by remember { mutableStateOf("") }
    val topPlayers by highScoreManager.topScores.collectAsState(initial = emptyList())
    
    Column(
        modifier = Modifier
            .fillMaxSize()
//            .padding(16.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "QuickTest",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 32.dp, bottom = 32.dp)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Start Game Button
        Button(
            onClick = { showNameDialog = true },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Start Game",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // High Score Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "🏆 Top 5 High Scores",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                if (topPlayers.isEmpty()) {
                    Text(
                        text = "No scores yet. Be the first to play!",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    LazyColumn {
                        items(topPlayers.take(5)) { player ->
                            HighScoreItem(
                                rank = topPlayers.indexOf(player) + 1,
                                playerScore = player
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

//        BannerAdViewOld(
//            fullWidth = false
//        )
//
//        BannerAdViewOld(
//            fullWidth = true
//        )

        BannerAdView(BannerAdSize.EXPAND_TOP)
        BannerAdView(BannerAdSize.DEFAULT)
        BannerAdView(BannerAdSize.FULL_WIDTH)
        BannerAdView(BannerAdSize.EXPAND_BOTTOM)
    }
    
    // Name Input Dialog
    if (showNameDialog) {
        Dialog(onDismissRequest = { showNameDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Enter Your Name",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    OutlinedTextField(
                        value = playerName,
                        onValueChange = { playerName = it },
                        label = { Text("Player Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(
                            onClick = {
                                showNameDialog = false
                                playerName = ""
                            }
                        ) {
                            Text("Cancel")
                        }
                        
                        Button(
                            onClick = {
                                if (playerName.isNotBlank()) {
                                    showNameDialog = false
                                    onStartGame(playerName.trim())
                                    playerName = ""
                                }
                            },
                            enabled = playerName.isNotBlank()
                        ) {
                            Text("Continue")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HighScoreItem(
    rank: Int,
    playerScore: PlayerScore
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$rank",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = when (rank) {
                    1 -> Color(0xFFFFD700) // Gold
                    2 -> Color(0xFFC0C0C0) // Silver
                    3 -> Color(0xFFCD7F32) // Bronze
                    else -> MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.width(32.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = playerScore.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Text(
            text = "${playerScore.score}/5",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
//    QuickTestTheme {
        HomeScreen(
            onStartGame = { }
        )
//    }
}