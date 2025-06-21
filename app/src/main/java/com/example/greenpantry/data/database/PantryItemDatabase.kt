package com.example.greenpantry.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PantryItem::class], version = 1, exportSchema = false)
abstract class PantryItemDatabase : RoomDatabase() {
    abstract fun PantryItemDao(): PantryItemDao

    companion object {
        @Volatile
        private var INSTANCE: PantryItemDatabase? = null

        fun getDatabase(context: Context): PantryItemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PantryItemDatabase::class.java,
                    "pantryItem_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}