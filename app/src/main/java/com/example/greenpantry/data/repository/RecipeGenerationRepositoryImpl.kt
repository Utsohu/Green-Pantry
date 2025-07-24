package com.example.greenpantry.data.repository

import android.os.Bundle
import android.util.Log
import com.example.greenpantry.R
import com.example.greenpantry.data.api.GeminiApiClient
import com.example.greenpantry.data.database.PantryItemDao
import com.example.greenpantry.data.database.Recipe
import com.example.greenpantry.data.database.RecipeDao
import com.example.greenpantry.data.model.GeneratedRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeGenerationRepositoryImpl @Inject constructor(
    private val geminiApiClient: GeminiApiClient,
    private val pantryItemDao: PantryItemDao,
    private val recipeDao: RecipeDao
) : RecipeGenerationRepository {
    
    companion object {
        private const val TAG = "RecipeGenerationRepo"
        private const val MIN_PANTRY_ITEMS = 3
    }
    
    override suspend fun generateRecipesFromPantry(): Result<List<GeneratedRecipe>> = withContext(Dispatchers.IO) {
        try {
            // Get pantry items with non-zero quantity
            val pantryItems = pantryItemDao.getAllItemsWithNonZeroQuantity()
            
            if (pantryItems.size < MIN_PANTRY_ITEMS) {
                return@withContext Result.failure(
                    Exception("Need at least $MIN_PANTRY_ITEMS items in pantry to generate recipes")
                )
            }
            
            // Extract item names
            val itemNames = pantryItems.map { it.name }
            
            Log.d(TAG, "Generating recipes with ${itemNames.size} pantry items")
            
            // Call Gemini API
            geminiApiClient.generateRecipes(itemNames)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate recipes: ${e.message}")
            Result.failure(e)
        }
    }
    
    override suspend fun saveGeneratedRecipe(recipe: GeneratedRecipe): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Convert GeneratedRecipe to Recipe entity
            val recipeEntity = Recipe(
                name = recipe.name,
                description = recipe.description,
                imageResId = R.drawable.recipe_placeholder, // Default image
                time = recipe.time,
                difficulty = recipe.difficulty,
                NOS = recipe.servings,
                calories = recipe.nutrition.calories,
                fiber = recipe.nutrition.fiber,
                totalFat = recipe.nutrition.fat,
                sugars = 0, // Not provided by AI
                transFat = 0, // Not provided by AI
                protein = recipe.nutrition.protein,
                sodium = 0, // Not provided by AI
                iron = 0, // Not provided by AI
                calcium = 0, // Not provided by AI
                carbs = recipe.nutrition.carbs,
                setUpInstructions = recipe.instructions.toMutableList(),
                ingredients = recipe.ingredients.toMutableList()
            )
            
            // Insert into database if doesn't already exist
            val check = recipeDao.getRecipeByTitle(recipe.name)
            if (check == null) { // not in db
                recipeDao.insertRecipe(recipeEntity)
                Log.d(TAG, "Successfully saved recipe: ${recipe.name}")
                Result.success(Unit)
            } else { // in db
                Log.d(TAG, "Recipe already saved: ${recipe.name}")
                Result.failure(Exception("${recipe.name} already saved"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save recipe: ${e.message}")
            Result.failure(e)
        }
    }
}