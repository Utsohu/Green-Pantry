package com.example.greenpantry.data.api

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiApiClient @Inject constructor(
    private val config: GeminiConfig
) {
    companion object {
        private const val TAG = "GeminiApiClient"
        private const val TIMEOUT_MS = 30_000L // 30 seconds timeout
    }
    
    private val generativeModel by lazy {
        Firebase.vertexAI.generativeModel(
            modelName = config.model,
            generationConfig = generationConfig {
                temperature = config.temperature
                maxOutputTokens = config.maxOutputTokens
            }
        )
    }
    
    suspend fun recognizeFood(bitmap: Bitmap): Result<String> = withContext(Dispatchers.IO) {
        try {
            withTimeout(TIMEOUT_MS) {
                val response = generativeModel.generateContent(
                    content {
                        image(bitmap)
                        text(config.foodRecognitionPrompt)
                    }
                )
                
                val responseText = response.text?.trim()
                
                if (responseText.isNullOrEmpty()) {
                    throw Exception("Empty response from API")
                }
                
                Result.success(responseText)
            }
        } catch (e: Exception) {
            Log.e(TAG, "API call failed: ${e.message}")
            Result.failure(e)
        }
    }
}