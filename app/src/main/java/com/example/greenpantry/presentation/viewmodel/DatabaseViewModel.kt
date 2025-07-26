package com.example.greenpantry.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greenpantry.data.database.DatabaseInitializer
import com.example.greenpantry.data.database.FoodItemDao
import com.example.greenpantry.data.database.RecipeDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DatabaseViewModel @Inject constructor(
    private val application: Application,
    private val databaseInitializer: DatabaseInitializer,
    val foodItemDao: FoodItemDao,
    val recipeDao: RecipeDao
) : ViewModel() {

    val loadingProgress: StateFlow<Float> = databaseInitializer.loadingProgress
    val loadingMessage: StateFlow<String> = databaseInitializer.loadingMessage
    val isLoading: StateFlow<Boolean> = databaseInitializer.isLoading

    private val _initializationComplete = MutableStateFlow(false)
    val initializationComplete: StateFlow<Boolean> = _initializationComplete.asStateFlow()

    fun initializeDatabases() {
        viewModelScope.launch {
            try {
                databaseInitializer.initializeDatabases(
                    context = application,
                    foodItemDao = foodItemDao,
                    recipeDao = recipeDao
                )
                _initializationComplete.value = true
            } catch (e: Exception) {
                // Handle error - could emit error state
                _initializationComplete.value = true // Allow app to continue
            }
        }
    }
}
