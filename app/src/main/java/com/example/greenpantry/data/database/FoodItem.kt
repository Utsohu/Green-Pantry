package com.example.greenpantry.data.database

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.greenpantry.data.model.FoodCategory

@Entity(tableName = "foodItems")
data class FoodItem(
    @PrimaryKey val foodId: Int,           // from FOOD_NAME.csv, FoodId column
    val name: String,                       // from FOOD_NAME.csv, FoodDescription column
    val description: String? = null,
    val imageURL: String,                  // assign using image database
    val category: String? = null,          // from FOOD_NAME.cvs, FoodGroup, but definitions in FOOD_GROUP.csv
    val servingSize: String,               // from MEASURE_NAME.csv, MeasureDescription
    val calories: Int = 0,                 // take from NUTRIENT_AMOUNT, use NUTRIENT_NAME TO HELP
    val fiber: Int = 0,                   // cal: 208, fiber: 291, tot fat: 204, sugars: 269, trans fat: 605
    val totFat: Int = 0,                  // protein: 203, sodium: 307, iron: 303, calcium: 301, carbs: 205
    val sugars: Int = 0,
    val transFat: Int = 0,
    val protein: Int = 0,
    val sodium: Int = 0,
    val iron: Int = 0,
    val calcium: Int = 0,
    val carbs: Int = 0,

)
