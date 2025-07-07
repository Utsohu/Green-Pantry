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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.greenpantry.R
import com.example.greenpantry.data.database.Recipe
import com.example.greenpantry.data.database.RecipeDatabase
import com.example.greenpantry.data.model.FoodCategory
import com.example.greenpantry.ui.home.RecipeDetailFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.example.greenpantry.ui.sharedcomponents.setupNotifBtn

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    
    private val viewModel: HomeViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNotifBtn(view)
        
        // Set up UI elements
        val vegeCount = view.findViewById<TextView>(R.id.vegetableCount)
        val fruitCount = view.findViewById<TextView>(R.id.fruitCount)
        val proteinCount = view.findViewById<TextView>(R.id.proteinCount)
        val grainCount = view.findViewById<TextView>(R.id.grainCount)
        val dairyCount = view.findViewById<TextView>(R.id.dairyCount)
        val otherCount = view.findViewById<TextView>(R.id.otherCount)
        
        // Observe category counts from ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoryCounts.collect { counts ->
                vegeCount.text = (counts[FoodCategory.VEGETABLE.name] ?: 0).toString()
                fruitCount.text = (counts[FoodCategory.FRUIT.name] ?: 0).toString()
                proteinCount.text = (counts[FoodCategory.PROTEIN.name] ?: 0).toString()
                grainCount.text = (counts[FoodCategory.GRAIN.name] ?: 0).toString()
                dairyCount.text = (counts[FoodCategory.DAIRY.name] ?: 0).toString()
                
                // Sum up all other categories (SNACK, BEVERAGE, CONDIMENT, OTHER)
                val otherTotal = (counts[FoodCategory.SNACK.name] ?: 0) +
                        (counts[FoodCategory.BEVERAGE.name] ?: 0) +
                        (counts[FoodCategory.CONDIMENT.name] ?: 0) +
                        (counts[FoodCategory.OTHER.name] ?: 0)
                otherCount.text = otherTotal.toString()
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
                imageResId = R.drawable.ic_launcher_foreground,
                time = 50, difficulty = 7, NOS = 2,
                calories = 120, fiber = 5, totalFat = 8, sugars = 2,
                transFat = 5, protein = 2, sodium = 2, iron = 3,
                calcium = 4, vitaminD = 1,
                setUpInstructions = mutableListOf("Follow the instructions and try to make a good meal!","Go out to restaurant.", "Buy the food!"),
                ingredients = mutableListOf("Romaine Lettuce", "Kale", "Yu Choy", "Apple")
            ),
            Recipe(
                name = "Quinoa Salad",
                description = "Protein-rich lunch",
                imageResId = R.drawable.ic_launcher_foreground,
                ingredients = mutableListOf("Yu Choy", "Apple", "Tomato")
            ),
            Recipe(
                name = "Smoothie Bowl",
                description = "Energizing snack",
                imageResId = R.drawable.ic_launcher_foreground
            )
        )

        val recipeItems = view.findViewById<LinearLayout>(R.id.homeSuggestedPantryRecipesList)
        val noSuggestions = view.findViewById<LinearLayout>(R.id.noSuggestions)
        val db = RecipeDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            //REMOVE THIS AFTER DEMO
            db.recipeDao().deleteAllRecipes()
            if(db.recipeDao().getAllRecipes().isEmpty()){
                db.recipeDao().insertAll(items)
            }
            val items = db.recipeDao().getAllRecipes()
            // recipe display
            if (items.isEmpty()) {  // display empty notif message
                recipeItems.visibility = View.INVISIBLE
                noSuggestions.visibility = View.VISIBLE
            } else {
                recipeItems.visibility = View.VISIBLE
                noSuggestions.visibility = View.INVISIBLE
                for (recipe in items) {
                    val itemView = LayoutInflater.from(context)
                        .inflate(R.layout.recipes_items, suggestedPantryRecipes, false)

                    val imageView = itemView.findViewById<ImageView>(R.id.itemImage)
                    val titleView = itemView.findViewById<TextView>(R.id.itemTitle)
                    val descView = itemView.findViewById<TextView>(R.id.itemDescription)

                    imageView.setImageResource(recipe.imageResId)
                    titleView.text = recipe.name
                    descView.text = recipe.description

                    itemView.setOnClickListener {
                        // Create an instance of the new fragment, passing the recipe title
                        val recipeDetailFragment = RecipeDetailFragment.newInstance(recipe.name)

                        parentFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragment_container,
                                recipeDetailFragment
                            ) // R.id.fragment_container is your main container
                            .addToBackStack(null) // Allows users to navigate back
                            .commit()
                        Toast.makeText(context, "${recipe.name} clicked", Toast.LENGTH_SHORT)
                            .show()
                    }

                    suggestedPantryRecipes.addView(itemView)
                }
            }
        }
    }
}