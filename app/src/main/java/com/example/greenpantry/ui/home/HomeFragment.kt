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
import androidx.lifecycle.lifecycleScope
import com.example.greenpantry.R
import com.example.greenpantry.data.database.Recipe
import com.example.greenpantry.data.database.RecipeDatabase
import com.example.greenpantry.ui.home.RecipeDetailFragment
import kotlinx.coroutines.launch
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

        val button = view.findViewById<Button>(R.id.homeSeeFullPantryBtn)
        button.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DetailsFragment())
                .addToBackStack(null) // Enables back navigation
                .commit()
        }

        val suggestedPantryRecipes =
            view.findViewById<LinearLayout>(R.id.homeSuggestedPantryRecipesList)

        val items = listOf(
            Recipe(
                name = "Avocado Toast",
                description = "A healthy breakfast",
                imageResId = R.drawable.ic_launcher_foreground
            ),
            Recipe(
                name = "Quinoa Salad",
                description = "Protein-rich lunch",
                imageResId = R.drawable.ic_launcher_foreground
            ),
            Recipe(
                name = "Smoothie Bowl",
                description = "Energizing snack",
                imageResId = R.drawable.ic_launcher_foreground
            )
        )

        val db = RecipeDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            //db.recipeDao().insertAll(items)
            val allRecipes = db.recipeDao().getAllRecipes()
            for (recipe in allRecipes) {
                val itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recipes_items, suggestedPantryRecipes, false)

                val imageView = itemView.findViewById<ImageView>(R.id.itemImage)
                val titleView = itemView.findViewById<TextView>(R.id.itemTitle)
                val descView = itemView.findViewById<TextView>(R.id.itemDescription)

                imageView.setImageResource(recipe.imageResId)
                titleView.text = recipe.name
                descView.text = recipe.description

                itemView.setOnClickListener {
                    // Toast.makeText(context, "$title clicked", Toast.LENGTH_SHORT).show()
                    if (recipe.name == "Avocado Toast") {
                        // Create an instance of the new fragment, passing the recipe title
                        val recipeDetailFragment = RecipeDetailFragment.newInstance(recipe.name)

                        parentFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragment_container,
                                recipeDetailFragment
                            ) // R.id.fragment_container is your main container
                            .addToBackStack(null) // Allows users to navigate back
                            .commit()
                    } else {
                        Toast.makeText(context, "${recipe.name} clicked", Toast.LENGTH_SHORT).show()
                    }
                }

                suggestedPantryRecipes.addView(itemView)
            }
        }
    }
}