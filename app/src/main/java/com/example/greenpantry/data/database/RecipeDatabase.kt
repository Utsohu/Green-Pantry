package com.example.greenpantry.data.database

import android.content.Context
import android.net.Uri
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.greenpantry.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Recipe::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        fun getDatabase(context: Context): RecipeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                RecipeDatabase::class.java,
                                "recipe_database"
                ).fallbackToDestructiveMigration(false)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Insert initial data using coroutine
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.recipeDao()?.apply {
                                    insertAll(listOf(
                                        Recipe(
                                            name = "Avocado Toast",
                                            description = "A healthy breakfast",
                                            imageResId = R.drawable.img_avocado_toast,
                                            time = 50, difficulty = 7, NOS = 2,
                                            calories = 120, fiber = 5, totalFat = 8, sugars = 2,
                                            transFat = 5, protein = 2, sodium = 2, iron = 3,
                                            calcium = 4, vitaminD = 1,
                                            setUpInstructions = mutableListOf("Follow the instructions and try to make a good meal!","Go out to restaurant.", "Buy the food!"),
                                            ingredients = mutableListOf("Romaine Lettuce", "Kale", "Yu Choy", "Apple") // need to change this to list of items
                                        ),
                                        Recipe(
                                            name = "Quinoa Salad",
                                            description = "Protein-rich lunch",
                                            imageResId = R.drawable.img_quinoasalad,
                                            ingredients = mutableListOf("Yu Choy", "Apple", "Tomato")
                                        ),
                                        Recipe(
                                            name = "Smoothie Bowl",
                                            description = "Energizing snack",
                                            imageResId = R.drawable.img_smoothiebowl
                                        ),
                                        Recipe(
                                            name = "Kale Smoothie",
                                            description = "A healthy green smoothie",
                                            imageResId = R.drawable.logo,
                                            ingredients = mutableListOf("Kale", "Banana", "Apple")
                                        ),
                                        Recipe(
                                            name = "Tomato Egg Stir-fry",
                                            description = "Classic Chinese comfort food",
                                            imageResId = R.drawable.logo,
                                            ingredients = mutableListOf("Tomato", "Eggs", "Green Onion")
                                        ),
                                        Recipe(
                                            name = "Apple Carrot Slaw",
                                            description = "Crunchy and fresh side dish",
                                            imageResId = R.drawable.logo,
                                            ingredients = mutableListOf("Apple", "Carrot", "Lemon Juice")
                                        ),
                                        Recipe(
                                            name = "Yu Choy Garlic Stir-fry",
                                            description = "Simple and savory greens",
                                            imageResId = R.drawable.logo,
                                            ingredients = mutableListOf("Yu Choy", "Garlic", "Soy Sauce")
                                        ),
                                        Recipe(
                                            name = "Romaine Lettuce Wraps",
                                            description = "Low-carb lettuce wraps",
                                            imageResId = R.drawable.logo,
                                            ingredients = mutableListOf("Romaine Lettuce", "Carrot", "Tofu")
                                        ),
                                        Recipe(
                                            name = "Banana Oat Pancakes",
                                            description = "Healthy breakfast option",
                                            imageResId = R.drawable.logo,
                                            ingredients = mutableListOf("Banana", "Oats", "Eggs")
                                        )
                                    ))
                                    // Add more recipes if needed
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

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromStringMutableList(value: MutableList<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringMutableList(value: String): MutableList<String> {
        val listType = object : TypeToken<MutableList<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }
}
