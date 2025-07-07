package com.example.greenpantry.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Singleton

@Database(entities = [PantryItem::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PantryItemDatabase : RoomDatabase() {
    abstract fun pantryItemDao(): PantryItemDao

    companion object {
        @Volatile
        private var INSTANCE: PantryItemDatabase? = null

        fun getDatabase(context: Context): PantryItemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PantryItemDatabase::class.java,
                    "pantryItem_database"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
