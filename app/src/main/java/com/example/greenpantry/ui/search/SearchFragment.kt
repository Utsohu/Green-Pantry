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
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.greenpantry.R
import com.example.greenpantry.data.database.PantryItem
import com.example.greenpantry.data.database.PantryItemDatabase
import com.example.greenpantry.data.database.RecipeDatabase
import com.example.greenpantry.ui.home.ItemDetailFragment
import com.example.greenpantry.ui.home.RecipeDetailFragment
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.example.greenpantry.ui.sharedcomponents.resetNav
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import android.text.TextWatcher
import android.text.Editable
import android.util.Log
import com.example.greenpantry.data.database.FoodItem
import com.example.greenpantry.data.database.FoodItemDatabase
import com.bumptech.glide.Glide
import com.example.greenpantry.ui.sharedcomponents.groupImg
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var inputField: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var searchAdapter: SearchAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ensure search icon is on, instead of camera if it was redirected
        resetNav(view, 2)

        initializeViews(view)
        setupCategoryFilter()
        setupSearchInput()
        observeViewModel()
    }

    private fun initializeViews(view: View) {
        inputField = view.findViewById(R.id.searchInput)
        searchRecyclerView = view.findViewById(R.id.searchList)
        
        // Setup RecyclerView with adapter
        searchAdapter = SearchAdapter { item ->
            // Handle item click
            val itemDetailFragment = ItemDetailFragment.newInstance(item.name)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, itemDetailFragment)
                .addToBackStack(null)
                .commit()
        }
        
        searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }
    }

    private fun setupCategoryFilter() {
        // Add category spinner if it exists in layout
        val categoryContainer = view?.findViewById<LinearLayout>(R.id.categoryFilterContainer)
        if (categoryContainer != null) {
            categorySpinner = view?.findViewById(R.id.categorySpinner) 
                ?: Spinner(requireContext()).apply {
                    categoryContainer.addView(this)
                }
            
            lifecycleScope.launch {
                val categories = viewModel.getAvailableCategories()
                val categoryNames = categories.map { 
                    if (it.isEmpty()) "All Categories" else it.lowercase().replaceFirstChar { char -> char.uppercase() }
                }
                
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                categorySpinner.adapter = adapter
                
                categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedCategory = if (position == 0) "" else categories[position]
                        viewModel.setCategory(selectedCategory)
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }
    }

    private fun setupSearchInput() {
        inputField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collect { items ->
                displaySearchResults(items)
            }
        }
    }

    private fun displaySearchResults(items: List<FoodItem>) {
        searchAdapter.submitList(items)
    }

    // RecyclerView Adapter with DiffUtil for efficient updates and smooth scrolling
    private class SearchAdapter(
        private val onItemClick: (FoodItem) -> Unit
    ) : ListAdapter<FoodItem, SearchAdapter.ViewHolder>(FoodItemDiffCallback()) {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recipes_items, parent, false)
            return ViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position), onItemClick)
        }
        
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val imageView: ImageView = itemView.findViewById(R.id.itemImage)
            private val titleView: TextView = itemView.findViewById(R.id.itemTitle)
            private val descView: TextView = itemView.findViewById(R.id.itemDescription)
            
            fun bind(item: FoodItem, onItemClick: (FoodItem) -> Unit) {
                // Set image with proper category-based placeholder
                val foodGroup = item.category?.let { groupImg(it) } ?: R.drawable.logo
                Glide.with(imageView.context)
                    .load(item.imageURL)
                    .placeholder(foodGroup)
                    .into(imageView)

                titleView.text = item.name
                descView.text = "Food Group: ${item.category}"

                itemView.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }

    // DiffUtil callback for efficient list updates
    private class FoodItemDiffCallback : DiffUtil.ItemCallback<FoodItem>() {
        override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem == newItem
        }
    }

}