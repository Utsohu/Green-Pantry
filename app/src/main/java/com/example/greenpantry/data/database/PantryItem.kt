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
    val dateAdded: Long = System.currentTimeMillis()
)
