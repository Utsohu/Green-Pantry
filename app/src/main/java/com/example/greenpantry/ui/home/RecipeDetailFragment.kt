package com.example.greenpantry.ui.home // Or your relevant package

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.semantics.text
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenpantry.R
import com.example.greenpantry.data.database.FoodItemDatabase
import com.example.greenpantry.data.database.PantryItemDatabase
import com.example.greenpantry.data.database.RecipeDatabase
import com.example.greenpantry.ui.utils.ListOptimizations
import com.example.greenpantry.ui.search.SearchFragment
import com.example.greenpantry.ui.sharedcomponents.popBack
import com.example.greenpantry.ui.sharedcomponents.setNutrition
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.Int

class RecipeDetailFragment : Fragment() {

    private var recipeName: String? = null
    private lateinit var recipeDB : RecipeDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipeName = it.getString(ARG_RECIPE_NAME)
        }
        recipeDB = RecipeDatabase.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recipe_detail_fragement, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set the back and notif button
        val backText: TextView = view.findViewById(R.id.recipeBack)
        popBack(backText)

        // Retrieve the recipe name passed as an argument
        val recipeName = arguments?.getString(ARG_RECIPE_NAME) // Assuming ARG_RECIPE_NAME is your bundle key

        // Find the TextView by its NEW ID from fragment_recipe_detail.xml
        val titleTextView = view.findViewById<TextView>(R.id.recipeDetailTitle) // Use the new ID here

        // Set the text of this TextView
        titleTextView.text = recipeName ?: "Recipe Details" // Provide a default if recipeName is null


        val time = view.findViewById<TextView>(R.id.timeAmt)
        val difficulty = view.findViewById<TextView>(R.id.diffAmt)
        val servings = view.findViewById<TextView>(R.id.numServings)
        recipeName?.let { name ->
            viewLifecycleOwner.lifecycleScope.launch {
                val item = recipeDB.recipeDao().getRecipeByTitle(name)
                if(item != null){
                    time.text = item.time.toString()
                    difficulty.text = item.difficulty.toString()
                    servings.text = item.NOS.toString()
                    setNutrition(view, item.calories, item.fiber, item.totalFat,
                        item.sugars, item.transFat, item.protein,
                        item.sodium, item.iron, item.calcium, item.carbs)

                    // update image
                    val itemImage = view.findViewById<ImageView>(R.id.recipeImage)
                    try {
                        if (item.imageName.isNotBlank()) {
                            val assetManager = view.context.assets
                            val inputStream = assetManager.open("recipeImages/${item.imageName}.jpg")
                            val drawable = Drawable.createFromStream(inputStream, null)
                            itemImage.setImageDrawable(drawable)
                        } else {
                            itemImage.setImageResource(item.imageResId)
                        }
                    } catch (e: Exception) {
                        itemImage.setImageResource(item.imageResId)
                    }



                    val ingredientView = view.findViewById<RecyclerView>(R.id.ingredientGrid)
                    val ingredientList = item.ingredients
                    ingredientView.layoutManager = object : LinearLayoutManager(requireContext()) {
                        override fun canScrollVertically(): Boolean = false // turn off nested scroll
                    }
                    ingredientView.setHasFixedSize(true) // Performance optimization
                    ingredientView.setItemViewCacheSize(20) // Cache more views
                    ingredientView.isNestedScrollingEnabled = false // Disable nested scrolling
                    // Apply additional performance optimizations
                    ListOptimizations.optimizeList(ingredientView)
                    ingredientView.adapter = IngredientAdapter(ingredientList)

                    // instructions
                    val instructionView = view.findViewById<RecyclerView>(R.id.instructionGrid)
                    val instructionList = item.setUpInstructions
                    instructionView.layoutManager = object : LinearLayoutManager(requireContext()) {
                        override fun canScrollVertically(): Boolean = false // turn off nested scroll
                    }
                    instructionView.setHasFixedSize(true) // Performance optimization
                    instructionView.setItemViewCacheSize(20) // Cache more views
                    instructionView.isNestedScrollingEnabled = false // Disable nested scrolling
                    // Apply additional performance optimizations
                    ListOptimizations.optimizeList(instructionView)
                    instructionView.adapter = InstructionAdapter(instructionList)
                }else {
                    val timeAmt = 30
                    val diffAmt = 4
                    val numServing = 1
                    time.text = timeAmt.toString()
                    difficulty.text = diffAmt.toString()
                    servings.text = numServing.toString()
                    // fallback dummy values
                    setNutrition(view, 165, 1, 4,
                        1, 1, 31,
                        0, 1, 1, 1)
                }
            }
        }?: run {
            val timeAmt = 30
            val diffAmt = 4
            val numServing = 1
            time.text = timeAmt.toString()
            difficulty.text = diffAmt.toString()
            servings.text = numServing.toString()
            // itemName is null or not a valid string
            setNutrition(view, 165, 1, 4,
                1, 1, 31, 0,
                1, 1, 1)
        }
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

class IngredientAdapter(private val items: List<String>) :
RecyclerView.Adapter<IngredientAdapter.IngredientHolder>() {

    class IngredientHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById<TextView>(R.id.item_name)
        val amount: TextView = view.findViewById<TextView>(R.id.item_amount)
        val unit: TextView = view.findViewById<TextView>(R.id.item_unit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ingredient_items, parent, false)
        return IngredientHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientHolder, position: Int) {
        holder.name.text = items[position]
        val amt = (1..40).random() // change this later one
        holder.amount.text = amt.toString()
        val unit = "g" // change this too
        holder.unit.text = unit
    }

    override fun getItemCount(): Int = items.size
}

class InstructionAdapter(private val items: List<String>) :
    RecyclerView.Adapter<InstructionAdapter.InstructionHolder>() {

    class InstructionHolder(view: View) : RecyclerView.ViewHolder(view) {
        val count: TextView = view.findViewById<TextView>(R.id.item_count)
        val line: TextView = view.findViewById<TextView>(R.id.item_line)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructionHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.instruction_items, parent, false)
        return InstructionHolder(view)
    }

    override fun onBindViewHolder(holder: InstructionHolder, position: Int) {
        holder.count.text = (position+1).toString()
        val info = items[position] // change this too
        holder.line.text = info
    }

    override fun getItemCount(): Int = items.size
}


