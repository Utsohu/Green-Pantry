package com.example.greenpantry.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val imageResId: Int,
    val time: Int = 30,
    val difficulty: Int = 5,
    val NOS: Int = 1, //number of servings
    val calories: Int = 0,
    val fiber: Int = 0,
    val totalFat: Int = 0,
    val sugars: Int = 0,
    val transFat: Int = 0,
    val protein: Int = 0,
    val sodium: Int = 0,
    val iron: Int = 0,
    val calcium: Int = 0,
    val carbs: Int = 0,
    val setUpInstructions: MutableList<String> = mutableListOf("Random Instruction 1", "Random Instruction 2"),
    val ingredients: MutableList<String> = mutableListOf("Romaine Lettuce", "Iceberg Lettuce", "Butter Lettuce"),
    val imageName: String = ""
)
