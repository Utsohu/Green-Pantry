package com.example.greenpantry.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenpantry.R
import com.example.greenpantry.data.database.Recipe
import com.example.greenpantry.data.database.RecipeDatabase
import com.example.greenpantry.data.model.FoodCategory
import com.example.greenpantry.ui.home.RecipeDetailFragment
import com.example.greenpantry.ui.utils.ListOptimizations
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.example.greenpantry.ui.sharedcomponents.setupNotifBtn

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var recipesRecyclerView: RecyclerView
    private lateinit var recipesAdapter: RecipeAdapter
    private var hasLoadedInitialData = false
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNotifBtn(view)
        
        // Show loading state initially
        showLoadingState(view, true)

        // Set up UI elements
        val vegeCount = view.findViewById<TextView>(R.id.vegetableCount)
        val fruitCount = view.findViewById<TextView>(R.id.fruitCount)
        val proteinCount = view.findViewById<TextView>(R.id.proteinCount)
        val grainCount = view.findViewById<TextView>(R.id.grainCount)
        val dairyCount = view.findViewById<TextView>(R.id.dairyCount)
        val otherCount = view.findViewById<TextView>(R.id.otherCount)
        
        // Observe category counts from ViewModel - but only update when needed
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoryCounts.collect { counts ->
                if (counts.isNotEmpty()) {
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
                    
                    // Hide loading state after first data load
                    if (!hasLoadedInitialData) {
                        showLoadingState(view, false)
                        hasLoadedInitialData = true
                    }
                }
            }
        }

        val button = view.findViewById<Button>(R.id.homeSeeFullPantryBtn)
        button.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DetailsFragment())
                .addToBackStack(null) // Enables back navigation
                .commit()
        }

        // Set up RecyclerView for recipes
        recipesRecyclerView = view.findViewById(R.id.homeSuggestedPantryRecipesRecyclerView)
        recipesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            
            // Apply performance optimizations
            ListOptimizations.optimizeList(this)
        }

        val noSuggestions = view.findViewById<LinearLayout>(R.id.noSuggestions)
        
        // Load recipes asynchronously and cache them
        loadRecipes(view, noSuggestions)
    }
    
    private fun showLoadingState(view: View, isLoading: Boolean) {
        // You can add a loading indicator here if you have one in the layout
        // For now, we'll just ensure counts show 0 during loading
        if (isLoading) {
            view.findViewById<TextView>(R.id.vegetableCount).text = "..."
            view.findViewById<TextView>(R.id.fruitCount).text = "..."
            view.findViewById<TextView>(R.id.proteinCount).text = "..."
            view.findViewById<TextView>(R.id.grainCount).text = "..."
            view.findViewById<TextView>(R.id.dairyCount).text = "..."
            view.findViewById<TextView>(R.id.otherCount).text = "..."
        }
    }
    
    private fun loadRecipes(view: View, noSuggestions: LinearLayout) {
        // Only load recipes once per fragment instance to avoid repeated database calls
        if (::recipesAdapter.isInitialized) return
        
        val db = RecipeDatabase.getDatabase(requireContext())
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val items = db.recipeDao().getAllRecipes()
                
                if (items.isEmpty()) {
                    recipesRecyclerView.visibility = View.INVISIBLE
                    noSuggestions.visibility = View.VISIBLE
                } else {
                    recipesRecyclerView.visibility = View.VISIBLE
                    noSuggestions.visibility = View.INVISIBLE
                    
                    recipesAdapter = RecipeAdapter(items) { recipe ->
                        val recipeDetailFragment = RecipeDetailFragment.newInstance(recipe.name)
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, recipeDetailFragment)
                            .addToBackStack(null)
                            .commit()
                        Toast.makeText(context, "${recipe.name} clicked", Toast.LENGTH_SHORT).show()
                    }
                    
                    recipesRecyclerView.adapter = recipesAdapter
                }
            } catch (e: Exception) {
                // Handle error - show no suggestions or error message
                recipesRecyclerView.visibility = View.INVISIBLE
                noSuggestions.visibility = View.VISIBLE
            }
        }
    }
}

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val onRecipeClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    
    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemImage)
        val titleView: TextView = view.findViewById(R.id.itemTitle)
        val descView: TextView = view.findViewById(R.id.itemDescription)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipes_items, parent, false)
        return RecipeViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        
        holder.imageView.setImageResource(recipe.imageResId)
        holder.titleView.text = recipe.name
        holder.descView.text = recipe.description
        
        holder.itemView.setOnClickListener {
            onRecipeClick(recipe)
        }
    }
    
    override fun getItemCount(): Int = recipes.size
}