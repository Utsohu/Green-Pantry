package com.example.greenpantry.data.api

import android.graphics.Bitmap
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiApiClient @Inject constructor(
    private val config: GeminiConfig
) {
    companion object {
        private const val TAG = "GeminiApiClient"
    }
    private val generativeModel by lazy {
        GenerativeModel(
            modelName = config.model,
            apiKey = config.apiKey
        )
    }
    
    suspend fun recognizeFoodItem(bitmap: Bitmap): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting Gemini API call for food recognition")
            Log.d(TAG, "Bitmap size: ${bitmap.width}x${bitmap.height}")
            
            val inputContent = content {
                image(bitmap)
                text(config.foodRecognitionPrompt)
            }
            
            Log.d(TAG, "Calling Gemini generateContent...")
            val response = generativeModel.generateContent(inputContent)
            
            response.text?.let { responseText ->
                Log.d(TAG, "Gemini API response received: ${responseText.take(100)}...")
                Result.success(responseText)
            } ?: run {
                Log.e(TAG, "No text in Gemini API response")
                Result.failure(Exception("No response from Gemini API"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API call failed", e)
            Result.failure(e)
        }
    }
}