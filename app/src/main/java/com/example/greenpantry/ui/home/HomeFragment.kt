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
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NotificationsFragment())
                .commit()
        }
        // pantry overview - calculate based on pantry items
        val vegeAmt = 10
        val fruitAmt = 6
        val proteinAmt = 3
        val grainAmt = 2
        val dairyAmt = 25
        val otherAmt = 1

        // change displayed values
        val vegeCount = view.findViewById<TextView>(R.id.vegetableCount)
        vegeCount.text = vegeAmt.toString()
        val fruitCount = view.findViewById<TextView>(R.id.fruitCount)
        fruitCount.text = fruitAmt.toString()
        val proteinCount = view.findViewById<TextView>(R.id.proteinCount)
        proteinCount.text = proteinAmt.toString()
        val grainCount = view.findViewById<TextView>(R.id.grainCount)
        grainCount.text = grainAmt.toString()
        val dairyCount = view.findViewById<TextView>(R.id.dairyCount)
        dairyCount.text = dairyAmt.toString()
        val otherCount = view.findViewById<TextView>(R.id.otherCount)
        otherCount.text = otherAmt.toString()

        // recipes
        val suggestedPantryRecipes = view.findViewById<LinearLayout>(R.id.homeSuggestedPantryRecipesList)

        val items =  mutableListOf<Triple<String, String, Int>>(
            Triple("Avocado Toast", "Time: 5min\nDifficulty: 1/10", R.drawable.ic_launcher_background),
            Triple("Quinoa Salad", "Time: 20min\nDifficulty:3/10", R.drawable.ic_launcher_background),
            Triple("Smoothie Bowl", "Time: 20min\nDifficulty:2/10", R.drawable.ic_launcher_background)
        )

        val button = view.findViewById<Button>(R.id.homeSeeFullPantryBtn)
        button.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DetailsFragment())
                .addToBackStack(null) // Enables back navigation
                .commit()
        }

        // recipe list
        val recipeItems = view.findViewById<LinearLayout>(R.id.homeSuggestedPantryRecipesList)
        val noSuggestions = view.findViewById<LinearLayout>(R.id.noSuggestions)

        // recipe display
        if (items.isEmpty()) {  // display empty notif message
            recipeItems.visibility = View.INVISIBLE
            noSuggestions.visibility = View.VISIBLE
        } else {
            recipeItems.visibility = View.VISIBLE
            noSuggestions.visibility = View.INVISIBLE
            for ((title, description, imageRes) in items) {
                val itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recipes_items, suggestedPantryRecipes, false)

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
                            .replace(
                                R.id.fragment_container,
                                recipeDetailFragment
                            ) // R.id.fragment_container is your main container
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

}