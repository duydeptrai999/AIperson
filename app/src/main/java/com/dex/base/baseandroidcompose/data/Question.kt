package com.quicktest.mathquiz.data

data class Question(
    val id: Int,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String // A, B, C, or D
)

// Predefined math questions
val mathQuestions = listOf(
    Question(
        id = 1,
        questionText = "15 + 27 = ?",
        optionA = "40",
        optionB = "42",
        optionC = "44",
        optionD = "46",
        correctAnswer = "B"
    ),
    Question(
        id = 2,
        questionText = "8 × 7 = ?",
        optionA = "54",
        optionB = "56",
        optionC = "58",
        optionD = "60",
        correctAnswer = "B"
    ),
    Question(
        id = 3,
        questionText = "144 ÷ 12 = ?",
        optionA = "10",
        optionB = "11",
        optionC = "12",
        optionD = "13",
        correctAnswer = "C"
    ),
    Question(
        id = 4,
        questionText = "75 - 28 = ?",
        optionA = "45",
        optionB = "46",
        optionC = "47",
        optionD = "48",
        correctAnswer = "C"
    ),
    Question(
        id = 5,
        questionText = "9 × 6 + 4 = ?",
        optionA = "56",
        optionB = "58",
        optionC = "60",
        optionD = "62",
        correctAnswer = "B"
    )
)