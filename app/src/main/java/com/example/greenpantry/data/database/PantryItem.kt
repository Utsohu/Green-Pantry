package com.example.greenpantry.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pantryItems")
data class PantryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val imageResId: Int
)
