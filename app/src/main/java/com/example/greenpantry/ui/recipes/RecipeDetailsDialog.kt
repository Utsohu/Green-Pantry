package com.example.greenpantry.ui.recipes

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.greenpantry.R
import com.example.greenpantry.data.model.GeneratedRecipe
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RecipeDetailsDialog : DialogFragment() {
    
    private var recipe: GeneratedRecipe? = null
    
    companion object {
        private const val ARG_RECIPE = "recipe"
        
        fun newInstance(recipe: GeneratedRecipe): RecipeDetailsDialog {
            return RecipeDetailsDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_RECIPE, recipe)
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipe = arguments?.getSerializable(ARG_RECIPE) as? GeneratedRecipe
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val currentRecipe = recipe ?: run {
            dismiss()
            return super.onCreateDialog(savedInstanceState)
        }
        
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_recipe_details, null)
        
        // Populate the view with recipe details
        view.findViewById<TextView>(R.id.tvRecipeName).text = currentRecipe.name
        view.findViewById<TextView>(R.id.tvDescription).text = currentRecipe.description
        view.findViewById<TextView>(R.id.tvCookingTime).text = "Cooking time: ${currentRecipe.time} minutes"
        view.findViewById<TextView>(R.id.tvDifficulty).text = "Difficulty: ${getDifficultyText(currentRecipe.difficulty)}"
        view.findViewById<TextView>(R.id.tvServings).text = "Servings: ${currentRecipe.servings}"
        
        // Ingredients
        val ingredientsText = currentRecipe.ingredients.joinToString("\n") { "• $it" }
        view.findViewById<TextView>(R.id.tvIngredients).text = ingredientsText
        
        // Instructions
        val instructionsText = currentRecipe.instructions.mapIndexed { index, instruction ->
            "${index + 1}. $instruction"
        }.joinToString("\n\n")
        view.findViewById<TextView>(R.id.tvInstructions).text = instructionsText
        
        // Nutrition
        val nutritionText = """
            Calories: ${currentRecipe.nutrition.calories}
            Protein: ${currentRecipe.nutrition.protein}g
            Carbs: ${currentRecipe.nutrition.carbs}g
            Fat: ${currentRecipe.nutrition.fat}g
            Fiber: ${currentRecipe.nutrition.fiber}g
        """.trimIndent()
        view.findViewById<TextView>(R.id.tvNutrition).text = nutritionText
        
        return MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .setPositiveButton("Close") { _, _ -> dismiss() }
            .create()
    }
    
    private fun getDifficultyText(difficulty: Int): String {
        return when (difficulty) {
            1 -> "Easy"
            2 -> "Easy-Medium"
            3 -> "Medium"
            4 -> "Medium-Hard"
            5 -> "Hard"
            else -> "Medium"
        }
    }
}