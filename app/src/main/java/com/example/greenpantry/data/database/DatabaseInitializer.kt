package com.example.greenpantry.data.database

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureTimeMillis

@Singleton
class DatabaseInitializer @Inject constructor() {
    
    private val _loadingProgress = MutableStateFlow(0f)
    val loadingProgress: StateFlow<Float> = _loadingProgress.asStateFlow()
    
    private val _loadingMessage = MutableStateFlow("Initializing...")
    val loadingMessage: StateFlow<String> = _loadingMessage.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    companion object {
        private const val TAG = "DatabaseInitializer"
        private const val BATCH_SIZE = 1000 // Process data in batches
    }
    
    /**
     * Initialize all databases concurrently with progress tracking
     */
    suspend fun initializeDatabases(
        context: Context,
        foodItemDao: FoodItemDao,
        recipeDao: RecipeDao
    ) = withContext(Dispatchers.IO) {
        if (_isLoading.value) return@withContext
        
        _isLoading.value = true
        _loadingProgress.value = 0f
        
        Log.d(TAG, "🚀 Starting database initialization")
        
        try {
            val totalTime = kotlin.system.measureTimeMillis {
                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val isDataLoaded = prefs.getBoolean("data_loaded", false)
                val isRecipesLoaded = recipeDao.getRecipeCount() > 0
                
                if (isDataLoaded && isRecipesLoaded) {
                    Log.d(TAG, "All databases already initialized")
                    _loadingProgress.value = 1f
                    _loadingMessage.value = "Complete"
                    return@measureTimeMillis
                }
                
                // Create parallel jobs for different operations
                val jobs = mutableListOf<Deferred<Unit>>()
                
                // Job 1: Load food items if needed
                if (!isDataLoaded) {
                    jobs.add(async {
                        Log.d(TAG, "Starting food items loading...")
                        _loadingMessage.value = "Loading food database..."
                        val foodLoadTime = measureTimeMillis {
                            loadFoodItemsOptimized(context, foodItemDao) { progress ->
                                _loadingProgress.value = progress * 0.5f // 0% to 50%
                            }
                        }
                        Log.d(TAG, "Food items loaded in ${foodLoadTime}ms (${foodLoadTime / 1000.0}s)")
                        Unit // Explicitly return Unit
                    })
                }
                
                // Job 2: Load recipes if needed (depends on food items)
                if (!isRecipesLoaded) {
                    jobs.add(async {
                        // Wait for food items to be loaded if they're being loaded
                        if (!isDataLoaded) {
                            // Wait until food items are loaded
                            while (!prefs.getBoolean("data_loaded", false)) {
                                delay(100)
                            }
                        }
                        
                        Log.d(TAG, "Starting recipes loading...")
                        _loadingMessage.value = "Loading recipes..."
                        val recipeLoadTime = measureTimeMillis {
                            loadRecipesOptimized(context, foodItemDao, recipeDao) { progress ->
                                _loadingProgress.value = 0.5f + (progress * 0.5f) // 50% to 100%
                            }
                        }
                        Log.d(TAG, "Recipes loaded in ${recipeLoadTime}ms (${recipeLoadTime / 1000.0}s)")
                        Unit // Explicitly return Unit
                    })
                }
                
                // Wait for all jobs to complete
                jobs.awaitAll()
            }
            
            // Log final performance metrics
            val itemCount = foodItemDao.getItemCount()
            val recipeCount = recipeDao.getRecipeCount()
            
            _loadingMessage.value = "Complete"
            Log.d(TAG, "✅ Database initialization completed in ${totalTime}ms")
            Log.d(TAG, "📦 Loaded $itemCount food items and $recipeCount recipes")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing databases", e)
            _loadingMessage.value = "Error loading data"
            throw e
        } finally {
            Log.d(TAG, "🏁 Database initialization finished")
            _isLoading.value = false
        }
    }
    
