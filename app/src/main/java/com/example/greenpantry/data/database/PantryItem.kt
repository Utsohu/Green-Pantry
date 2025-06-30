package com.example.greenpantry.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pantryItems")
data class PantryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val imageResId: Int,
    val category: String? = null,
    val isPackaged: Boolean = false,
    val brand: String? = null,
    val quantity: String? = null,
    val recognitionConfidence: Float? = null,
    val imageUri: String? = null,
    val dateAdded: Long = System.currentTimeMillis(),
    val calories: Int = 0,
    val fiber: Int = 0,
    val totalFat: Int = 0,
    val sugars: Int = 0,
    val transFat: Int = 0,
    val protein: Int = 0,
    val sodium: Int = 0,
    val iron: Int = 0,
    val calcium: Int = 0,
    val vitaminD: Int = 0,
    val curNum: Int = 0
)
