package com.example.greenpantry.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenpantry.R
import com.example.greenpantry.data.database.PantryItem
import com.example.greenpantry.data.database.PantryItemDatabase
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.example.greenpantry.ui.utils.ListOptimizations
import com.example.greenpantry.ui.sharedcomponents.popBack
import kotlinx.coroutines.launch
import android.text.TextWatcher
import android.text.Editable
import android.util.Log
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.bumptech.glide.Glide
import com.example.greenpantry.ui.sharedcomponents.groupImg

class DetailsFragment : Fragment(R.layout.fragment_pantry) {
    private var allItems: List<PantryItem> = emptyList()
    private lateinit var pantryDB : PantryItemDatabase
    private var searchJob: Job? = null
    private var hasLoadedData = false
    private val filterViewModel: FilterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pantryDB = PantryItemDatabase.getDatabase(requireContext())

        val backText: TextView = view.findViewById(R.id.pantryBack)
        popBack(backText)

        // search text value
        val inputField = view.findViewById<EditText>(R.id.searchInput)

        // filter button
        val filterBtn = view.findViewById<ImageButton>(R.id.filterButton)
        filterBtn.setOnClickListener {
            // filter popup
            FilterFragment().show(parentFragmentManager, "custom_dialog")
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.pantryGrid)
        val emptyPantry = view.findViewById<LinearLayout>(R.id.emptyPantry)

        setupRecyclerView(recyclerView)
        loadInitialData(inputField, recyclerView, emptyPantry)
        setupSearchFunctionality(inputField, recyclerView, emptyPantry)
        setupFragmentResultListener(inputField, recyclerView, emptyPantry)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        val itemWidthDp = 135
        val displayMetrics = resources.displayMetrics
        val itemWidthPx = (itemWidthDp * displayMetrics.density).toInt()
        val spanCount = (displayMetrics.widthPixels / itemWidthPx).coerceAtLeast(1)

        val layoutManager = GridLayoutManager(requireContext(), spanCount).apply {
            isItemPrefetchEnabled = true
            initialPrefetchItemCount = 4
        }

