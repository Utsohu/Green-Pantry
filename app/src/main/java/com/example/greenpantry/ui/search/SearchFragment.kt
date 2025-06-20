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
import com.example.greenpantry.R
import com.example.greenpantry.ui.notifs.NotificationsFragment

class SearchFragment : Fragment(R.layout.fragment_search) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notifBtn = view.findViewById<ImageButton>(R.id.notificationButton)
        notifBtn.setOnClickListener {
            // go to notification fragment
            notifBtn.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, NotificationsFragment())
                    .commit()
            }
        }

        // search text value
        val inputField = view.findViewById<EditText>(R.id.searchInput)
        val userInput = inputField.text.toString()

        // search list
        val searchItems = view.findViewById<LinearLayout>(R.id.searchList)

        // this list should be scrollable with all the items available in the app
        val items =  mutableListOf<Triple<String, String, Int>>(
            Triple("Romaine Lettuce", "Serving Size: 200g", R.drawable.ic_launcher_foreground),
            Triple("Iceberg Lettuce", "Serving Size: 180g", R.drawable.ic_launcher_foreground),
            Triple("Butter Lettuce", "Serving Size: 140g", R.drawable.ic_launcher_foreground),
            Triple("Kale", "Serving Size: 200g", R.drawable.ic_launcher_foreground),
            Triple("Spinach", "Serving Size: 250g", R.drawable.ic_launcher_foreground),
            Triple("Cabbage", "Serving Size: 140g", R.drawable.ic_launcher_foreground),
            Triple("Yu Choy", "Serving Size: 220g", R.drawable.ic_launcher_foreground),
            Triple("Bok Choy", "Serving Size: 1800g", R.drawable.ic_launcher_foreground)
        )

        for ((title, description, imageRes) in items) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.recipes_items, searchItems, false)

            val imageView = itemView.findViewById<ImageView>(R.id.itemImage)
            val titleView = itemView.findViewById<TextView>(R.id.itemTitle)
            val descView = itemView.findViewById<TextView>(R.id.itemDescription)

            imageView.setImageResource(imageRes)
            titleView.text = title
            descView.text = description

            itemView.setOnClickListener {
                // go to recipe detail fragment
                Toast.makeText(context, "$title clicked", Toast.LENGTH_SHORT).show()
            }

            searchItems.addView(itemView)
        }
    }
}