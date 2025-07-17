package com.example.greenpantry.data.repository

import com.example.greenpantry.data.model.GeneratedRecipe

interface RecipeGenerationRepository {
    suspend fun generateRecipesFromPantry(): Result<List<GeneratedRecipe>>
    suspend fun saveGeneratedRecipe(recipe: GeneratedRecipe): Result<Unit>
}