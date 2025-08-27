package com.dex.base.baseandroidcompose.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data model for AI Health Advice response
 */
data class AIHealthAdviceResponse(
    @SerializedName("event")
    val event: String,
    @SerializedName("task_id")
    val taskId: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("message_id")
    val messageId: String,
    @SerializedName("conversation_id")
    val conversationId: String,
    @SerializedName("mode")
    val mode: String,
    @SerializedName("answer")
    val answer: String,
    @SerializedName("metadata")
    val metadata: AIResponseMetadata?,
    @SerializedName("created_at")
    val createdAt: Long
)

data class AIResponseMetadata(
    @SerializedName("usage")
    val usage: AIUsage
)

data class AIUsage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int,
    @SerializedName("completion_tokens")
    val completionTokens: Int,
    @SerializedName("total_tokens")
    val totalTokens: Int,
    @SerializedName("total_price")
    val totalPrice: String,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("latency")
    val latency: Double
)

/**
 * Parsed health advice data from AI response
 */
data class AIHealthAdvice(
    val statusMessage: String,
    val assessmentLevel: String,
    val assessmentScore: Int,
    val healthAnalysis: String,
    val recommendations: List<HealthRecommendation>,
    val nutritionalAdvice: String,
    val workoutTips: String,
    val conversationId: String? = null
)

data class HealthRecommendation(
    val title: String,
    val content: String
)

/**
 * Request data for AI health advice
 */
data class AIHealthAdviceRequest(
    @SerializedName("query")
    val query: String,
    @SerializedName("user")
    val user: String,
    @SerializedName("conversation_id")
    val conversationId: String? = null,
    @SerializedName("response_mode")
    val responseMode: String = "blocking",
    @SerializedName("inputs")
    val inputs: AIHealthInputs
)

data class AIHealthInputs(
    @SerializedName("data")
    val data: String, // JSON string containing health_data and character_id
    @SerializedName("template")
    val template: String // JSON string defining output structure
)

/**
 * Health data to send to AI
 */
data class HealthDataInput(
    val weather: WeatherHealthData,
    val user: UserHealthData,
    val location: LocationHealthData
)

data class WeatherHealthData(
    val temperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val description: String,
    val feelsLike: Double,
    val visibility: Int,
    val uvIndex: Double? = null,
    val airQuality: String? = null
)

data class UserHealthData(
    val age: Int,
    val occupation: String,
    val activityLevel: String = "moderate",
    val healthConditions: List<String> = emptyList(),
    val preferences: List<String> = emptyList()
)

data class LocationHealthData(
    val city: String,
    val country: String,
    val timezone: String
)