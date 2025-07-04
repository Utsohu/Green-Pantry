package com.example.greenpantry.data.model

import android.net.Uri

data class RecognizedFoodItem(
    val name: String,
    val category: FoodCategory,
    val isPackaged: Boolean,
    val brand: String? = null,
    var quantity: String? = null,
    val confidence: Float,
    val imageUri: Uri? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toPantryItem(description: String = "", imageResId: Int = 0): com.example.greenpantry.data.database.PantryItem {
        return com.example.greenpantry.data.database.PantryItem(
            name = name,
            description = description,
            imageResId = imageResId,
            category = category.name,
            isPackaged = isPackaged,
            brand = brand,
            quantity = quantity,
            recognitionConfidence = confidence,
            imageUri = imageUri?.toString(),
            dateAdded = timestamp
        )
    }
}

enum class FoodCategory {
    FRUIT,
    VEGETABLE,
    DAIRY,
    GRAIN,
    PROTEIN,
    SNACK,
    BEVERAGE,
    CONDIMENT,
    OTHER;
    
    companion object {
        fun fromString(value: String): FoodCategory {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                when (value.lowercase()) {
                    "fruit", "fruits" -> FRUIT
                    "vegetable", "vegetables", "veggie" -> VEGETABLE
                    "dairy", "milk" -> DAIRY
                    "grain", "grains", "cereal" -> GRAIN
                    "protein", "meat", "fish" -> PROTEIN
                    "snack", "snacks" -> SNACK
                    "beverage", "beverages", "drink" -> BEVERAGE
                    "condiment", "condiments", "sauce" -> CONDIMENT
                    else -> OTHER
                }
            }
        }
    }
}