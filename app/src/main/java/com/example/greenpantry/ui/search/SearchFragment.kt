package com.example.greenpantry.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.greenpantry.R
import com.example.greenpantry.data.database.PantryItem
import com.example.greenpantry.data.database.PantryItemDatabase
import com.example.greenpantry.data.database.RecipeDatabase
import com.example.greenpantry.ui.home.ItemDetailFragment
import com.example.greenpantry.ui.home.RecipeDetailFragment
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.example.greenpantry.ui.sharedcomponents.resetNav
import com.example.greenpantry.ui.sharedcomponents.setupNotifBtn
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import android.text.TextWatcher
import android.text.Editable

class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var allItems: List<PantryItem>
    private lateinit var searchItemsContainer: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ensure search icon is on, instead of camera if it was redirected
        resetNav(view, 2)

        setupNotifBtn(view)

        // search text value
        val inputField = view.findViewById<EditText>(R.id.searchInput)
        val userInput = inputField.text.toString()

        // search list
        searchItemsContainer = view.findViewById(R.id.searchList)


        val db = PantryItemDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            //REMOVE THIS BLOCK WHEN ALL THE WORKS ARE DONE
            //db.pantryItemDao().deleteAllPantryItems()
            //remove this block later, this is only for testing
            //val checkItems = db.pantryItemDao().getAllPantryItems()

            allItems = db.pantryItemDao().getAllPantryItems() // this should be change to item database
            filterAndDisplayItems(inputField.text.toString(), allItems, searchItemsContainer)

            inputField.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    filterAndDisplayItems(s.toString(), allItems, searchItemsContainer)
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun filterAndDisplayItems(
        query: String,
        allItems: List<PantryItem>,
        container: LinearLayout
    ) {
        container.removeAllViews()

        val filteredItems = if (query.isEmpty()) {
            allItems
        } else {
            allItems.filter { it.name.contains(query, ignoreCase = true) }
        }

        for (item in filteredItems) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.recipes_items, container, false)

            val imageView = itemView.findViewById<ImageView>(R.id.itemImage)
            val titleView = itemView.findViewById<TextView>(R.id.itemTitle)
            val descView = itemView.findViewById<TextView>(R.id.itemDescription)

            imageView.setImageResource(item.imageResId)
            titleView.text = item.name
            descView.text = item.description

            itemView.setOnClickListener {
                val itemDetailFragment = ItemDetailFragment.newInstance(item.name)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, itemDetailFragment)
                    .addToBackStack(null)
                    .commit()
            }

            container.addView(itemView)
        }
    }

}