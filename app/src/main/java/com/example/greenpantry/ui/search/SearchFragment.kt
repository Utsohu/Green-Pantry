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
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ensure search icon is on, instead of camera if it was redirected
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.menu.setGroupCheckable(0, true, false)
        for (i in 0 until bottomNav.menu.size()) {
            bottomNav.menu.getItem(i).isChecked = false
        }
        bottomNav.menu.getItem(2).isChecked = true // change search to on
        bottomNav.menu.setGroupCheckable(0, true, true)

        val notifBtn = view.findViewById<ImageButton>(R.id.notificationButton)
        notifBtn.setOnClickListener {
            // go to notification fragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NotificationsFragment())
                .commit()
        }

        // search text value
        val inputField = view.findViewById<EditText>(R.id.searchInput)
        val userInput = inputField.text.toString()

        // search list
        val searchItems = view.findViewById<LinearLayout>(R.id.searchList)

        // this list should be scrollable with all the items available in the app
        val items =  mutableListOf<PantryItem>(
            PantryItem(name="Romaine Lettuce", description="Serving Size: 200g", imageResId=R.drawable.ic_launcher_background),
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
            //remove this block later, this is only for testing
            val checkItems = db.pantryItemDao().getAllPantryItems()
            if(checkItems.isEmpty()){
                db.pantryItemDao().insertAll(items)
            }
            val pantry_items = db.pantryItemDao().getAllPantryItems()
            for (item in pantry_items) {
                val itemView =
                    LayoutInflater.from(context).inflate(R.layout.recipes_items, searchItems, false)

                val imageView = itemView.findViewById<ImageView>(R.id.itemImage)
                val titleView = itemView.findViewById<TextView>(R.id.itemTitle)
                val descView = itemView.findViewById<TextView>(R.id.itemDescription)

                imageView.setImageResource(item.imageResId)
                titleView.text = item.name
                descView.text = item.description

                itemView.setOnClickListener {
                    // go to item detail fragment
                    //Toast.makeText(context, "${item.name} clicked", Toast.LENGTH_SHORT).show()

                    // Create an instance of the new fragment, passing the recipe title
                    val itemDetailFragment = ItemDetailFragment.newInstance(item.name)

                    parentFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragment_container,
                            itemDetailFragment
                        ) // R.id.fragment_container is your main container
                        .addToBackStack(null) // Allows users to navigate back
                        .commit()
                }

                searchItems.addView(itemView)
            }
        }
    }
}