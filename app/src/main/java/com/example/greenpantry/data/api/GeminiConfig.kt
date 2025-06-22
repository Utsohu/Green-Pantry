package com.example.greenpantry.data.api

import android.util.Log
import com.example.greenpantry.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiConfig @Inject constructor() {
    val apiKey: String = BuildConfig.GEMINI_API_KEY
    
    init {
        Log.d("GeminiConfig", "API Key configured: ${if (apiKey.isNotEmpty()) "Yes (length: ${apiKey.length})" else "No"}")
        Log.d("GeminiConfig", "API Key value: ${apiKey.take(10)}...") // Show first 10 chars for debugging
        
        if (apiKey.isEmpty() || apiKey == "null" || apiKey == "\"\"") {
            Log.e("GeminiConfig", "WARNING: Gemini API key is not properly configured!")
            Log.e("GeminiConfig", "Please add GEMINI_API_KEY=your_actual_key to local.properties")
        }
    }
    val model: String = "gemini-1.5-flash"
    val maxOutputTokens: Int = 2048
    val temperature: Float = 0.4f
    
    val foodRecognitionPrompt = """
        Identify the food item in this image. Please provide:
        1. The specific name of the food item
        2. Food category (fruit, vegetable, dairy, grain, protein, etc.)
        3. Whether it's packaged or fresh
        4. If packaged, any visible brand name
        5. Approximate quantity or size if apparent
        
        Return the information in the following JSON format:
        {
            "itemName": "string",
            "category": "string",
            "isPackaged": boolean,
            "brand": "string or null",
            "quantity": "string or null",
            "confidence": float (0.0 to 1.0)
        }
    """.trimIndent()
}