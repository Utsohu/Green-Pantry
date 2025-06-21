package com.example.greenpantry.data.database

import androidx.room.*

@Dao
interface PantryItemDao {

    // Insert a single pantry item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPantryItem(item: PantryItem)

    // Insert multiple pantry items
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PantryItem>)

    // Get all pantry items
    @Query("SELECT * FROM pantryItems")
    suspend fun getAllPantryItems(): List<PantryItem>

    // Get one pantry item by name
    @Query("SELECT * FROM pantryItems WHERE name = :name LIMIT 1")
    suspend fun getPantryItemByName(name: String): PantryItem?

    // Update a pantry item
    @Update
    suspend fun updatePantryItem(item: PantryItem)

    // Delete one pantry item
    @Delete
    suspend fun deletePantryItem(item: PantryItem)

    // Delete all pantry items
    @Query("DELETE FROM pantryItems")
    suspend fun deleteAllPantryItems()

    // Count of items
    @Query("SELECT COUNT(*) FROM pantryItems")
    suspend fun getItemCount(): Int

    // Search items by partial name match
    @Query("SELECT * FROM pantryItems WHERE name LIKE '%' || :query || '%'")
    suspend fun searchItemsByName(query: String): List<PantryItem>
}
