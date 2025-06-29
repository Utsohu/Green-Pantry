package com.example.greenpantry.ui.home // Or your relevant package

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
import com.example.greenpantry.R
import com.example.greenpantry.data.database.PantryItemDatabase
import com.example.greenpantry.data.database.RecipeDatabase
import com.example.greenpantry.ui.search.SearchFragment
import com.example.greenpantry.ui.sharedcomponents.popBack
import com.example.greenpantry.ui.sharedcomponents.setupNotifBtn
import com.example.greenpantry.ui.sharedcomponents.setNutrition
import kotlinx.coroutines.launch

class ItemDetailFragment : Fragment() {

    private var itemName: String? = null
    private lateinit var pantryDB : PantryItemDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemName = it.getString(ARG_ITEM_NAME)
        }
        pantryDB = PantryItemDatabase.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_item_detail, container, false)

        // set the back and notif button
        val backText: TextView = view.findViewById(R.id.itemBack)
        popBack(backText)
        setupNotifBtn(view)

        // Retrieve the recipe name passed as an argument
        val itemName = arguments?.getString(ARG_ITEM_NAME) // Assuming ARG_ITEM_NAME is your bundle key

        // Find the TextView by its NEW ID from fragment_item_detail.xml
        val titleTextView = view.findViewById<TextView>(R.id.itemDetailTitle)

        // Set the text of this TextView
        titleTextView.text = itemName ?: "Item Details" // Provide a default if itemName is null

        // update image
        val itemImage = view.findViewById<ImageView>(R.id.itemImage)
        val newImage = R.drawable.ic_launcher_background // replace with the image of item
        itemImage.setImageResource(newImage)

        // update serving size
        val servingAmt = view.findViewById<TextView>(R.id.servingAmt)
        val size = 100 // dummy val for now
        servingAmt.text = size.toString()


        itemName?.let { name ->
            viewLifecycleOwner.lifecycleScope.launch {
                val item = pantryDB.pantryItemDao().getPantryItemByName(name)
                if(item != null){
                    setNutrition(view, item.calories, item.fiber, item.totalFat,
                        item.sugars, item.transFat, item.protein,
                        item.sodium, item.iron, item.calcium, item.vitaminD)
                }else {
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

        val recipeItems = view.findViewById<LinearLayout>(R.id.ItemRecipesList)
        lifecycleScope.launch {
            // recipe display
           for (recipe in items) {
               val recipeView = LayoutInflater.from(context)
                   .inflate(R.layout.recipes_items, recipeItems, false)

               val imageView = recipeView.findViewById<ImageView>(R.id.itemImage)
               val titleView = recipeView.findViewById<TextView>(R.id.itemTitle)
               val descView = recipeView.findViewById<TextView>(R.id.itemDescription)

               imageView.setImageResource(recipe.imageResId)
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

        // add to pantry button
        val addBtn = view.findViewById<Button>(R.id.addBtn)
        addBtn.setOnClickListener {
            // open popup for adding to pantry
            EditItemFragment.newInstance(itemName.toString(),"ADD TO PANTRY").show(parentFragmentManager, "custom_dialog")
        }

        return view
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
}