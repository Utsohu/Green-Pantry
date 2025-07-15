package com.example.greenpantry.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greenpantry.data.database.FoodItem
import com.example.greenpantry.data.database.FoodItemDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val foodItemDao: FoodItemDao
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<FoodItem>>(emptyList())
    val searchResults: StateFlow<List<FoodItem>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private var searchJob: Job? = null
    private var currentQuery = ""

    init {
        // Load initial items (popular/recommended items)
        loadInitialItems()
    }

    fun search(query: String, debounceMs: Long = 300) {
        currentQuery = query
        
        // Cancel previous search
        searchJob?.cancel()
        
        searchJob = viewModelScope.launch {
            // Debounce search to avoid excessive queries
            delay(debounceMs)
            
            _isLoading.value = true
            
            try {
                val results = if (query.trim().isEmpty()) {
                    // Show popular items when no query
                    getPopularItems()
                } else {
                    performEnhancedSearch(query.trim())
                }
                
                _searchResults.value = results
            } catch (e: Exception) {
                // Handle error - could emit to error state if needed
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
        // Re-run search with new category filter
        if (currentQuery.isNotEmpty() || category.isNotEmpty()) {
            performFilteredSearch()
        }
    }

    private fun performFilteredSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = foodItemDao.searchFoodItemsWithFilter(
                    query = currentQuery,
                    category = _selectedCategory.value
                )
                _searchResults.value = results.take(50)
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun performEnhancedSearch(query: String): List<FoodItem> {
        val cleanQuery = query.lowercase().trim()
        
        return try {
            // Get all items that match the search query
            val searchResults = if (_selectedCategory.value.isNotEmpty()) {
                // Search with category filter
                foodItemDao.searchFoodItemsWithFilter(cleanQuery, _selectedCategory.value)
            } else {
                // Search all categories
                foodItemDao.searchFoodItemsByName(cleanQuery)
            }
            
            // Apply client-side ranking and filtering
            rankAndFilterResults(searchResults, cleanQuery).take(50)
        } catch (e: Exception) {
            // Fallback to basic search
            foodItemDao.searchFoodItemsByName(cleanQuery).take(50)
        }
    }

    private fun rankAndFilterResults(items: List<FoodItem>, query: String): List<FoodItem> {
        return items.sortedBy { item ->
            val itemName = item.name.lowercase()
            when {
                // Exact match gets highest priority
                itemName == query -> 0
                // Starts with query gets second priority
                itemName.startsWith(query) -> 1
                // Contains query as whole word gets third priority
                itemName.contains(" $query") || itemName.contains("$query ") -> 2
                // Contains query anywhere gets fourth priority
                itemName.contains(query) -> 3
                // Category match gets lowest priority
                item.category?.lowercase()?.contains(query) == true -> 4
                else -> 5
            }
        }
    }

    private fun loadInitialItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load a mix of items from different categories
                val popularItems = getPopularItems()
                _searchResults.value = popularItems
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getPopularItems(): List<FoodItem> {
        return try {
            // Get a diverse set of popular items from different categories
            val categories = listOf("FRUIT", "VEGETABLE", "PROTEIN", "DAIRY", "GRAIN")
            val popularItems = mutableListOf<FoodItem>()
            
            categories.forEach { category ->
                val categoryItems = foodItemDao.getFoodItemsByCategory(category).take(10)
                popularItems.addAll(categoryItems)
            }
            
            // Shuffle and limit to create variety
            popularItems.shuffled().take(30)
        } catch (e: Exception) {
            // Fallback to all items if category search fails
            foodItemDao.getAllFoodItems().take(30)
        }
    }

    fun clearSearch() {
        currentQuery = ""
        _selectedCategory.value = ""
        loadInitialItems()
    }

    // Get unique categories for filter dropdown
    suspend fun getAvailableCategories(): List<String> {
        return try {
            listOf("", "FRUIT", "VEGETABLE", "PROTEIN", "DAIRY", "GRAIN", "OTHER")
        } catch (e: Exception) {
            emptyList()
        }
    }
}