        recyclerView.apply {
            setLayoutManager(layoutManager)
            setHasFixedSize(true)
            setItemViewCacheSize(30)
            recycledViewPool.setMaxRecycledViews(0, 40)
            isNestedScrollingEnabled = true

            // Add item animator optimizations
            itemAnimator?.apply {
                addDuration = 0
                changeDuration = 0
                moveDuration = 0
                removeDuration = 0
            }

            // Apply performance optimizations
            ListOptimizations.optimizeList(this)
        }
    }

    private fun loadInitialData(inputField: EditText, recyclerView: RecyclerView, emptyPantry: LinearLayout) {
        // Only load data once to avoid repeated database calls
        if (hasLoadedData) {
            filterAndDisplayItems(inputField.text.toString(), recyclerView, emptyPantry)
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                allItems = pantryDB.pantryItemDao().getAllItemsWithNonZeroQuantity()
                hasLoadedData = true
                filterAndDisplayItems(inputField.text.toString(), recyclerView, emptyPantry)
            } catch (e: Exception) {
                // Handle error case
                allItems = emptyList()
                hasLoadedData = true
                filterAndDisplayItems("", recyclerView, emptyPantry)
            }
        }
    }

    private fun setupSearchFunctionality(inputField: EditText, recyclerView: RecyclerView, emptyPantry: LinearLayout) {
        inputField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Debounce search to avoid excessive filtering
                searchJob?.cancel()
                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(150) // Wait 150ms before executing search
                    filterAndDisplayItems(s.toString(), recyclerView, emptyPantry)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFragmentResultListener(inputField: EditText, recyclerView: RecyclerView, emptyPantry: LinearLayout) {
        // if updated, reload
        parentFragmentManager.setFragmentResultListener("edit_item_result", viewLifecycleOwner) { _, bundle ->
            val wasUpdated = bundle.getBoolean("updated", false)
            if (wasUpdated) {
                Log.d("PantryFragment","updating")
                // Force reload data from database
                hasLoadedData = false
                loadInitialData(inputField, recyclerView, emptyPantry)
            }
        }

        // want to update visible items when a filter is changed
        parentFragmentManager.setFragmentResultListener("filter_changed", viewLifecycleOwner) { _, bundle ->
            val wasUpdated = bundle.getBoolean("updated", false)
            if (wasUpdated) {
                filterAndDisplayItems(inputField.text.toString(), recyclerView, emptyPantry)
            }
        }
    }

    // reset the filters to off when view is closed
    override fun onDestroyView() {
        super.onDestroyView()

        val viewModel: FilterViewModel by activityViewModels()
        viewModel.vege.value = false
        viewModel.fruit.value = false
        viewModel.prot.value = false
        viewModel.dairy.value = false
        viewModel.grain.value = false
        viewModel.oth.value = false
    }

    private fun filterAndDisplayItems(
        query: String,
        recyclerView: RecyclerView,
        emptyPantry: LinearLayout
    ) {
        val activeCategories = mutableSetOf<String>()
        if (filterViewModel.vege.value == true) activeCategories.add("VEGETABLE")
        if (filterViewModel.fruit.value == true) activeCategories.add("FRUIT")
        if (filterViewModel.prot.value == true) activeCategories.add("PROTEIN")
        if (filterViewModel.grain.value == true) activeCategories.add("GRAIN")
        if (filterViewModel.dairy.value == true) activeCategories.add("DAIRY")
        if (filterViewModel.oth.value == true) activeCategories.add("OTHER")

        val filteredItems = allItems.filter {
            val matchesQuery = it.name.contains(query, ignoreCase = true)
            val matchesCategory = activeCategories.isEmpty() || activeCategories.contains(it.category)
            matchesQuery && matchesCategory
        }

        if (filteredItems.isEmpty()) {
            recyclerView.visibility = View.INVISIBLE
            emptyPantry.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyPantry.visibility = View.GONE

            // Check if adapter exists and update, otherwise create new one
            val adapter = recyclerView.adapter as? DetailItemAdapter
            if (adapter != null) {
                adapter.updateItems(filteredItems)
            } else {
                recyclerView.adapter = DetailItemAdapter(filteredItems, this@DetailsFragment)
            }
        }
    }

    private fun refreshPantry(inputField: EditText, recyclerView: RecyclerView, emptyPantry: LinearLayout) {
        // This method is now handled by loadInitialData with caching
        hasLoadedData = false
        loadInitialData(inputField, recyclerView, emptyPantry)
    }
}

class DetailItemAdapter(private var items: List<PantryItem>, private val fragment: Fragment) :
    RecyclerView.Adapter<DetailItemAdapter.DetailViewHolder>() {

    class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById<ImageView>(R.id.item_image)
        val label: TextView = view.findViewById<TextView>(R.id.item_label)
        val amount: TextView = view.findViewById<TextView>(R.id.item_amount)
        val editBtn: ImageButton = view.findViewById<ImageButton>(R.id.item_edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pantry_grid_items, parent, false)
        return DetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val item = items[position]

        // Use direct property access for better performance
        with(holder) {
            label.text = item.name
            amount.text = item.curNum.toString()

            // Optimize image loading to prevent memory issues
            try {
                var foodGroup = item.category?.let { groupImg(it) }
                if (foodGroup == null) {
                    foodGroup = R.drawable.logo
                }
                Glide.with(image.context)
                    .load(item.imageURL)
                    .placeholder(foodGroup)
                    .into(image)
            } catch (e: Exception) {
                // Fallback to default image if resource loading fails
                image.setImageResource(R.drawable.ic_launcher_foreground)
            }

            // Remove and re-set listener to avoid memory leaks
            editBtn.setOnClickListener(null)
            editBtn.setOnClickListener {
                EditItemFragment.newInstance(item.name, "UPDATE")
                    .show(fragment.parentFragmentManager, "custom_dialog")
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<PantryItem>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = items.size
            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition].name == newItems[newItemPosition].name
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition] == newItems[newItemPosition]
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }
}


