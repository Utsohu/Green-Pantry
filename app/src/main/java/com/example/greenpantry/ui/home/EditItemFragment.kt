package com.example.greenpantry.ui.home

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
import com.example.greenpantry.ui.sharedcomponents.setupNotifBtn
import kotlinx.coroutines.launch
import android.util.Log
import com.example.greenpantry.data.database.PantryItemDao

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
        val amountDisplay = view.findViewById<EditText>(R.id.amountInput)
        val unitDisplay = view.findViewById<EditText>(R.id.unitInput)
        val addBtn = view.findViewById<Button>(R.id.addBtn)

        // get the current img, amount and unit from database
        val pantryDB = PantryItemDatabase.getDatabase(requireContext())

        if (itemName != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                val item = pantryDB.pantryItemDao().getPantryItemByName(itemName)
                if (item != null) { // in pantry
                    // Set image, amount and unit hints using current data
                    itemImg.setImageResource(item.imageResId) // Replace with actual image
                    amountDisplay.hint = item.curNum.toString()
                    unitDisplay.hint = item.quantity
                }
            }
        }

        // update button text
        addBtn.text = btnText

        addBtn.setOnClickListener {
            // error check the input
            val amountInput = amountDisplay.text.toString()
            val unitInput = unitDisplay.text.toString()

            val addAmount = amountInput.toIntOrNull()
            if (addAmount == null || addAmount <= 0) {
                Toast.makeText(view.context, "Please enter a valid amount", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } // add a case when it is 0, remove it from the pantry

            val newUnit = unitInput
            if (newUnit.isBlank()) {
                Toast.makeText(view.context, "Please enter a unit", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (itemName != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val item = pantryDB.pantryItemDao().getPantryItemByName(itemName)
                    if (item != null) {
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
                        Toast.makeText(requireContext(), "Added to pantry", Toast.LENGTH_SHORT)
                            .show()
                        parentFragmentManager.setFragmentResult("edit_item_result", Bundle().apply {
                            putBoolean("updated", true)
                        })
                        dismiss()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Item not found in database",
                            Toast.LENGTH_SHORT
                        ).show()
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