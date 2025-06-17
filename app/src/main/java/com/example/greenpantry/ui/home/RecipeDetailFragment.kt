package com.example.greenpantry.ui.home // Or your relevant package

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import com.example.greenpantry.R

class RecipeDetailFragment : Fragment() {

    private var recipeName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipeName = it.getString(ARG_RECIPE_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recipe_detail_fragement, container, false)

        // Retrieve the recipe name passed as an argument
        val recipeName = arguments?.getString(ARG_RECIPE_NAME) // Assuming ARG_RECIPE_NAME is your bundle key

        // Find the TextView by its NEW ID from fragment_recipe_detail.xml
        val titleTextView = view.findViewById<TextView>(R.id.recipeDetailTitle) // Use the new ID here

        // Set the text of this TextView
        titleTextView.text = recipeName ?: "Recipe Details" // Provide a default if recipeName is null

        return view
    }

    companion object {
        private const val ARG_RECIPE_NAME = "recipe_name"

        @JvmStatic
        fun newInstance(recipeName: String) =
            RecipeDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_RECIPE_NAME, recipeName)
                }
            }
    }
}