package com.example.greenpantry.ui.home // Or your relevant package

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.greenpantry.R
import com.example.greenpantry.data.database.FoodItemDatabase
import com.example.greenpantry.data.database.PantryItemDatabase
import com.example.greenpantry.data.database.RecipeDatabase
import com.example.greenpantry.ui.search.SearchFragment
import com.example.greenpantry.ui.sharedcomponents.groupImg
import com.example.greenpantry.ui.sharedcomponents.itemImageSetup
import com.example.greenpantry.ui.sharedcomponents.popBack
import com.example.greenpantry.ui.sharedcomponents.setNutrition
import kotlinx.coroutines.launch
import java.io.IOException

class ItemDetailFragment : Fragment() {

    private var itemName: String? = null
    private lateinit var foodDB : FoodItemDatabase
    private lateinit var recipeDB : RecipeDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemName = it.getString(ARG_ITEM_NAME)
        }
        foodDB = FoodItemDatabase.getDatabase(requireContext())
        recipeDB = RecipeDatabase.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_item_detail, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        parentFragmentManager.setFragmentResultListener("edit_item_result", this) { _, bundle ->
            val updated = bundle.getBoolean("updated", false)
            if (updated) {
                refreshItemDetails()
            }
        }*/

        // set the back and notif button
        val backText: TextView = view.findViewById(R.id.itemBack)
        popBack(backText)

        // Retrieve the recipe name passed as an argument
        val itemName = arguments?.getString(ARG_ITEM_NAME) // Assuming ARG_ITEM_NAME is your bundle key

        // Find the TextView by its NEW ID from fragment_item_detail.xml
        val titleTextView = view.findViewById<TextView>(R.id.itemDetailTitle)

        // Set the text of this TextView
        titleTextView.text = itemName ?: "Item Details" // Provide a default if itemName is null


        itemName?.let { name ->
            viewLifecycleOwner.lifecycleScope.launch {
                val item = foodDB.foodItemDao().getFoodItemByName(name)
                if(item != null){
                    setNutrition(view, item.calories, item.fiber, item.totFat,
                        item.sugars, item.transFat, item.protein,
                        item.sodium, item.iron, item.calcium, item.carbs)

                    // update serving size
                    val servingAmt = view.findViewById<TextView>(R.id.servingAmt)
                    servingAmt.text = item.servingSize

                    // update image
                    val itemImage = view.findViewById<ImageView>(R.id.itemImage)
                    itemImageSetup(item, null, itemImage)
                } else {
                    // fallback dummy values
                    setNutrition(view, 165, 1, 4,
                        1, 1, 31,
                        0, 1, 1, 1)
                }
            }
        }?: run {
            // itemName is null or not a valid string
            setNutrition(view, 165, 1, 4,
                1, 1, 31, 0,
                1, 1, 1)
        }

        // dummy recipe list
        data class Recipe(
            val name: String,
            val description: String,
            val imageResId: Int
        )

        val recipeItems = view.findViewById<LinearLayout>(R.id.ItemRecipesList)
        lifecycleScope.launch {
            // recipe display
            val items = recipeDB.recipeDao().getRecipesByIngredient(itemName?:"")

            // only show the ones related to the item and limit to 10 recipes
            val itemRecipes = mutableListOf<com.example.greenpantry.data.database.Recipe>()
            for (recipe in items) {
                if (recipe.ingredients.any { it.contains(itemName ?: "", ignoreCase = true) }) {
                    itemRecipes.add(recipe)
                }

                if (itemRecipes.size >= 10) break
            }

            // if itemRecipe is empty, hide the recipe option
            val recipeTitle = view.findViewById<TextView>(R.id.itemRecipeTitle)
            if (itemRecipes.size == 0) {
                recipeTitle.visibility = View.INVISIBLE
            } else { // not empty, so show recipes
                recipeTitle.visibility = View.VISIBLE

                for (recipe in itemRecipes) { // inflate
                    val recipeView = LayoutInflater.from(context)
                        .inflate(R.layout.recipes_items, recipeItems, false)

                    val imageView = recipeView.findViewById<ImageView>(R.id.itemImage)
                    val titleView = recipeView.findViewById<TextView>(R.id.itemTitle)
                    val descView = recipeView.findViewById<TextView>(R.id.itemDescription)

                    if (recipe.imageName.isNotBlank()) {
                        try {
                            val inputStream = context?.assets?.open("recipeImages/${recipe.imageName}.jpg")
                            if (inputStream != null) {
                                val drawable = Drawable.createFromStream(inputStream, null)
                                imageView.setImageDrawable(drawable)
                            } else {
                                imageView.setImageResource(recipe.imageResId)
                            }
                        } catch (e: IOException) {
                            // Asset image not found, fallback
                            imageView.setImageResource(recipe.imageResId)
                        }
                    } else {
                        imageView.setImageResource(recipe.imageResId)
                    }

                    titleView.text = recipe.name
                    descView.text = recipe.description

                    recipeView.setOnClickListener {
                        val recipeDetailFragment = RecipeDetailFragment.newInstance(recipe.name)
                        parentFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragment_container,
                                recipeDetailFragment
                            ) // R.id.fragment_container is your main container
                            .addToBackStack(null) // Allows users to navigate back
                            .commit()
                    }

                    recipeItems.addView(recipeView)
                }
            }
        }

        // add to pantry button
        val addBtn = view.findViewById<Button>(R.id.addBtn)
        addBtn.setOnClickListener {
            // open popup for adding to pantry
            EditItemFragment.newInstance(itemName.toString(),"ADD TO PANTRY").show(parentFragmentManager, "custom_dialog")
        }
    }

    companion object {
        private const val ARG_ITEM_NAME = "item_name"

        @JvmStatic
        fun newInstance(itemName: String) =
            ItemDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ITEM_NAME, itemName)
                }
            }
    }

    /*
    private fun refreshItemDetails() {
        val view = view ?: return
        itemName?.let { name ->
            viewLifecycleOwner.lifecycleScope.launch {
                val item = pantryDB.pantryItemDao().getPantryItemByName(name)
                if (item != null) {
                    setNutrition(view, item.calories, item.fiber, item.totFat,
                        item.sugars, item.transFat, item.protein,
                        item.sodium, item.iron, item.calcium, item.carbs)

                    val servingAmt = view.findViewById<TextView>(R.id.servingAmt)
                    servingAmt.text = item.curNum.toString() // or any updated display
                }
            }
        }
    }*/

}