package com.example.greenpantry.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.greenpantry.R
import com.example.greenpantry.ui.notifs.NotificationsFragment

class HomeFragment : Fragment(R.layout.fragment_home) {
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

        val suggestedPantryRecipes = view.findViewById<LinearLayout>(R.id.homeSuggestedPantryRecipesList)

        val items =  mutableListOf<Triple<String, String, Int>>(
            Triple("Avocado Toast", "Time: 5min\nDifficulty: 1/10", R.drawable.ic_launcher_foreground),
            Triple("Quinoa Salad", "Time: 20min\nDifficulty:3/10", R.drawable.ic_launcher_foreground),
            Triple("Smoothie Bowl", "Time: 20min\nDifficulty:2/10", R.drawable.ic_launcher_foreground)
        )

        val button = view.findViewById<Button>(R.id.homeSeeFullPantryBtn)
        button.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DetailsFragment())
                .addToBackStack(null) // Enables back navigation
                .commit()
        }

        for ((title, description, imageRes) in items) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.recipes_items, suggestedPantryRecipes, false)

            val imageView = itemView.findViewById<ImageView>(R.id.itemImage)
            val titleView = itemView.findViewById<TextView>(R.id.itemTitle)
            val descView = itemView.findViewById<TextView>(R.id.itemDescription)

            imageView.setImageResource(imageRes)
            titleView.text = title
            descView.text = description

            itemView.setOnClickListener {
                 // Toast.makeText(context, "$title clicked", Toast.LENGTH_SHORT).show()
                if (title == "Avocado Toast") {
                    // Create an instance of the new fragment, passing the recipe title
                    val recipeDetailFragment = RecipeDetailFragment.newInstance(title)

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, recipeDetailFragment) // R.id.fragment_container is your main container
                        .addToBackStack(null) // Allows users to navigate back
                        .commit()
                } else {
                    Toast.makeText(context, "$title clicked", Toast.LENGTH_SHORT).show()
                }
            }

            suggestedPantryRecipes.addView(itemView)
        }
    }

}