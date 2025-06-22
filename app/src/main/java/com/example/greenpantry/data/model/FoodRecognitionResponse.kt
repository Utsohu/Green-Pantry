package com.example.greenpantry.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FoodRecognitionResponse(
    val itemName: String,
    val category: String,
    val isPackaged: Boolean,
    val brand: String? = null,
    val quantity: String? = null,
    val confidence: Float
)