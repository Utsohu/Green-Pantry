package com.example.greenpantry.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {

    // Insert a single food item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItem(item: FoodItem)

    // Insert multiple food items
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FoodItem>)

    // Get all food items
    @Query("SELECT * FROM foodItems")
    suspend fun getAllFoodItems(): List<FoodItem>
    
    // Get all food items as Flow for real-time updates
    @Query("SELECT * FROM foodItems")
    fun getAllFoodItemsFlow(): Flow<List<FoodItem>>

    // Get one food item by name
    @Query("SELECT * FROM foodItems WHERE name = :name LIMIT 1")
    suspend fun getFoodItemByName(name: String): FoodItem?

    // Search items by partial name match
    @Query("SELECT * FROM foodItems WHERE name LIKE '%' || :query || '%'")
    suspend fun searchFoodItemsByName(query: String): List<FoodItem>

    // Count of items
    @Query("SELECT COUNT(*) FROM foodItems")
    suspend fun getItemCount(): Int
}
