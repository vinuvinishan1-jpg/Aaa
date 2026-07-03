package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent?
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val apiService: GeminiApiService by lazy {
        retrofit.create(GeminiApiService::class.java)
    }

    /**
     * Generates text content using the Gemini 3.5 Flash model.
     * Returns the raw text or a simulated response with guidance if the API key is not configured.
     */
    suspend fun generateText(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Friendly fallback mock when API Key is not configured yet
            return@withContext simulateAiResponse(prompt)
        }

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = prompt))
                )
            )
        )

        try {
            val response = apiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "AI could not generate a response. Please try a different prompt."
        } catch (e: Exception) {
            // Fallback with information
            "Error calling Gemini API: ${e.localizedMessage ?: e.message}\n\n[Simulating response for demo purposes]\n\n" + simulateAiResponse(prompt)
        }
    }

    private fun simulateAiResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("caption") -> {
                "✨ VIBE CHECK: Stepping into the digital future with VYBE X. Where connections spark, creations go wild, and every moment is capitalized. ⚡🚀 #VybeCheck #NextGen #ConnectCreateEarn"
            }
            lower.contains("hashtag") -> {
                "#VybeX #SocialRevolution #GenZAlpha #ConnectCreateEarn #TechFuture #CyberPunkAesthetic #LiveTheVybe"
            }
            lower.contains("idea") || lower.contains("content") -> {
                "💡 VYBE X CONTENT STRATEGY:\n\n" +
                "1. **Glassmorphism Tour**: Post a story showing off your custom futuristic UI dashboard and active stream settings.\n" +
                "2. **Disappearing Live Q&A**: Host a multi-host challenge. Reward attendees with custom digital gifts.\n" +
                "3. **Exclusive Private Circle Sneak Peek**: Share behind-the-scenes designs of your creator store merch."
            }
            else -> {
                "🚀 VYBE X AI Studio response for '$prompt':\n\nOptimizing your social outreach... Connect with your circles, stream live challenges, and cash out digital gifts today!"
            }
        }
    }
}
