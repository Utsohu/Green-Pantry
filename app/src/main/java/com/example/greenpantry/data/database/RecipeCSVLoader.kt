package com.example.greenpantry.data.database

import android.content.Context
import android.util.Log
import com.example.greenpantry.R
import com.opencsv.CSVReaderBuilder
import java.io.BufferedReader
import java.io.InputStreamReader
import org.json.JSONArray

suspend fun loadRecipesFromCSV(context: Context, itemDao: FoodItemDao): List<Recipe> {
    val recipes = mutableListOf<Recipe>()

    try {
        val inputStream = context.assets.open("RECIPE_SETUP_DATA.csv")
        val reader = CSVReaderBuilder(InputStreamReader(inputStream))
            .withSkipLines(1) // Skip header line
            .build()

        reader.forEachIndexed { index, row ->
            try {
                // Column indices based on your CSV:
                // 0: ID, 1: Title, 2: Ingredients, 3: Instructions, 4: Image_Name, 5: Cleaned_Ingredients

                val title = row.getOrNull(1)?.trim() ?: return@forEachIndexed
                val instructionsRaw = row.getOrNull(3)
                    ?.trim()
                    ?.split(".")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() } ?: return@forEachIndexed
                val imageName = row.getOrNull(4)?.trim() ?: return@forEachIndexed
                val cleanedIngredientsRaw = row.getOrNull(5)?.trim() ?: return@forEachIndexed

                val ingredientsList = parsePythonList(cleanedIngredientsRaw)
                val mainIngredients = ingredientsList.map { extractMainIngredient(it) }

                val recipe = Recipe(
                    name = title,
                    description = title,
                    ingredients = mainIngredients.toMutableList(),
                    setUpInstructions = instructionsRaw.toMutableList(),
                    imageResId = R.drawable.logo, // now a String, like "chicken-miso"
                    imageName = imageName
                )

                for (ingredient in mainIngredients) {
                    val item = itemDao.getFoodItemByName(ingredient)
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
                    }
                    else{
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

                recipes.add(recipe)
                Log.d("RecipeCSV", "Added recipe #$index: $title (${mainIngredients.size} ingredients)")

            } catch (e: Exception) {
                Log.e("RecipeCSV", "Error parsing row #$index", e)
            }
        }

    } catch (e: Exception) {
        Log.e("RecipeCSV", "Error reading CSV file", e)
    }

    Log.d("RecipeCSV", "Total recipes loaded: ${recipes.size}")
    return recipes
}

fun parsePythonList(pythonList: String): List<String> {
    return try {
        // Remove the surrounding square brackets if present
        val trimmed = pythonList.trim().removePrefix("[").removeSuffix("]")

        // Split on "', '" or similar, accounting for possible leading/trailing single quotes
        trimmed.split("',\\s*'".toRegex())
            .map {
                it.trim().removePrefix("'").removeSuffix("'").replace("\"\"", "\"")
            }
            .filter { it.isNotEmpty() }
    } catch (e: Exception) {
        e.printStackTrace()
        listOf()
    }
}


// Extract the main ingredient name from the full ingredient phrase
fun extractMainIngredient(ingredient: String): String {
    // Remove parenthetical info like (about 3 lb. total)
    val noParens = ingredient.replace("\\(.*?\\)".toRegex(), "")
    // Remove leading quantities, fractions, units, e.g. "1", "2 Tbsp.", "¼ tsp.", etc.
    val noQty = noParens.replace("^([\\d¼½¾⅓⅔\\s./-]+[a-zA-Z]*\\.?\\s*)+".toRegex(), "")
    // Split remaining phrase by spaces and take the last word as the main ingredient
    val words = noQty.trim().split(" ")
    return if (words.isNotEmpty()) words.last().lowercase() else ""
}


