package com.example.greenpantry.data.api

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiConfig @Inject constructor() {
    val model: String = "gemini-2.5-flash"  // Firebase AI Logic model
    val maxOutputTokens: Int = 2048
    val temperature: Float = 0.4f
    
    init {
        Log.d("GeminiConfig", "Model: $model")
    }
    
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
    
    // Recipe generation prompt is now simplified since we use structured generation
    val recipeGenerationPrompt = """
        Based on the following pantry items, suggest exactly 3 unique and creative recipes.
        Available items: {pantryItems}
        
        Make sure recipes are practical, use common cooking techniques, and prioritize using the available pantry items.
        Include complete details for each recipe including ingredients with quantities and step-by-step instructions.
    """.trimIndent()
}