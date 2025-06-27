package com.example.greenpantry.ui.home // Or your relevant package

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenpantry.R
import com.example.greenpantry.ui.search.SearchFragment
import com.example.greenpantry.ui.sharedcomponents.popBack
import com.example.greenpantry.ui.sharedcomponents.setNutrition
import com.example.greenpantry.ui.sharedcomponents.setupNotifBtn

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

        // set the back and notif button
        val backText: TextView = view.findViewById(R.id.recipeBack)
        popBack(backText)
        setupNotifBtn(view)

        // Retrieve the recipe name passed as an argument
        val recipeName = arguments?.getString(ARG_RECIPE_NAME) // Assuming ARG_RECIPE_NAME is your bundle key

        // Find the TextView by its NEW ID from fragment_recipe_detail.xml
        val titleTextView = view.findViewById<TextView>(R.id.recipeDetailTitle) // Use the new ID here

        // Set the text of this TextView
        titleTextView.text = recipeName ?: "Recipe Details" // Provide a default if recipeName is null

        // update image
        val itemImage = view.findViewById<ImageView>(R.id.recipeImage)
        val newImage = R.drawable.ic_launcher_background // replace with the image of item
        itemImage.setImageResource(newImage)

        // update recipe overvie text
        val time = view.findViewById<TextView>(R.id.timeAmt)
        val difficulty = view.findViewById<TextView>(R.id.diffAmt)
        val servings = view.findViewById<TextView>(R.id.numServings)
        val timeAmt = 30
        val diffAmt = 4
        val numServing = 1
        time.text = timeAmt.toString()
        difficulty.text = diffAmt.toString()
        servings.text = numServing.toString()

        // dummy nutrition info
        val calAmt = 165
        val fiberAmt = 1
        val totFatAmt = 4
        val sugarsAmt = 1
        val transFatAmt = 1
        val protAmt = 31
        val sodiumAmt = 0
        val ironAmt = 1
        val calciumAmt = 1
        val vitDAmt = 1

        setNutrition(view, calAmt, fiberAmt, totFatAmt, sugarsAmt, transFatAmt,
            protAmt, sodiumAmt, ironAmt, calciumAmt, vitDAmt)

        // ingredients
        val ingredientView = view.findViewById<RecyclerView>(R.id.ingredientGrid)
        val ingredientList = List(4) { "Item ${it + 1}" } // dummy vals
        ingredientView.layoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean = false // turn off nested scroll
        }
        ingredientView.adapter = IngredientAdapter(ingredientList)

        // instructions
        val instructionView = view.findViewById<RecyclerView>(R.id.instructionGrid)
        val instructionList = List(4) { "${it + 1}" } // dummy vals
        instructionView.layoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean = false // turn off nested scroll
        }
        instructionView.adapter = InstructionAdapter(instructionList)


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

class IngredientAdapter(private val items: List<String>) :
RecyclerView.Adapter<IngredientAdapter.IngredientHolder>() {

    class IngredientHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById<ImageView>(R.id.item_image)
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
        val itemImg = R.drawable.ic_launcher_background // change this too
        holder.image.setImageResource(itemImg)
        holder.name.text = items[position]
        val amt = 1 // change this later one
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
        val itemImg = R.drawable.ic_launcher_background // change this too
        holder.count.text = items[position]
        val info = "Random instructions" // change this too
        holder.line.text = info
    }

    override fun getItemCount(): Int = items.size
}


