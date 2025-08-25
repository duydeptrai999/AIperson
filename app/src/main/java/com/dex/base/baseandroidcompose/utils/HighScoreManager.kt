package com.dex.base.baseandroidcompose.utils

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PlayerScore(
    val name: String,
    val score: Int,
    val timestamp: Long
)

class HighScoreManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("high_scores", Context.MODE_PRIVATE)
    private val _topScores = MutableStateFlow<List<PlayerScore>>(emptyList())
    val topScores: Flow<List<PlayerScore>> = _topScores.asStateFlow()
    
    init {
        loadScores()
    }
    
    fun saveScore(playerName: String, score: Int) {
        val currentScores = _topScores.value.toMutableList()
        val newScore = PlayerScore(playerName, score, System.currentTimeMillis())
        
        // Add new score
        currentScores.add(newScore)
        
        // Sort by score (descending) then by timestamp (ascending for same scores)
        currentScores.sortWith(compareByDescending<PlayerScore> { it.score }.thenBy { it.timestamp })
        
        // Keep only top 10 scores
        val topScores = currentScores.take(10)
        
        // Save to SharedPreferences
        val editor = prefs.edit()
        editor.clear() // Clear existing scores
        
        topScores.forEachIndexed { index, playerScore ->
            editor.putString("name_$index", playerScore.name)
            editor.putInt("score_$index", playerScore.score)
            editor.putLong("timestamp_$index", playerScore.timestamp)
        }
        editor.putInt("count", topScores.size)
        editor.apply()
        
        // Update flow
        _topScores.value = topScores
    }
    
    private fun loadScores() {
        val count = prefs.getInt("count", 0)
        val scores = mutableListOf<PlayerScore>()
        
        for (i in 0 until count) {
            val name = prefs.getString("name_$i", "") ?: ""
            val score = prefs.getInt("score_$i", 0)
            val timestamp = prefs.getLong("timestamp_$i", 0L)
            
            if (name.isNotEmpty()) {
                scores.add(PlayerScore(name, score, timestamp))
            }
        }
        
        _topScores.value = scores
    }
    
    fun getTopScores(limit: Int = 5): List<PlayerScore> {
        return _topScores.value.take(limit)
    }
    
    fun isHighScore(score: Int): Boolean {
        val currentScores = _topScores.value
        return currentScores.size < 10 || score > (currentScores.lastOrNull()?.score ?: 0)
    }
}