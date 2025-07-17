package com.example.greenpantry.ui.recipes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greenpantry.data.model.GeneratedRecipe
import com.example.greenpantry.data.repository.RecipeGenerationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeGenerationViewModel @Inject constructor(
    private val repository: RecipeGenerationRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "RecipeGenerationVM"
    }
    
    private val _generatedRecipes = MutableLiveData<List<GeneratedRecipe>>()
    val generatedRecipes: LiveData<List<GeneratedRecipe>> = _generatedRecipes
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _saveSuccess = MutableLiveData<String?>()
    val saveSuccess: LiveData<String?> = _saveSuccess
    
    init {
        generateRecipes()
    }
    
    fun generateRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.generateRecipesFromPantry()
                .onSuccess { recipes ->
                    _generatedRecipes.value = recipes
                    _isLoading.value = false
                    Log.d(TAG, "Successfully generated ${recipes.size} recipes")
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to generate recipes"
                    _isLoading.value = false
                    Log.e(TAG, "Failed to generate recipes", exception)
                }
        }
    }
    
    fun regenerateRecipes() {
        Log.d(TAG, "Regenerating recipes")
        generateRecipes()
    }
    
    fun saveRecipeToDatabase(recipe: GeneratedRecipe) {
        viewModelScope.launch {
            repository.saveGeneratedRecipe(recipe)
                .onSuccess {
                    _saveSuccess.value = "${recipe.name} saved to your recipes!"
                    Log.d(TAG, "Recipe saved: ${recipe.name}")
                }
                .onFailure { exception ->
                    _error.value = "Failed to save recipe: ${exception.message}"
                    Log.e(TAG, "Failed to save recipe", exception)
                }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearSaveSuccess() {
        _saveSuccess.value = null
    }
}