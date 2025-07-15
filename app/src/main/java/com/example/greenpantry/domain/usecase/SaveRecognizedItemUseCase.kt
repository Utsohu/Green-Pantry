package com.example.greenpantry.domain.usecase

import android.content.Context
import com.example.greenpantry.R
import com.example.greenpantry.data.database.PantryItemDao
import com.example.greenpantry.data.database.PantryItemDatabase
import com.example.greenpantry.data.model.RecognizedFoodItem
import javax.inject.Inject

class SaveRecognizedItemUseCase @Inject constructor(
    private val pantryItemDAO: PantryItemDao
) {
    suspend operator fun invoke(
        recognizedItem: RecognizedFoodItem,
        description: String = "",
        imageResId: Int = 0
    ): Result<Unit> {
        return try {
            // Parse the amount from description, default to 1 if not a valid number
            val amount = description.toIntOrNull() ?: 1
            // Check if item already exists in pantry
            val existingItem = pantryItemDAO.getPantryItemByName(recognizedItem.name)
            
            val pantryItem = if (existingItem != null) {
                // Add to existing quantity instead of replacing
                existingItem.copy(curNum = existingItem.curNum + amount)
            } else {
                // Create new item
                recognizedItem.toPantryItem(
                    description = description,
                    imageURL =  ""
                ).copy(curNum = amount)
            }

            pantryItemDAO.insertPantryItem(pantryItem)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}