package com.example.greenpantry.data.model

import java.io.Serializable

data class NutritionInfo(
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val fiber: Int
) : Serializable