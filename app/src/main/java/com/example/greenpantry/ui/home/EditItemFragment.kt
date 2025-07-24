package com.example.greenpantry.ui.home

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenpantry.R
import com.example.greenpantry.data.database.PantryItemDatabase
import com.example.greenpantry.ui.home.ItemDetailFragment.Companion
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.example.greenpantry.ui.sharedcomponents.popBack
import kotlinx.coroutines.launch
import android.util.Log
import com.bumptech.glide.Glide
import com.example.greenpantry.data.database.FoodItemDatabase
import com.example.greenpantry.data.database.PantryItem
import com.example.greenpantry.data.database.PantryItemDao
import com.example.greenpantry.ui.sharedcomponents.groupImg
import com.example.greenpantry.ui.sharedcomponents.itemImageSetup
import com.google.android.material.slider.Slider

class EditItemFragment : DialogFragment() {

    private var itemName: String? = null
    private var btnText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemName = it.getString(ARG_ITEM_NAME)
            btnText = it.getString(ARG_BUTTON_TEXT) // "UPDATE" or "ADD TO PANTRY"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_item, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve name passed as an argument
        val itemName = arguments?.getString(ARG_ITEM_NAME)

        // Find the TextView by its NEW ID from fragment_item_detail.xml
        val itemTitle = view.findViewById<TextView>(R.id.item_title)

        itemTitle.text = itemName ?: "Item"

        // update other values based on item
        val itemImg = view.findViewById<ImageView>(R.id.item_img)
        val amountDisplay = view.findViewById<Slider>(R.id.amountSlider)
        val amountVal = view.findViewById<TextView>(R.id.sliderVal)
        val unitDisplay = view.findViewById<EditText>(R.id.unitInput)
        val addBtn = view.findViewById<Button>(R.id.addBtn)

        // get the current img, amount and unit from database
        val pantryDB = PantryItemDatabase.getDatabase(requireContext())
        val foodDB = FoodItemDatabase.getDatabase(requireContext())

        // adjust amount display for slider as it changes
        amountDisplay.addOnChangeListener { _, value, _ ->
            amountVal.text = value.toInt().toString()
        }

        var inPantry = false
        if (itemName != null) { // is in pantry
            viewLifecycleOwner.lifecycleScope.launch {
                val item = pantryDB.pantryItemDao().getPantryItemByName(itemName)
                if (item != null) { // in pantry
                    // Set image, amount and unit hints using current data
                    inPantry = true

                    itemImageSetup(null, item, itemImg)

                    val newVal = item.curNum.coerceIn(0, 100).toFloat()
                    amountDisplay.value = newVal
                    unitDisplay.hint = item.quantity
                }
                else { // not in pantry so retrieve from food db
                    val food = foodDB.foodItemDao().getFoodItemByName(itemName)
                    itemImageSetup(food, null, itemImg)
                    amountDisplay.value = 1F // none in pantry
                    unitDisplay.hint = "each" // default
                }
            }
        }

        // update button text
        addBtn.text = btnText

        addBtn.setOnClickListener {
            // error check the input
            val addAmount = amountDisplay.value.toInt()
            val unitInput = unitDisplay.text.toString()

            if (addAmount == null || addAmount < 0 || (addAmount == 0 && !inPantry)) {
                Toast.makeText(view.context, "Please enter a valid amount", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (addAmount == 0 && inPantry && itemName != null) { // remove from pantry
                viewLifecycleOwner.lifecycleScope.launch {
                    val item = pantryDB.pantryItemDao().getPantryItemByName(itemName)
                    if (item != null) {
                        pantryDB.pantryItemDao().deletePantryItem(item)
                    }
                }
            }

            var newUnit = unitInput

            if (itemName != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val item = pantryDB.pantryItemDao().getPantryItemByName(itemName)
                    if (item != null) { // item is in pantry
                        if (newUnit.isBlank()) {
                            newUnit = item.quantity.toString()
                        }
                        // update based on which fragment called it
                        val updatedItem = when (btnText) {
                            "UPDATE" -> item.copy(curNum = addAmount, quantity = newUnit)
                            "ADD TO PANTRY" -> item.copy(
                                curNum = item.curNum + addAmount,
                                quantity = newUnit
                            )

                            else -> {
                                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                                null
                            }
                        }
                        updatedItem?.let { // ensure not null
                            pantryDB.pantryItemDao().updatePantryItem(it)
                        }
                        if (btnText == "UPDATE" && addAmount == 0) {
                            Toast.makeText(requireContext(), "Removed from pantry", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(requireContext(), "Added to pantry", Toast.LENGTH_SHORT)
                                .show()
                        }
                        parentFragmentManager.setFragmentResult("edit_item_result", Bundle().apply {
                            putBoolean("updated", true)
                        })
                        dismiss()
                    } else { // item not in pantry
                        val food = foodDB.foodItemDao().getFoodItemByName(itemName)
                        if (newUnit.isBlank()) {
                            newUnit = "each"
                        }
                        val newItem = food?.let { it1 ->
                            PantryItem(
                                name = itemName,
                                description = "Newly added item",
                                imageURL = it1.imageURL,
                                category = it1.category,
                                quantity = newUnit,
                                calories = it1.calories,
                                fiber = it1.fiber,
                                totFat = it1.totFat,
                                sugars = it1.sugars,
                                transFat = it1.transFat,
                                protein = it1.protein,
                                sodium = it1.sodium,
                                iron = it1.iron,
                                calcium = it1.calcium,
                                carbs = it1.carbs,
                                curNum = addAmount
                            )
                        }
                        if (newItem != null) {
                            pantryDB.pantryItemDao().insertPantryItem(newItem)
                        }
                        // added new item to pantry
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Missing item reference", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // adjust the layout of the fragment since it doesn't take from the parent linearlayout
    override fun onStart() {
        super.onStart()

        // calculate custom height in px
        val desiredHeightInDp = 500 // this is what's set in xml
        val scale = resources.displayMetrics.density
        val desiredHeightInPx = (desiredHeightInDp * scale + 0.5f).toInt()

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, desiredHeightInPx)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_rectangle)

    }

    companion object {
        private const val ARG_ITEM_NAME = "item_name"
        private const val ARG_BUTTON_TEXT = "button_text"

        @JvmStatic
        fun newInstance(itemName: String, buttonText: String) =
            EditItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ITEM_NAME, itemName)
                    putString(ARG_BUTTON_TEXT, buttonText)
                }
            }
    }
}