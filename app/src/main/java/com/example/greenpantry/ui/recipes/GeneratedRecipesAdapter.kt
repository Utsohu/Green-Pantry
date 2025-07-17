package com.example.greenpantry.ui.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.greenpantry.R
import com.example.greenpantry.data.model.GeneratedRecipe

class GeneratedRecipesAdapter(
    private val onDetailsClick: (GeneratedRecipe) -> Unit,
    private val onSaveClick: (GeneratedRecipe) -> Unit
) : ListAdapter<GeneratedRecipe, GeneratedRecipesAdapter.RecipeViewHolder>(RecipeDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_generated_recipe, parent, false)
        return RecipeViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRecipeName: TextView = itemView.findViewById(R.id.tvRecipeName)
        private val tvRecipeDescription: TextView = itemView.findViewById(R.id.tvRecipeDescription)
        private val tvCookingTime: TextView = itemView.findViewById(R.id.tvCookingTime)
        private val tvDifficulty: TextView = itemView.findViewById(R.id.tvDifficulty)
        private val tvServings: TextView = itemView.findViewById(R.id.tvServings)
        private val btnViewDetails: Button = itemView.findViewById(R.id.btnViewDetails)
        private val btnSaveRecipe: Button = itemView.findViewById(R.id.btnSaveRecipe)
        
        fun bind(recipe: GeneratedRecipe) {
            tvRecipeName.text = recipe.name
            tvRecipeDescription.text = recipe.description
            tvCookingTime.text = "${recipe.time} min"
            tvDifficulty.text = getDifficultyText(recipe.difficulty)
            tvServings.text = "${recipe.servings} servings"
            
            btnViewDetails.setOnClickListener {
                onDetailsClick(recipe)
            }
            
            btnSaveRecipe.setOnClickListener {
                onSaveClick(recipe)
                btnSaveRecipe.isEnabled = false
                btnSaveRecipe.text = "Saved"
            }
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
    
    class RecipeDiffCallback : DiffUtil.ItemCallback<GeneratedRecipe>() {
        override fun areItemsTheSame(oldItem: GeneratedRecipe, newItem: GeneratedRecipe): Boolean {
            return oldItem.name == newItem.name
        }
        
        override fun areContentsTheSame(oldItem: GeneratedRecipe, newItem: GeneratedRecipe): Boolean {
            return oldItem == newItem
        }
    }
}