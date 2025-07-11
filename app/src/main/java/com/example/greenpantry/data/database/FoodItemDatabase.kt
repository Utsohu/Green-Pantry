package com.example.greenpantry.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import android.util.Log
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.greenpantry.R
import com.example.greenpantry.data.database.PantryItemDatabase.Companion
import com.example.greenpantry.data.model.FoodCategory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [FoodItem::class], version = 1, exportSchema = false)
abstract class FoodItemDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao

    companion object {
        @Volatile
        private var INSTANCE: FoodItemDatabase? = null

        fun getDatabase(context: Context): FoodItemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodItemDatabase::class.java,
                    "foodItem_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                FoodItemDatabase.INSTANCE = instance
                instance
            }
        }
    }
}
