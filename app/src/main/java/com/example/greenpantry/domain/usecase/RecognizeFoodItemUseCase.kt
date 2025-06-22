package com.example.greenpantry.domain.usecase

import android.graphics.Bitmap
import com.example.greenpantry.data.model.RecognizedFoodItem
import com.example.greenpantry.data.repository.FoodRecognitionRepository
import javax.inject.Inject

class RecognizeFoodItemUseCase @Inject constructor(
    private val repository: FoodRecognitionRepository
) {
    suspend operator fun invoke(bitmap: Bitmap): Result<RecognizedFoodItem> {
        return repository.recognizeFoodItem(bitmap)
    }
}