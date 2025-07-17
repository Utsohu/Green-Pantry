package com.example.greenpantry.data.api

import android.graphics.Bitmap
import android.util.Log
import com.example.greenpantry.data.model.GeneratedRecipe
import com.example.greenpantry.data.model.NutritionInfo
import com.example.greenpantry.data.model.RecipeGenerationResponse
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI
import com.google.firebase.vertexai.type.Schema
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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
    
    private val gson = Gson()
    
    private val generativeModel by lazy {
        Firebase.vertexAI.generativeModel(
            modelName = config.model,
            generationConfig = generationConfig {
                temperature = config.temperature
                maxOutputTokens = config.maxOutputTokens
            }
        )
    }
    
    private val recipeGenerationModel by lazy {
        // Define the schema for recipe generation
        val recipeSchema = Schema.obj(
            mapOf(
                "recipes" to Schema.array(
                    Schema.obj(
                        mapOf(
                            "name" to Schema.string(),
                            "description" to Schema.string(),
                            "ingredients" to Schema.array(Schema.string()),
                            "instructions" to Schema.array(Schema.string()),
                            "time" to Schema.integer(),
                            "difficulty" to Schema.integer(),
                            "servings" to Schema.integer(),
                            "nutrition" to Schema.obj(
                                mapOf(
                                    "calories" to Schema.integer(),
                                    "protein" to Schema.integer(),
                                    "carbs" to Schema.integer(),
                                    "fat" to Schema.integer(),
                                    "fiber" to Schema.integer()
                                )
                            )
                        )
                    )
                )
            )
        )
        
        Firebase.vertexAI.generativeModel(
            modelName = config.model,
            generationConfig = generationConfig {
                temperature = 0.7f // Slightly higher for creativity
                maxOutputTokens = 8192 // Much higher limit for 3 detailed recipes
                responseMimeType = "application/json"
                responseSchema = recipeSchema
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
    
    suspend fun generateRecipes(pantryItems: List<String>): Result<List<GeneratedRecipe>> = withContext(Dispatchers.IO) {
        try {
            withTimeout(TIMEOUT_MS) {
                val itemsList = pantryItems.joinToString(", ")
                // Optimized prompt to reduce token usage while maintaining quality
                val prompt = """
                    Create 3 recipes using these pantry items: $itemsList
                    
                    Requirements:
                    - Use available ingredients as primary components
                    - Include common household staples if needed
                    - Keep instructions clear and concise
                    - Provide realistic cooking times and serving sizes
                """.trimIndent()
                
                val response = recipeGenerationModel.generateContent(
                    content {
                        text(prompt)
                    }
                )
                
                val responseText = response.text?.trim()
                
                if (responseText.isNullOrEmpty()) {
                    throw Exception("Empty response from API")
                }
                
                Log.d(TAG, "Recipe generation response: $responseText")
                
                // Parse the JSON response
                try {
                    val recipeResponse = gson.fromJson(responseText, RecipeGenerationResponse::class.java)
                    
                    // Validate that we have recipes
                    if (recipeResponse.recipes.isEmpty()) {
                        throw Exception("No recipes generated")
                    }
                    
                    if (recipeResponse.recipes.size != 3) {
                        Log.w(TAG, "Expected 3 recipes but got ${recipeResponse.recipes.size}")
                    }
                    
                    Result.success(recipeResponse.recipes)
                } catch (e: JsonSyntaxException) {
                    Log.e(TAG, "Failed to parse recipe JSON: ${e.message}")
                    Log.e(TAG, "Response was: $responseText")
                    Result.failure(Exception("Failed to parse recipe response: ${e.message}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Recipe generation failed: ${e.message}")
            Result.failure(e)
        }
    }
}