package com.example.greenpantry.ui.home // Or your relevant package

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.greenpantry.R
import com.example.greenpantry.data.database.RecipeDatabase
import com.example.greenpantry.ui.search.SearchFragment
import com.example.greenpantry.ui.sharedcomponents.setupNotifBtn
import kotlinx.coroutines.launch

class ItemDetailFragment : Fragment() {

    private var itemName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemName = it.getString(ARG_ITEM_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_item_detail, container, false)

        // set the back and notif button
        val backText: TextView = view.findViewById(R.id.itemBack)
        backText.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SearchFragment())
                .commit()
        }
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

        // dummy nutrition info
        val calAmt = 165
        val fiberAmt = 1
        val totFatAmt = 3.6
        val sugarsAmt = 1
        val transFatAmt = 1
        val protAmt = 31
        val sodiumAmt = 0.1
        val ironAmt = 1
        val calciumAmt = 1
        val vitDAmt = 1

        val calories = view.findViewById<TextView>(R.id.calAmt)
        val fiber = view.findViewById<TextView>(R.id.fiberAmt)
        val totFat = view.findViewById<TextView>(R.id.totFatAmt)
        val sugars = view.findViewById<TextView>(R.id.sugarAmt)
        val transFat = view.findViewById<TextView>(R.id.transFatAmt)
        val protein = view.findViewById<TextView>(R.id.proteinAmt)
        val sodium = view.findViewById<TextView>(R.id.sodiumAmt)
        val iron = view.findViewById<TextView>(R.id.ironAmt)
        val calcium = view.findViewById<TextView>(R.id.calciumAmt)
        val vitaminD = view.findViewById<TextView>(R.id.vitDAmt)

        calories.text = calAmt.toString()
        fiber.text = fiberAmt.toString()
        totFat.text = totFatAmt.toString()
        sugars.text = sugarsAmt.toString()
        transFat.text = transFatAmt.toString()
        protein.text = protAmt.toString()
        sodium.text = sodiumAmt.toString()
        iron.text = ironAmt.toString()
        calcium.text = calciumAmt.toString()
        vitaminD.text = vitDAmt.toString()


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