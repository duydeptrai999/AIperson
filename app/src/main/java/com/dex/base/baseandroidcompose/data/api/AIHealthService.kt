package com.dex.base.baseandroidcompose.data.api

import com.dex.base.baseandroidcompose.data.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import android.content.Context
import android.content.SharedPreferences
import java.util.UUID
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import java.net.SocketTimeoutException
import java.io.IOException

/**
 * API service for AI Health Advice
 */
interface AIHealthService {

    @POST("v1/chat-messages")
    suspend fun getHealthAdvice(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: AIHealthAdviceRequest
    ): Response<AIHealthAdviceResponse>
}

/**
 * Repository for AI Health Advice
 */
@Singleton
class AIHealthRepository @Inject constructor(
    private val apiService: AIHealthService,
    @ApplicationContext private val context: Context
) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("ai_health_prefs", Context.MODE_PRIVATE)
    }

    companion object {
        private const val API_KEY = "app-Sk0XODho8WD6R1BRg4NGpPwD"
        private const val CHARACTER_ID = "character_01"
        private const val USER_ID_KEY = "user_id"

        private val HEALTH_ADVICE_TEMPLATE = """
        {
            "statusMessage": "Tình trạng sức khỏe tổng quan dựa trên thời tiết hiện tại",
            "assessmentLevel": "Mức độ đánh giá sức khỏe (Tốt/Trung bình/Cần chú ý)",
            "healthAnalysis": "Phân tích chi tiết về tác động của thời tiết hiện tại đến sức khỏe dựa trên tuổi tác và nghề nghiệp",
            "recommendations": [
                {
                    "title": "Hoạt động nên làm",
                    "content": "Các hoạt động phù hợp với thời tiết hiện tại với hướng dẫn cụ thể"
                },
                {
                    "title": "Hoạt động cần tránh",
                    "content": "Những việc không nên làm trong điều kiện thời tiết này với lý do rõ ràng"
                },
                {
                    "title": "Kế hoạch từ giờ đến cuối ngày",
                    "content": "Lịch trình hoạt động cụ thể cho thời gian còn lại trong ngày"
                }
            ],
            "nutritionalAdvice": "Lời khuyên dinh dưỡng chi tiết phù hợp với thời tiết và độ tuổi, bao gồm thực phẩm cụ thể và lượng nước cần uống",
            "workoutTips": "Bài tập thể dục cụ thể phù hợp với điều kiện thời tiết và nghề nghiệp, bao gồm thời gian và cường độ"
        }
        """.trimIndent()
    }

    suspend fun getHealthAdvice(
        weatherData: WeatherData,
        userProfile: UserProfile,
        conversationId: String? = null
    ): Result<AIHealthAdvice> {
        return retryWithExponentialBackoff(
            maxRetries = 3,
            initialDelayMs = 1000L
        ) {
            performHealthAdviceRequest(weatherData, userProfile, conversationId)
        }
    }

    private suspend fun performHealthAdviceRequest(
        weatherData: WeatherData,
        userProfile: UserProfile,
        conversationId: String? = null
    ): Result<AIHealthAdvice> {
        return try {
            // Prepare health data
            val healthData = HealthDataInput(
                weather = WeatherHealthData(
                    temperature = weatherData.temperature,
                    humidity = weatherData.humidity,
                    windSpeed = weatherData.windSpeed,
                    description = weatherData.description,
                    feelsLike = weatherData.feelsLike,
                    visibility = weatherData.visibility,
                    uvIndex = null, // Add if available
                    airQuality = null // Add if available
                ),
                user = UserHealthData(
                    age = userProfile.age,
                    occupation = userProfile.occupation.name,
                    activityLevel = "moderate", // Can be derived from user preferences
                    healthConditions = emptyList(), // Add if available in user profile
                    preferences = emptyList() // Add if available
                ),
                location = LocationHealthData(
                    city = userProfile.location.city,
                    country = userProfile.location.country,
                    timezone = userProfile.location.timezone
                )
            )

            // Create query
            val query = buildHealthQuery(weatherData, userProfile)

            // Get or generate user ID
            val userId = getUserId()

            // Prepare request
            val request = AIHealthAdviceRequest(
                query = query,
                user = userId,
                conversationId = conversationId,
                responseMode = "blocking",
                inputs = AIHealthInputs(
                    data = """{
                        "health_data":"${com.google.gson.Gson().toJson(healthData)}", 
                        "character_id":"$CHARACTER_ID"
                    }""",
                    template = HEALTH_ADVICE_TEMPLATE
                )
            )

            // Log request details
            Log.d("AIHealthService", "=== API REQUEST ===")
            Log.d("AIHealthService", "URL: https://ai.dreamapi.net/v1/chat-messages")
            Log.d("AIHealthService", "Authorization: Bearer $API_KEY")
            Log.d("AIHealthService", "Request Body: ${com.google.gson.Gson().toJson(request)}")
            Log.d("AIHealthService", "Query: $query")
            Log.d("AIHealthService", "User ID: $userId")
            Log.d("AIHealthService", "Character ID: $CHARACTER_ID")

            // Make API call
            val response = apiService.getHealthAdvice(
                authorization = "Bearer $API_KEY",
                request = request
            )

            // Log response details
            Log.d("AIHealthService", "=== API RESPONSE ===")
            Log.d("AIHealthService", "Response Code: ${response.code()}")
            Log.d("AIHealthService", "Response Message: ${response.message()}")
            Log.d("AIHealthService", "Response Headers: ${response.headers()}")

            if (response.isSuccessful) {
                val apiResponse = response.body()
                Log.d("AIHealthService", "Response Body: ${com.google.gson.Gson().toJson(apiResponse)}")

                if (apiResponse != null) {
                    Log.d("AIHealthService", "Parsing health advice from answer: ${apiResponse.answer}")
                    // Parse the JSON response from answer field
                    val healthAdvice = parseHealthAdviceFromAnswer(
                        apiResponse.answer,
                        apiResponse.conversationId
                    )
                    Log.d("AIHealthService", "Successfully parsed health advice: ${com.google.gson.Gson().toJson(healthAdvice)}")
                    Result.success(healthAdvice)
                } else {
                    Log.e("AIHealthService", "Empty response body from AI service")
                    Result.failure(Exception("Empty response from AI service"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AIHealthService", "API call failed with code ${response.code()}")
                Log.e("AIHealthService", "Error message: ${response.message()}")
                Log.e("AIHealthService", "Error body: $errorBody")
                
                // Xử lý cụ thể cho lỗi 522 (Connection Timed Out)
                val errorMessage = when (response.code()) {
                    522 -> "Server timeout - AI service đang quá tải, vui lòng thử lại sau"
                    503 -> "Service unavailable - AI service tạm thời không khả dụng"
                    429 -> "Too many requests - Vượt quá giới hạn request"
                    else -> "API call failed: ${response.code()} - ${response.message()}"
                }
                
                Result.failure(NetworkException(response.code(), errorMessage, errorBody))
            }

        } catch (e: SocketTimeoutException) {
            Log.e("AIHealthService", "Socket timeout occurred", e)
            Result.failure(NetworkException(408, "Request timeout - Kết nối quá chậm", e.message))
        } catch (e: IOException) {
            Log.e("AIHealthService", "Network IO exception occurred", e)
            Result.failure(NetworkException(0, "Network error - Lỗi kết nối mạng", e.message))
        } catch (e: Exception) {
            Log.e("AIHealthService", "Exception occurred during API call", e)
            Log.e("AIHealthService", "Exception message: ${e.message}")
            Log.e("AIHealthService", "Exception type: ${e.javaClass.simpleName}")
            Result.failure(e)
        }
    }

    /**
     * Retry mechanism với exponential backoff
     */
    private suspend fun <T> retryWithExponentialBackoff(
        maxRetries: Int,
        initialDelayMs: Long,
        operation: suspend () -> Result<T>
    ): Result<T> {
        var currentDelay = initialDelayMs
        var lastException: Exception? = null

        repeat(maxRetries) { attempt ->
            try {
                val result = operation()
                if (result.isSuccess) {
                    return result
                }
                
                // Nếu là lỗi không thể retry (như 401, 403), return ngay
                val exception = result.exceptionOrNull()
                if (exception is NetworkException) {
                    when (exception.code) {
                        401, 403, 404 -> return result // Không retry cho các lỗi này
                    }
                }
                
                lastException = exception as? Exception
                
            } catch (e: Exception) {
                lastException = e
                Log.w("AIHealthService", "Retry attempt ${attempt + 1} failed", e)
            }

            // Nếu không phải lần thử cuối, delay trước khi retry
            if (attempt < maxRetries - 1) {
                Log.d("AIHealthService", "Retrying in ${currentDelay}ms... (attempt ${attempt + 1}/$maxRetries)")
                delay(currentDelay)
                currentDelay = (currentDelay * 1.5).toLong() // Exponential backoff
            }
        }

        Log.e("AIHealthService", "All retry attempts failed")
        return Result.failure(lastException ?: Exception("All retry attempts failed"))
    }

    private fun buildHealthQuery(
        weatherData: WeatherData,
        userProfile: UserProfile
    ): String {
        return """
        Với thông tin thời tiết hiện tại:
        - Nhiệt độ: ${weatherData.temperature.toInt()}°C (cảm giác như ${weatherData.feelsLike.toInt()}°C)
        - Độ ẩm: ${weatherData.humidity}%
        - Tốc độ gió: ${weatherData.windSpeed} m/s
        - Tình trạng: ${weatherData.description}
        - Tầm nhìn: ${weatherData.visibility/1000} km
        - Địa điểm: ${userProfile.location.city}, ${userProfile.location.country}
        
        Và thông tin cá nhân:
        - Tuổi: ${userProfile.age}
        - Nghề nghiệp: ${userProfile.occupation.name}
        - Địa chỉ: ${userProfile.location.city}
        
        Hãy đưa ra lời khuyên sức khỏe chi tiết và cụ thể cho tôi trong điều kiện thời tiết này. 
        Bao gồm những hoạt động nên làm, cần tránh, chế độ dinh dưỡng phù hợp và bài tập thể dục phù hợp.
        Từ giờ đến cuối ngày nên làm gì?
        """.trimIndent()
    }

    private fun parseHealthAdviceFromAnswer(
        answer: String,
        conversationId: String
    ): AIHealthAdvice {
        return try {
            // Try to extract JSON from answer (it might be wrapped in ```json blocks)
            val jsonStart = answer.indexOf("{")
            val jsonEnd = answer.lastIndexOf("}")

            if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                val jsonString = answer.substring(jsonStart, jsonEnd + 1)
                val gson = com.google.gson.Gson()
                val healthAdvice = gson.fromJson(jsonString, AIHealthAdvice::class.java)

                // Calculate assessment score based on assessment level if not provided


                healthAdvice.copy(
                    conversationId = conversationId,
                )
            } else {
                // Fallback: create health advice from plain text
                createFallbackHealthAdvice(answer, conversationId)
            }
        } catch (e: Exception) {
            Log.e("AIHealthService", "Error parsing health advice: ${e.message}")
            // Fallback: create health advice from plain text
            createFallbackHealthAdvice(answer, conversationId)
        }
    }

    private fun createFallbackHealthAdvice(
        answer: String,
        conversationId: String
    ): AIHealthAdvice {
        return AIHealthAdvice(
            statusMessage = "Lời khuyên sức khỏe từ AI",
            assessmentLevel = "Tốt",
            assessmentScore = 8,
            healthAnalysis = answer.take(200) + if (answer.length > 200) "..." else "",
            recommendations = listOf(
                HealthRecommendation(
                    title = "Lời khuyên chung",
                    content = answer
                )
            ),
            nutritionalAdvice = "Uống đủ nước và ăn nhiều rau xanh",
            workoutTips = "Tập thể dục nhẹ nhàng phù hợp với thời tiết",
            conversationId = conversationId
        )
    }

    /**
     * Get existing user ID or generate a new one
     * First time will generate random ID
     * Subsequent calls will use the saved ID
     */
    private fun getUserId(): String {
        val existingUserId = sharedPreferences.getString(USER_ID_KEY, null)

        return if (existingUserId == null) {
            // First time - generate new random user ID
            val newUserId = "user-${UUID.randomUUID()}"
            sharedPreferences.edit().putString(USER_ID_KEY, newUserId).apply()
            Log.d("AIHealthService", "Generated new user ID: $newUserId")
            newUserId
        } else {
            // Use existing user ID
            Log.d("AIHealthService", "Using existing user ID: $existingUserId")
            existingUserId
        }
    }
}

/**
 * Custom exception cho network errors
 */
class NetworkException(
    val code: Int,
    override val message: String,
    val errorBody: String?
) : Exception(message)