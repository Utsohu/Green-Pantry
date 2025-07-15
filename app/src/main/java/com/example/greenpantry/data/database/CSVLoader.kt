package com.example.greenpantry.data.database

import android.content.Context
import android.util.Log
import com.example.greenpantry.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import androidx.core.content.edit

class CSVLoader(
    private val context: Context,
    private val dao: FoodItemDao
) {
    suspend fun loadIfNeeded() = withContext(Dispatchers.IO) {
        Log.d("Item Database", "Entered CSVLoader")

        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("data_loaded", false)) return@withContext
        Log.d("Item Database", "Items have not been loaded yet")

        fun readCSV(filename: String): List<List<String>> {
            return context.assets.open(filename).bufferedReader().useLines { lines ->
                lines.drop(1).map { it.split(",") }.toList() // drop to skip header
            }
        }

        /* filter IDS for nutrients
        cal: 208, fiber: 291, tot fat: 204, sugars: 269, trans fat: 605
        protein: 203, sodium: 307, iron: 303, calcium: 301, carbs: 205 */
        val calID = 208
        val fiberID = 291
        val totFatID = 204
        val sugarID = 269
        val transFatID = 605
        val protID = 203
        val sodiumID = 307
        val ironID = 303
        val calciumID = 301
        val carbsID = 205
        val nutrients = setOf(calID, fiberID, totFatID, sugarID, transFatID, protID, sodiumID, ironID, calciumID, carbsID)

        /* group items by food group - dataset already manually removed unused items
        */
        val vegeIds = setOf(2, 11, 16) // spices and herbs, vegetables, legumes
        val fruitIds = setOf(9) // fruit and fruit juices
        val protIds = setOf(5, 7, 10, 12, 13, 15, 17) // chicken, luncheons, pork, nuts/seeds, fish, lamb
        val grainIds = setOf(8, 12, 18, 20) // breakfast cereals, baked, pasta
        val dairyIds = setOf(1) // dairy/egg
        val otherIds = setOf(4, 6, 14, 19, 25) // fats/oils, soup/sauce, drinks, sweets, snacks

        val newItems = mutableListOf<FoodItem>()
        val nutrientMap = mutableMapOf<Int, MutableMap<Int, Int>>() // FoodID, then map of nutrient id and val

        // pre process nutrition, and image into maps
        for (row in readCSV("NUTRIENT_AMOUNT.csv")) {
            // hold food id and them its related nutrition
            val foodId = row[0].toIntOrNull() ?: continue
            val nutrientId = row[1].toIntOrNull() ?: continue
            val value = row[2].toDoubleOrNull() ?: continue // double values in csv

            // skip nutrient rows that are not the one's we want
            if (nutrientId !in nutrients) continue

            // is one of the nutrients and add to map
            val mapItem = nutrientMap.getOrPut(foodId) { mutableMapOf() }
            mapItem[nutrientId] = value.roundToInt()
        }

        for (row in readCSV("FOOD_NAME.csv")) {
            // want to create a food item and insert it into the database
            val foodID = row[0].toIntOrNull() ?: continue
            val foodGroup = row[2].toIntOrNull() ?: continue
            val foodName = row[4].trim()
            val foodImage = row[10].trim()

            // set the category
            val foodCat = when (foodGroup) {
                in vegeIds -> "VEGETABLE"
                in fruitIds -> "FRUIT"
                in protIds -> "PROTEIN"
                in grainIds -> "GRAIN"
                in dairyIds -> "DAIRY"
                in otherIds -> "OTHER"
                else -> continue // skip, might be the baby food, mixed food or fast foods
            }

            val itemNutrients = nutrientMap.get(foodID)

            // setup food item
            val foodItem = FoodItem(
                foodId = foodID,
                name = foodName,
                imageURL = foodImage,
                category = foodCat,
                servingSize = "100g", // this is what the nutrition data is based on
                calories = itemNutrients?.get(calID) ?: 0,
                fiber = itemNutrients?.get(fiberID) ?: 0,
                totFat = itemNutrients?.get(totFatID) ?: 0,
                sugars = itemNutrients?.get(sugarID) ?: 0,
                transFat = itemNutrients?.get(transFatID) ?: 0,
                protein = itemNutrients?.get(protID) ?: 0,
                sodium = itemNutrients?.get(sodiumID) ?: 0,
                iron = itemNutrients?.get(ironID) ?: 0,
                calcium = itemNutrients?.get(calciumID) ?: 0,
                carbs = itemNutrients?.get(carbsID) ?: 0
            )
            newItems.add(foodItem)

        }

        // added all items to db and mark as loaded
        dao.insertAll(newItems)
        prefs.edit { putBoolean("data_loaded", true) }
        Log.d("Item Database", "Added: ${dao.getItemCount()} items")
    }
}