    /**
     * Optimized food items loading with batching and streaming
     */
    private suspend fun loadFoodItemsOptimized(
        context: Context,
        dao: FoodItemDao,
        onProgress: (Float) -> Unit = {}
    ) = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("data_loaded", false)) return@withContext
        
        Log.d(TAG, "Starting optimized food items loading")
        
        // Pre-load nutrient data into memory map for faster lookups
        val nutrientMap = async { loadNutrientMapOptimized(context) }
        
        // Stream process food names with batching
        val foodItems = mutableListOf<FoodItem>()
        var processedCount = 0
        val totalEstimated = 1742 // Known size of FOOD_NAME.csv
        
        context.assets.open("FOOD_NAME.csv").bufferedReader().use { reader ->
            reader.readLine() // Skip header
            
            reader.lineSequence().chunked(BATCH_SIZE).forEach { batch ->
                val batchItems = batch.mapNotNull { line ->
                    processFoodItemLine(line, nutrientMap.await())
                }
                
                foodItems.addAll(batchItems)
                processedCount += batch.size
                
                // Update progress
                val progress = (processedCount.toFloat() / totalEstimated).coerceAtMost(1f)
                onProgress(progress)
                
                // Insert in batches to avoid memory pressure
                if (foodItems.size >= BATCH_SIZE) {
                    dao.insertAll(foodItems.take(BATCH_SIZE))
                    foodItems.removeAll(foodItems.take(BATCH_SIZE))
                    // Only log every few batches to reduce spam
                    if (processedCount % (BATCH_SIZE * 5) == 0) {
                        Log.d(TAG, "Processed $processedCount/$totalEstimated food items (${(progress*100).toInt()}%)")
                    }
                }
            }
            
            // Insert remaining items
            if (foodItems.isNotEmpty()) {
                dao.insertAll(foodItems)
            }
        }
        
        prefs.edit().putBoolean("data_loaded", true).apply()
        Log.d(TAG, "Food items loading completed: ${dao.getItemCount()} items")
    }
    
    /**
     * Optimized nutrient map loading with streaming
     */
    private suspend fun loadNutrientMapOptimized(context: Context): Map<Int, Map<Int, Int>> = 
        withContext(Dispatchers.IO) {
            val nutrientMap = mutableMapOf<Int, MutableMap<Int, Int>>()
            val targetNutrients = setOf(208, 291, 204, 269, 605, 203, 307, 303, 301, 205)
            
            context.assets.open("NUTRIENT_AMOUNT.csv").bufferedReader().use { reader ->
                reader.readLine() // Skip header
                
                reader.lineSequence().forEach { line ->
                    val parts = line.split(",")
                    if (parts.size >= 3) {
                        val foodId = parts[0].toIntOrNull()
                        val nutrientId = parts[1].toIntOrNull()
                        val value = parts[2].toDoubleOrNull()
                        
                        if (foodId != null && nutrientId != null && value != null && 
                            nutrientId in targetNutrients) {
                            nutrientMap.getOrPut(foodId) { mutableMapOf() }[nutrientId] = value.toInt()
                        }
                    }
                }
            }
            
            Log.d(TAG, "Nutrient map loaded: ${nutrientMap.size} food items")
            nutrientMap
        }
    
    /**
     * Process individual food item line
     */
    private fun processFoodItemLine(line: String, nutrientMap: Map<Int, Map<Int, Int>>): FoodItem? {
        return try {
            val parts = line.split(",")
            if (parts.size < 5) return null
            
            val foodId = parts[0].toIntOrNull() ?: return null
            val foodGroup = parts[2].toIntOrNull() ?: return null
            val foodName = parts[4].trim()
            val foodImage = parts.lastOrNull()?.trim() ?: ""
            
            val category = mapFoodGroupToCategory(foodGroup) ?: return null
            val nutrients = nutrientMap[foodId] ?: emptyMap()
            
            FoodItem(
                foodId = foodId,
                name = foodName,
                imageURL = foodImage,
                category = category,
                servingSize = "100g",
                calories = nutrients[208] ?: 0,
                fiber = nutrients[291] ?: 0,
                totFat = nutrients[204] ?: 0,
                sugars = nutrients[269] ?: 0,
                transFat = nutrients[605] ?: 0,
                protein = nutrients[203] ?: 0,
                sodium = nutrients[307] ?: 0,
                iron = nutrients[303] ?: 0,
                calcium = nutrients[301] ?: 0,
                carbs = nutrients[205] ?: 0
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to process food item line: $line", e)
            null
        }
    }
    
    /**
     * Optimized recipe loading with parallel processing
     */
    private suspend fun loadRecipesOptimized(
        context: Context,
        itemDao: FoodItemDao,
        recipeDao: RecipeDao,
        onProgress: (Float) -> Unit = {}
    ) = withContext(Dispatchers.IO) {
        if (recipeDao.getRecipeCount() > 0) return@withContext
        
        Log.d(TAG, "Starting optimized recipe loading")
        
        // Pre-load food items map for faster lookups
        val foodItemsMap = async {
            itemDao.getAllFoodItems().associateBy { it.name.lowercase() }
        }
        
        val recipes = mutableListOf<Recipe>()
        var processedCount = 0
        val totalEstimated = 58783 // Known size of RECIPE_SETUP_DATA.csv
        
        context.assets.open("RECIPE_SETUP_DATA.csv").bufferedReader().use { reader ->
            reader.readLine() // Skip header
            
            reader.lineSequence().chunked(BATCH_SIZE / 10).forEach { batch ->
                val batchRecipes = batch.mapNotNull { line ->
                    processRecipeLine(line, foodItemsMap.await())
                }
                
                recipes.addAll(batchRecipes)
                processedCount += batch.size
                
                // Update progress
                val progress = (processedCount.toFloat() / totalEstimated).coerceAtMost(1f)
                onProgress(progress)
                
                // Insert in smaller batches for recipes due to complexity
                if (recipes.size >= 100) {
                    recipeDao.insertAll(recipes.take(100))
                    recipes.removeAll(recipes.take(100))
                    // Only log every 1000 processed items to reduce spam
                    if (processedCount % 1000 == 0) {
                        Log.d(TAG, "Processed $processedCount/$totalEstimated recipe lines (${(progress*100).toInt()}%)")
                    }
                }
            }
            
            // Insert remaining recipes
            if (recipes.isNotEmpty()) {
                recipeDao.insertAll(recipes)
            }
        }
        
        Log.d(TAG, "Recipe loading completed: ${recipeDao.getRecipeCount()} recipes")
    }
    
    /**
     * Process individual recipe line with nutrition calculation
     */
    private fun processRecipeLine(line: String, foodItemsMap: Map<String, FoodItem>): Recipe? {
        return try {
            val parts = line.split(",")
            if (parts.size < 6) return null
            
            val title = parts[1].trim()
            val instructionsRaw = parts[3].trim().split("\n")
                .map { it.trim() }.filter { it.isNotEmpty() }
            val imageName = parts[4].trim()
            val cleanedIngredientsRaw = parts[5].trim()
            
            val ingredientsList = parsePythonList(cleanedIngredientsRaw)
            
            val recipe = Recipe(
                name = title,
                description = title,
                ingredients = ingredientsList.toMutableList(),
                setUpInstructions = instructionsRaw.toMutableList(),
                imageResId = com.example.greenpantry.R.drawable.logo,
                imageName = imageName
            )
            
            // Calculate nutrition efficiently
            calculateRecipeNutrition(recipe, ingredientsList, foodItemsMap)
            
            recipe
        } catch (e: Exception) {
            Log.w(TAG, "Failed to process recipe line", e)
            null
        }
    }
    
    /**
     * Efficiently calculate recipe nutrition
     */
    private fun calculateRecipeNutrition(
        recipe: Recipe,
        ingredients: List<String>,
        foodItemsMap: Map<String, FoodItem>
    ) {
        for (ingredient in ingredients) {
            val item = foodItemsMap[ingredient.lowercase()]
            if (item != null) {
                recipe.calories += item.calories
                recipe.fiber += item.fiber
                recipe.totalFat += item.totFat
                recipe.sugars += item.sugars
                recipe.transFat += item.transFat
                recipe.protein += item.protein
                recipe.sodium += item.sodium
                recipe.iron += item.iron
                recipe.calcium += item.calcium
                recipe.carbs += item.carbs
            } else {
                // Add random nutrition for unknown ingredients
                recipe.calories += (1..15).random()
                recipe.fiber += (0..1).random()
                recipe.totalFat += (0..5).random()
                recipe.sugars += (0..1).random()
                recipe.transFat += (0..2).random()
                recipe.protein += (0..1).random()
                recipe.sodium += (0..1).random()
                recipe.iron += (0..1).random()
                recipe.calcium += (0..1).random()
                recipe.carbs += (0..1).random()
            }
        }
    }
    
    private fun mapFoodGroupToCategory(foodGroup: Int): String? {
        return when (foodGroup) {
            in setOf(2, 11, 16) -> "VEGETABLE"
            in setOf(9) -> "FRUIT"  
            in setOf(5, 7, 10, 12, 13, 15, 17) -> "PROTEIN"
            in setOf(8, 12, 18, 20) -> "GRAIN"
            in setOf(1) -> "DAIRY"
            in setOf(4, 6, 14, 19, 25) -> "OTHER"
            else -> null
        }
    }
    
    /**
     * Parse Python list format from CSV
     */
    private fun parsePythonList(pythonList: String): List<String> {
        return try {
            val trimmed = pythonList.trim().removePrefix("[").removeSuffix("]")
            trimmed.split("',\\s*'".toRegex())
                .map { it.trim().removePrefix("'").removeSuffix("'").replace("\"\"", "\"") }
                .filter { it.isNotEmpty() }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse python list: $pythonList", e)
            emptyList()
        }
    }
}

/**
 * Extension function to chunk sequences
 */
private fun <T> Sequence<T>.chunked(size: Int): Sequence<List<T>> = sequence {
    val iterator = this@chunked.iterator()
    while (iterator.hasNext()) {
        val chunk = mutableListOf<T>()
        repeat(size) {
            if (iterator.hasNext()) {
                chunk.add(iterator.next())
            }
        }
        if (chunk.isNotEmpty()) {
            yield(chunk)
        }
    }
}
