package com.example.greenpantry.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.greenpantry.R

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val suggestedPantryRecipes = view.findViewById<LinearLayout>(R.id.homeSuggestedPantryRecipesList)

        val items = listOf(
            Triple("Avocado Toast", "A healthy breakfast", R.drawable.ic_launcher_foreground),
            Triple("Quinoa Salad", "Protein-rich lunch", R.drawable.ic_launcher_foreground),
            Triple("Smoothie Bowl", "Energizing snack", R.drawable.ic_launcher_foreground)
        )

        val button = view.findViewById<Button>(R.id.homeSeeFullPantryBtn)
        button.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DetailsFragment())
                .addToBackStack(null) // Enables back navigation
                .commit()
        }

        for ((title, description, imageRes) in items) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.home_suggested_recipes_items, suggestedPantryRecipes, false)

            val imageView = itemView.findViewById<ImageView>(R.id.itemImage)
            val titleView = itemView.findViewById<TextView>(R.id.itemTitle)
            val descView = itemView.findViewById<TextView>(R.id.itemDescription)

            imageView.setImageResource(imageRes)
            titleView.text = title
            descView.text = description

            itemView.setOnClickListener {
                Toast.makeText(context, "$title clicked", Toast.LENGTH_SHORT).show()
            }

            suggestedPantryRecipes.addView(itemView)
        }
    }

}