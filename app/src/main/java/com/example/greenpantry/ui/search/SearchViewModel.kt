package com.example.greenpantry.ui.search

import android.util.Log
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
        Log.d("SearchViewModel", "Initializing SearchViewModel")
        loadInitialItems()
    }

    fun search(query: String, debounceMs: Long = 150) {
        currentQuery = query
        
        // Show loading immediately for better UX
        if (query.trim().isNotEmpty()) {
            Log.d("SearchViewModel", "Setting loading to true immediately for query: '$query'")
            _isLoading.value = true
        }
        
        // Cancel previous search
        searchJob?.cancel()
        
        searchJob = viewModelScope.launch {
            // Debounce search to avoid excessive queries
            delay(debounceMs)
            
            // Set loading again after debounce in case it was cleared
            if (!_isLoading.value) {
                Log.d("SearchViewModel", "Setting loading to true after debounce for query: '$query'")
                _isLoading.value = true
            }
            
            try {
                val results = if (query.trim().isEmpty()) {
                    // Show popular items when no query but respect category filter
                    if (_selectedCategory.value.isNotEmpty()) {
                        foodItemDao.getFoodItemsByCategory(_selectedCategory.value).take(20) // Reduced from 30 to 20
                    } else {
                        getPopularItems()
                    }
                } else {
                    performEnhancedSearch(query.trim())
                }
                
                _searchResults.value = results
                Log.d("SearchViewModel", "Search completed, found ${results.size} items")
            } catch (e: Exception) {
                // Handle error - could emit to error state if needed
                Log.e("SearchViewModel", "Search error", e)
                _searchResults.value = emptyList()
            } finally {
                Log.d("SearchViewModel", "Setting loading to false")
                _isLoading.value = false
            }
        }
    }

    fun setCategory(category: String) {
        Log.d("SearchViewModel", "Category changed to: '$category'")
        _selectedCategory.value = category
        // Re-run search with new category filter
        if (currentQuery.isNotEmpty() || category.isNotEmpty()) {
            performFilteredSearch()
        } else {
            // If no query and category is empty, reload initial items
            loadInitialItems()
        }
    }

    private fun performFilteredSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = if (currentQuery.isEmpty() && _selectedCategory.value.isNotEmpty()) {
                    // Show category items when no query but category is selected
                    foodItemDao.getFoodItemsByCategory(_selectedCategory.value).take(25) // Reduced from 50 to 25
                } else {
                    // Normal search with filter
                    foodItemDao.searchFoodItemsWithFilter(
                        query = currentQuery,
                        category = _selectedCategory.value
                    )
                }
                _searchResults.value = results.take(25) // Reduced from 50 to 25
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
            rankAndFilterResults(searchResults, cleanQuery).take(25) // Reduced from 50 to 25
        } catch (e: Exception) {
            // Fallback to basic search
            foodItemDao.searchFoodItemsByName(cleanQuery).take(25) // Reduced from 50 to 25
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
            Log.d("SearchViewModel", "Loading initial items")
            _isLoading.value = true
            try {
                // Load a mix of items from different categories
                val popularItems = getPopularItems()
                _searchResults.value = popularItems
                Log.d("SearchViewModel", "Initial items loaded: ${popularItems.size}")
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error loading initial items", e)
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getPopularItems(): List<FoodItem> {
        return try {
            // Get a smaller, more diverse set of popular items from different categories
            val categories = listOf("FRUIT", "VEGETABLE", "PROTEIN", "DAIRY", "GRAIN")
            val popularItems = mutableListOf<FoodItem>()
            
            // Take fewer items per category for better performance
            categories.forEach { category ->
                val categoryItems = foodItemDao.getFoodItemsByCategory(category).take(4) // Reduced from 10 to 4
                popularItems.addAll(categoryItems)
            }
            
            // Shuffle and limit to create variety - reduced total items
            popularItems.shuffled().take(15) // Reduced from 30 to 15
        } catch (e: Exception) {
            // Fallback to fewer items if category search fails
            foodItemDao.getAllFoodItems().take(15) // Reduced from 30 to 15
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
