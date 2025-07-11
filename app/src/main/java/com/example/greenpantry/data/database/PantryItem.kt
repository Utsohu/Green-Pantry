package com.example.greenpantry.data.database

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.greenpantry.data.model.FoodCategory

@Entity(tableName = "pantryItems")
data class PantryItem(
    @PrimaryKey val name: String,
    val description: String,
    val imageResId: Int,
    val category: String? = null,
    val isPackaged: Boolean = false,
    val brand: String? = null,
    var quantity: String? = null, // this is the unit
    val recognitionConfidence: Float? = null,
    val imageUri: String? = null,
    val dateAdded: Long = System.currentTimeMillis(),
    val calories: Int = 0,
    val fiber: Int = 0,
    val totFat: Int = 0,
    val sugars: Int = 0,
    val transFat: Int = 0,
    val protein: Int = 0,
    val sodium: Int = 0,
    val iron: Int = 0,
    val calcium: Int = 0,
    val carbs: Int = 0,
    var curNum: Int = 0
)
