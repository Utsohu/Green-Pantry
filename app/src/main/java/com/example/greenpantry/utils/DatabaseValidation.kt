package com.example.greenpantry.utils

import android.content.Context
import android.util.Log
import com.example.greenpantry.data.database.DatabaseInitializer
import com.example.greenpantry.data.database.FoodItemDao
import com.example.greenpantry.data.database.RecipeDao
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

/**
 * Simple database performance testing utility
 */
object DatabaseValidation {
    private const val TAG = "DatabaseTest"
    
    // 🔧 ENABLE/DISABLE TESTING: Set to false to disable all database testing
    private const val TESTING_ENABLED = true
    
    /**
     * Quick performance test - logs current status and timing
     */
    fun quickTest(
        context: Context,
        foodItemDao: FoodItemDao,
        recipeDao: RecipeDao
    ) {
        if (!TESTING_ENABLED) return // ← Easy way to disable testing
        
        runBlocking {
            try {
                val itemCount = foodItemDao.getItemCount()
                val recipeCount = recipeDao.getRecipeCount()
                
                Log.d(TAG, "📊 Database Status: $itemCount items, $recipeCount recipes")
                
                if (itemCount == 0 || recipeCount == 0) {
                    Log.d(TAG, "🔄 Running performance test...")
                    runFullTest(context, foodItemDao, recipeDao)
                } else {
                    Log.d(TAG, "✅ Database already loaded")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Test failed: ${e.message}")
            }
        }
    }
    
    /**
     * Full performance test - measures actual loading time
     */
    private suspend fun runFullTest(
        context: Context,
        foodItemDao: FoodItemDao,
        recipeDao: RecipeDao
    ) {
        val loadTime = measureTimeMillis {
            val initializer = DatabaseInitializer()
            initializer.initializeDatabases(context, foodItemDao, recipeDao)
        }
        
        val itemCount = foodItemDao.getItemCount()
        val recipeCount = recipeDao.getRecipeCount()
        
        Log.d(TAG, "")
        Log.d(TAG, "🚀 PERFORMANCE RESULTS:")
        Log.d(TAG, "⏱️  Load Time: ${loadTime / 1000.0}s")
        Log.d(TAG, "📦 Items: $itemCount")
        Log.d(TAG, "🍳 Recipes: $recipeCount")
        
        when {
            loadTime < 30000 -> Log.d(TAG, "✅ EXCELLENT: Under 30 seconds!")
            loadTime < 60000 -> Log.d(TAG, "✅ GOOD: Under 1 minute")
            else -> Log.d(TAG, "⚠️  SLOW: Over 1 minute")
        }
        Log.d(TAG, "")
    }
}
