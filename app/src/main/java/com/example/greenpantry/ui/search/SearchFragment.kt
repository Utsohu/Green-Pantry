package com.example.greenpantry.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenpantry.R
import com.example.greenpantry.data.database.PantryItem
import com.example.greenpantry.data.database.PantryItemDatabase
import com.example.greenpantry.data.database.RecipeDatabase
import com.example.greenpantry.ui.home.ItemDetailFragment
import com.example.greenpantry.ui.home.RecipeDetailFragment
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.example.greenpantry.ui.utils.ListOptimizations
import com.example.greenpantry.ui.sharedcomponents.resetNav
import com.example.greenpantry.ui.sharedcomponents.setupNotifBtn
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import android.text.TextWatcher
import android.text.Editable
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class SearchFragment : Fragment(R.layout.fragment_search) {
    private var allItems: List<PantryItem> = emptyList()
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchItemAdapter
    private var searchJob: Job? = null
    private var hasLoadedData = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ensure search icon is on, instead of camera if it was redirected
        resetNav(view, 2)
        setupNotifBtn(view)

        // search text value
        val inputField = view.findViewById<EditText>(R.id.searchInput)

        // search RecyclerView
        searchRecyclerView = view.findViewById(R.id.searchRecyclerView)
        
        // Set up RecyclerView with optimizations immediately
        setupRecyclerView()
        
        // Load data only once and cache it
        loadSearchData(inputField)
    }
    
    private fun setupRecyclerView() {
        // Check if fragment is still attached
        if (!isAdded || context == null) return
        
        searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true) // Performance optimization
            setItemViewCacheSize(20) // Cache more views for smoother scrolling
            
            // Apply additional performance optimizations
            ListOptimizations.optimizeList(this)
        }
    }
    
    private fun loadSearchData(inputField: EditText) {
        // Avoid reloading data if already loaded
        if (hasLoadedData && allItems.isNotEmpty()) {
            setupSearchFunctionality(inputField)
            return
        }

        // Check if fragment is still attached to avoid crashes
        if (!isAdded || context == null) return

        val db = PantryItemDatabase.getDatabase(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                allItems = db.pantryItemDao().getAllPantryItems()
                hasLoadedData = true
                
                // Check if fragment is still attached before updating UI
                if (!isAdded || view == null) return@launch
                
                // Initialize adapter once with all items
                searchAdapter = SearchItemAdapter(allItems) { item ->
                    // Check if fragment is still attached before navigation
                    if (isAdded && parentFragmentManager.isStateSaved.not()) {
                        val itemDetailFragment = ItemDetailFragment.newInstance(item.name)
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, itemDetailFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
                
                searchRecyclerView.adapter = searchAdapter
                
                // Set up search functionality after data is loaded
                setupSearchFunctionality(inputField)
                
            } catch (e: Exception) {
                // Handle error case - check if fragment is still attached
                if (!isAdded || view == null) return@launch
                
                allItems = emptyList()
                searchAdapter = SearchItemAdapter(emptyList()) { }
                searchRecyclerView.adapter = searchAdapter
            }
        }
    }
    
    private fun setupSearchFunctionality(inputField: EditText) {
        inputField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Debounce search to avoid excessive filtering
                searchJob?.cancel()
                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(100) // Wait 100ms before executing search
                    filterAndDisplayItems(s.toString())
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
        
        // Initial display with all items
        filterAndDisplayItems(inputField.text.toString())
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        // Cancel any pending search jobs to prevent memory leaks
        searchJob?.cancel()
        searchJob = null
    }

    private fun filterAndDisplayItems(query: String) {
        // Check if fragment is still attached and adapter is initialized
        if (!isAdded || !::searchAdapter.isInitialized) return
        
        val filteredItems = if (query.isEmpty()) {
            allItems
        } else {
            allItems.filter { it.name.contains(query, ignoreCase = true) }
        }
        
        try {
            searchAdapter.updateItems(filteredItems)
        } catch (e: Exception) {
            // Handle adapter update errors gracefully
            e.printStackTrace()
        }
    }
}

class SearchItemAdapter(
    private var items: List<PantryItem>,
    private val onItemClick: (PantryItem) -> Unit
) : RecyclerView.Adapter<SearchItemAdapter.SearchViewHolder>() {
    
    class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemImage)
        val titleView: TextView = view.findViewById(R.id.itemTitle)
        val descView: TextView = view.findViewById(R.id.itemDescription)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipes_items, parent, false)
        return SearchViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        if (position >= items.size) return // Safety check
        
        val item = items[position]
        
        try {
            holder.imageView.setImageResource(item.imageResId)
        } catch (e: Exception) {
            // Set a default image if resource loading fails
            holder.imageView.setImageResource(R.drawable.ic_launcher_foreground)
        }
        
        holder.titleView.text = item.name
        holder.descView.text = "Quantity: ${item.curNum}"
        
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }
    
    override fun getItemCount(): Int = items.size
    
    fun updateItems(newItems: List<PantryItem>) {
        try {
            val diffCallback = object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = items.size
                override fun getNewListSize(): Int = newItems.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return if (oldItemPosition < items.size && newItemPosition < newItems.size) {
                        items[oldItemPosition].name == newItems[newItemPosition].name
                    } else false
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return if (oldItemPosition < items.size && newItemPosition < newItems.size) {
                        val oldItem = items[oldItemPosition]
                        val newItem = newItems[newItemPosition]
                        oldItem.name == newItem.name && 
                        oldItem.curNum == newItem.curNum && 
                        oldItem.imageResId == newItem.imageResId
                    } else false
                }
            }

            // Use async DiffUtil for better performance with large lists
            val diffResult = DiffUtil.calculateDiff(diffCallback, true)
            items = newItems
            diffResult.dispatchUpdatesTo(this)
        } catch (e: Exception) {
            // Fallback to simple update if DiffUtil fails
            items = newItems
            notifyDataSetChanged()
        }
    }
}