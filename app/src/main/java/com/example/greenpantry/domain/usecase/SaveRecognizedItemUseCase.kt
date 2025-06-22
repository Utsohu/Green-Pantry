package com.example.greenpantry.domain.usecase

import com.example.greenpantry.data.database.PantryItemDao
import com.example.greenpantry.data.model.RecognizedFoodItem
import javax.inject.Inject

class SaveRecognizedItemUseCase @Inject constructor(
    private val pantryItemDao: PantryItemDao
) {
    suspend operator fun invoke(
        recognizedItem: RecognizedFoodItem,
        description: String = "",
        imageResId: Int = 0
    ): Result<Unit> {
        return try {
            val pantryItem = recognizedItem.toPantryItem(
                description = description,
                imageResId = imageResId
            )
            pantryItemDao.insertPantryItem(pantryItem)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}