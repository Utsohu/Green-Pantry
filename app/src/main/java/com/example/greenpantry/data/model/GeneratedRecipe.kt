package com.example.greenpantry.data.model

import java.io.Serializable

data class GeneratedRecipe(
    val name: String,
    val description: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val time: Int,
    val difficulty: Int,
    val servings: Int,
    val nutrition: NutritionInfo
) : Serializable