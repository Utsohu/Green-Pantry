package com.example.greenpantry.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.greenpantry.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [PantryItem::class], version = 7, exportSchema = false)
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
                ).fallbackToDestructiveMigration(false)
                    .addCallback(object : RoomDatabase.Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Insert initial data here
                            // Use coroutine to avoid blocking
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    val dao = database.pantryItemDao()
                                    dao.insertAll(listOf(
                                        PantryItem(name = "Romaine Lettuce",
                                            quantity = "5",
                                            curNum = 500,
                                            description = "Romaine Lettuce!!!",
                                            imageResId = R.drawable.logo),
                                        PantryItem(
                                            name = "Banana",
                                            quantity = "3",
                                            curNum = 300,
                                            description = "Fresh bananas",
                                            imageResId = R.drawable.logo
                                        ),
                                        PantryItem(
                                            name = "Carrot",
                                            quantity = "6",
                                            curNum = 600,
                                            description = "Organic carrots",
                                            imageResId = R.drawable.logo
                                        ),
                                        PantryItem(
                                            name = "Kale",
                                            quantity = "2",
                                            curNum = 200,
                                            description = "Dark green leafy kale",
                                            imageResId = R.drawable.logo
                                        ),
                                        PantryItem(
                                            name = "Yu Choy",
                                            quantity = "3",
                                            curNum = 300,
                                            description = "Tender Asian greens",
                                            imageResId = R.drawable.logo
                                        )
                                    ))

                                }
                            }
                        }
                    })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
