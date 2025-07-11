package com.example.greenpantry.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greenpantry.data.database.PantryItem
import com.example.greenpantry.data.database.PantryItemDao
import com.example.greenpantry.data.model.FoodCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val pantryItemDao: PantryItemDao
) : ViewModel() {
    
    private val _pantryItems = MutableStateFlow<List<PantryItem>>(emptyList())
    val pantryItems: StateFlow<List<PantryItem>> = _pantryItems.asStateFlow()
    
    private val _categoryCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val categoryCounts: StateFlow<Map<String, Int>> = _categoryCounts.asStateFlow()
    
    private var hasInitialized = false
    
    init {
        loadPantryItems()
    }
    
    private fun loadPantryItems() {
        // Prevent multiple initializations
        if (hasInitialized) return
        hasInitialized = true
        
        viewModelScope.launch {
            try {
                // Use single database call instead of flow to reduce overhead
                val items = pantryItemDao.getAllPantryItems()
                _pantryItems.value = items
                updateCategoryCounts(items)
            } catch (e: Exception) {
                // Handle error case
                _pantryItems.value = emptyList()
                _categoryCounts.value = emptyMap()
            }
        }
    }
    
    fun refreshData() {
        // Manual refresh method that can be called when data actually changes
        viewModelScope.launch {
            try {
                val items = pantryItemDao.getAllPantryItems()
                _pantryItems.value = items
                updateCategoryCounts(items)
            } catch (e: Exception) {
                // Keep existing data on error
            }
        }
    }
    
    private fun updateCategoryCounts(items: List<PantryItem>) {
        val counts = mutableMapOf<String, Int>()
        
        // Initialize all categories with 0
        FoodCategory.values().forEach { category ->
            counts[category.name] = 0
        }
        
        // Count items by category
        items.forEach { item ->
            val category = item.category ?: "OTHER"
            counts[category] = (counts[category] ?: 0) + 1
        }
        
        _categoryCounts.value = counts
    }
    
    fun getCategoryCount(category: FoodCategory): Int {
        return categoryCounts.value[category.name] ?: 0
    }
}