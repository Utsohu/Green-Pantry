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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenpantry.R
import com.example.greenpantry.data.database.PantryItem
import com.example.greenpantry.data.database.PantryItemDatabase
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.example.greenpantry.ui.sharedcomponents.popBack
import com.example.greenpantry.ui.sharedcomponents.setupNotifBtn
import kotlinx.coroutines.launch
import android.text.TextWatcher
import android.text.Editable
import android.util.Log


class DetailsFragment : Fragment(R.layout.fragment_pantry) {
    private lateinit var allItems: List<PantryItem>
    private lateinit var pantryDB : PantryItemDatabase
    private val filterViewModel: FilterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pantryDB = PantryItemDatabase.getDatabase(requireContext())

        val backText: TextView = view.findViewById(R.id.pantryBack)
        popBack(backText)
        setupNotifBtn(view)

        // search text value
        val inputField = view.findViewById<EditText>(R.id.searchInput)
        val userInput = inputField.text.toString()

        // filter button
        val filterBtn = view.findViewById<ImageButton>(R.id.filterButton)
        filterBtn.setOnClickListener {
            // filter popup
            FilterFragment().show(parentFragmentManager, "custom_dialog")
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.pantryGrid)
        val emptyPantry = view.findViewById<LinearLayout>(R.id.emptyPantry)

        inputField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAndDisplayItems(s.toString(), recyclerView, emptyPantry)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        val itemWidthDp = 135
        val displayMetrics = resources.displayMetrics
        val itemWidthPx = (itemWidthDp * displayMetrics.density).toInt()

        val spanCount = (displayMetrics.widthPixels / itemWidthPx).coerceAtLeast(1)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)

        // first load
        refreshPantry(inputField,recyclerView,emptyPantry)

        // if updated, reload
        parentFragmentManager.setFragmentResultListener("edit_item_result", viewLifecycleOwner) { _, bundle ->
            val wasUpdated = bundle.getBoolean("updated", false)
            if (wasUpdated) {
                Log.d("PantryFragment","updating")
                refreshPantry(inputField,recyclerView,emptyPantry)
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
            recyclerView.adapter = DetailItemAdapter(filteredItems, this@DetailsFragment)
        }
    }

    private fun refreshPantry(inputField: EditText, recyclerView: RecyclerView, emptyPantry: LinearLayout) {
        viewLifecycleOwner.lifecycleScope.launch {
            allItems = pantryDB.pantryItemDao().getAllItemsWithNonZeroQuantity()
            filterAndDisplayItems(inputField.text.toString(), recyclerView, emptyPantry)
        }
    }
}

class DetailItemAdapter(private val items: List<PantryItem>, private val fragment: Fragment) :
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
        holder.label.text = items[position].name
        holder.amount.text = items[position].curNum.toString()
        holder.image.setImageResource(items[position].imageResId)

        // Set image or listeners on buttons here if needed
        val itemName = holder.label.text
        holder.editBtn.setOnClickListener {
            // item edit popup
            EditItemFragment.newInstance(itemName.toString(),"UPDATE").show(fragment.parentFragmentManager, "custom_dialog")
        }
    }

    override fun getItemCount(): Int = items.size
}


