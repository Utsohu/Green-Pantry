package com.example.greenpantry.di

import android.content.Context
import com.example.greenpantry.data.api.GeminiApiClient
import com.example.greenpantry.data.api.GeminiConfig
import com.example.greenpantry.data.database.FoodItemDao
import com.example.greenpantry.data.database.FoodItemDatabase
import com.example.greenpantry.data.database.PantryItemDao
import com.example.greenpantry.data.database.PantryItemDatabase
import com.example.greenpantry.data.database.RecipeDao
import com.example.greenpantry.data.database.RecipeDatabase
import com.example.greenpantry.data.imageprocessing.ImageEncoder
import com.example.greenpantry.data.imageprocessing.ImagePreprocessor
import com.example.greenpantry.data.repository.FoodRecognitionRepository
import com.example.greenpantry.data.repository.FoodRecognitionRepositoryImpl
import com.example.greenpantry.data.repository.RecipeGenerationRepository
import com.example.greenpantry.data.repository.RecipeGenerationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideGeminiConfig(): GeminiConfig {
        return GeminiConfig()
    }
    
    @Provides
    @Singleton
    fun provideGeminiApiClient(config: GeminiConfig): GeminiApiClient {
        return GeminiApiClient(config)
    }
    
    @Provides
    @Singleton
    fun provideImagePreprocessor(): ImagePreprocessor {
        return ImagePreprocessor()
    }
    
    @Provides
    @Singleton
    fun provideImageEncoder(): ImageEncoder {
        return ImageEncoder()
    }
    
    @Provides
    @Singleton
    fun provideFoodRecognitionRepository(
        apiClient: GeminiApiClient,
        imagePreprocessor: ImagePreprocessor
    ): FoodRecognitionRepository {
        return FoodRecognitionRepositoryImpl(apiClient, imagePreprocessor)
    }
    
    @Provides
    @Singleton
    fun providePantryItemDatabase(@ApplicationContext context: Context): PantryItemDatabase {
        return PantryItemDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun providePantryItemDao(database: PantryItemDatabase): PantryItemDao {
        return database.pantryItemDao()
    }
    
    @Provides
    @Singleton
    fun provideFoodItemDatabase(@ApplicationContext context: Context): FoodItemDatabase {
        return FoodItemDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideFoodItemDao(database: FoodItemDatabase): FoodItemDao {
        return database.foodItemDao()
    }
    
    @Provides
    @Singleton
    fun provideRecipeDatabase(@ApplicationContext context: Context): RecipeDatabase {
        return RecipeDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideRecipeDao(database: RecipeDatabase): RecipeDao {
        return database.recipeDao()
    }
    
    @Provides
    @Singleton
    fun provideRecipeGenerationRepository(
        geminiApiClient: GeminiApiClient,
        pantryItemDao: PantryItemDao,
        recipeDao: RecipeDao
    ): RecipeGenerationRepository {
        return RecipeGenerationRepositoryImpl(geminiApiClient, pantryItemDao, recipeDao)
    }
}