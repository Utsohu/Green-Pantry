package com.example.greenpantry.data.model

import android.graphics.Bitmap

data class FoodRecognitionRequest(
    val bitmap: Bitmap,
    val userId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)