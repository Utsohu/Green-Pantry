package com.example.greenpantry.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    //insert a single recipe
    @Insert
    suspend fun insertRecipe(recipe: Recipe)

    //get all the recipes
    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipes(): List<Recipe>

    //insert a list of recipes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<Recipe>)

    // Get a single recipe by name (or ID if preferred)
    @Query("SELECT * FROM recipes WHERE name = :name LIMIT 1")
    suspend fun getRecipeByTitle(name: String): Recipe?

    // Update a recipe
    @Update
    suspend fun updateRecipe(recipe: Recipe)

    // Delete all recipes
    @Query("DELETE FROM recipes")
    suspend fun deleteAllRecipes()

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    // Count how many recipes are in the table
    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun getRecipeCount(): Int

    // Search for recipes by partial title match
    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%'")
    suspend fun searchRecipesByTitle(query: String): List<Recipe>

    //return a list of recipes using a particular ingredient
    @Query("SELECT * FROM recipes WHERE ingredients LIKE '%' || :ingredient || '%'")
    suspend fun getRecipesByIngredient(ingredient: String): List<Recipe>
}
