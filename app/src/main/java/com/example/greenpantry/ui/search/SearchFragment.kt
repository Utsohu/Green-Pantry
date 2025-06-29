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

        // this list should be scrollable with all the items available in the app
        val items =  mutableListOf<PantryItem>(
            PantryItem(name="Romaine Lettuce", description="Serving Size: 200g", imageResId=R.drawable.ic_launcher_background, calories = 1234, fiber = 123, totalFat = 434, sugars = 123, transFat = 777, protein = 32, sodium = 23, iron = 2, calcium = 2, vitaminD = 2),
            PantryItem(name="Iceberg Lettuce", description="Serving Size: 180g", imageResId=R.drawable.ic_launcher_background),
            PantryItem(name="Butter Lettuce", description="Serving Size: 140g", imageResId=R.drawable.ic_launcher_background),
            PantryItem(name="Kale", description="Serving Size: 200g", imageResId=R.drawable.ic_launcher_background),
            PantryItem(name="Spinach", description="Serving Size: 250g", imageResId=R.drawable.ic_launcher_background),
            PantryItem(name="Cabbage", description="Serving Size: 140g", imageResId=R.drawable.ic_launcher_background),
            PantryItem(name="Yu Choy", description="Serving Size: 220g", imageResId=R.drawable.ic_launcher_background),
            PantryItem(name="Bok Choy", description="Serving Size: 1800g", imageResId=R.drawable.ic_launcher_background)
        )

        val db = PantryItemDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            //REMOVE THIS BLOCK WHEN ALL THE WORKS ARE DONE
            db.pantryItemDao().deleteAllPantryItems()
            //remove this block later, this is only for testing
            val checkItems = db.pantryItemDao().getAllPantryItems()
            if(checkItems.isEmpty()){
                db.pantryItemDao().insertAll(items)
            }
            allItems = db.pantryItemDao().getAllPantryItems()

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