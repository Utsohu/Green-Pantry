package com.example.greenpantry.data.repository

import android.graphics.Bitmap
import com.example.greenpantry.data.model.RecognizedFoodItem
import kotlinx.coroutines.flow.Flow

interface FoodRecognitionRepository {
    suspend fun recognizeFoodItem(bitmap: Bitmap): Result<RecognizedFoodItem>
    suspend fun getCachedRecognition(imageHash: String): RecognizedFoodItem?
    suspend fun cacheRecognition(imageHash: String, item: RecognizedFoodItem)
    fun getRecentRecognitions(): Flow<List<RecognizedFoodItem>>
}