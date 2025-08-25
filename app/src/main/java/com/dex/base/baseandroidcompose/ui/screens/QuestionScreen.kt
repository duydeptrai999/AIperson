package com.dex.base.baseandroidcompose.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dex.base.baseandroidcompose.ui.theme.CorrectAnswer
import com.dex.base.baseandroidcompose.ui.theme.QuickTestTheme
import com.dex.base.baseandroidcompose.ui.theme.SelectedAnswer
import com.dex.base.baseandroidcompose.ui.theme.WrongAnswer
import com.quicktest.mathquiz.data.mathQuestions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(
    playerName: String,
    onGameComplete: (Int) -> Unit
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf(false) }
    var isAnswerCorrect by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val currentQuestion = mathQuestions[currentQuestionIndex]
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${currentQuestionIndex + 1}/5",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Score: $score",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        LinearProgressIndicator(
            progress = (currentQuestionIndex + 1) / 5f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )
        
        // Question Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentQuestion.questionText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
        
        // Answer Options
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnswerButton(
                text = "A. ${currentQuestion.optionA}",
                option = "A",
                selectedAnswer = selectedAnswer,
                correctAnswer = currentQuestion.correctAnswer,
                showResult = showResult,
                onClick = {
                    selectedAnswer = "A"
                    showConfirmDialog = true
                }
            )
            
            AnswerButton(
                text = "B. ${currentQuestion.optionB}",
                option = "B",
                selectedAnswer = selectedAnswer,
                correctAnswer = currentQuestion.correctAnswer,
                showResult = showResult,
                onClick = {
                    selectedAnswer = "B"
                    showConfirmDialog = true
                }
            )
            
            AnswerButton(
                text = "C. ${currentQuestion.optionC}",
                option = "C",
                selectedAnswer = selectedAnswer,
                correctAnswer = currentQuestion.correctAnswer,
                showResult = showResult,
                onClick = {
                    selectedAnswer = "C"
                    showConfirmDialog = true
                }
            )
            
            AnswerButton(
                text = "D. ${currentQuestion.optionD}",
                option = "D",
                selectedAnswer = selectedAnswer,
                correctAnswer = currentQuestion.correctAnswer,
                showResult = showResult,
                onClick = {
                    selectedAnswer = "D"
                    showConfirmDialog = true
                }
            )
        }
    }
    
    // Confirmation Dialog
    if (showConfirmDialog) {
        Dialog(onDismissRequest = { showConfirmDialog = false }) {
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
                        text = "Confirm Answer",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = "You selected: $selectedAnswer",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(
                            onClick = {
                                showConfirmDialog = false
                                selectedAnswer = ""
                            }
                        ) {
                            Text("Cancel")
                        }
                        
                        Button(
                            onClick = {
                                showConfirmDialog = false
                                isAnswerCorrect = selectedAnswer == currentQuestion.correctAnswer
                                if (isAnswerCorrect) {
                                    score++
                                }
                                showResult = true
                                
                                // Auto proceed after showing result - faster response
                                scope.launch {
//                                    delay(800)  // Reduced from 1500ms to 800ms
                                    showResult = false
                                    selectedAnswer = ""
                                    
                                    if (currentQuestionIndex < mathQuestions.size - 1) {
                                        currentQuestionIndex++
                                    } else {
                                        onGameComplete(score)
                                    }
                                }
                            }
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerButton(
    text: String,
    option: String,
    selectedAnswer: String,
    correctAnswer: String,
    showResult: Boolean,
    onClick: () -> Unit
) {
    val buttonColor = when {
        showResult && option == correctAnswer -> CorrectAnswer
        showResult && option == selectedAnswer && option != correctAnswer -> WrongAnswer
        option == selectedAnswer && !showResult -> SelectedAnswer
        else -> MaterialTheme.colorScheme.surface
    }
    
    val textColor = when {
        showResult && option == correctAnswer -> Color.White
        showResult && option == selectedAnswer && option != correctAnswer -> Color.White
        option == selectedAnswer && !showResult -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Button(
        onClick = { if (!showResult) onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = !showResult
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuestionScreenPreview() {
    QuickTestTheme {
        QuestionScreen(
            playerName = "Preview Player",
            onGameComplete = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnswerButtonPreview() {
    QuickTestTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnswerButton(
                text = "A. Normal Answer",
                option = "A",
                selectedAnswer = "",
                correctAnswer = "B",
                showResult = false,
                onClick = { }
            )
            
            AnswerButton(
                text = "B. Selected Answer",
                option = "B",
                selectedAnswer = "B",
                correctAnswer = "B",
                showResult = false,
                onClick = { }
            )
            
            AnswerButton(
                text = "C. Correct Answer",
                option = "C",
                selectedAnswer = "B",
                correctAnswer = "C",
                showResult = true,
                onClick = { }
            )
            
            AnswerButton(
                text = "D. Wrong Answer",
                option = "D",
                selectedAnswer = "D",
                correctAnswer = "C",
                showResult = true,
                onClick = { }
            )
        }
    }
}