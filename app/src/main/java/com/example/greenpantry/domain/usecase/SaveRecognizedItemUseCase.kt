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
            // Parse the amount from description, default to 1 if not a valid number
            val amount = description.toIntOrNull() ?: 1
            
            val pantryItem = recognizedItem.toPantryItem(
                description = description,
                imageResId = imageResId
            ).copy(curNum = amount)
            
            pantryItemDao.insertPantryItem(pantryItem)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}