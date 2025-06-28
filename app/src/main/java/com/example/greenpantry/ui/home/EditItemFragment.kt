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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenpantry.R
import com.example.greenpantry.ui.home.ItemDetailFragment.Companion
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.example.greenpantry.ui.sharedcomponents.popBack
import com.example.greenpantry.ui.sharedcomponents.setupNotifBtn

class EditItemFragment : DialogFragment() {

    private var itemName: String? = null
    private var btnText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemName = it.getString(ARG_ITEM_NAME)
            btnText = it.getString(ARG_BUTTON_TEXT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_item, container, false)

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
        val newImg = R.drawable.ic_launcher_background // replace with the image of item
        itemImg.setImageResource(newImg)
        val curAmt = 1
        val curUnit = "per 100 grams"
        amountDisplay.hint = curAmt.toString()
        unitDisplay.hint = curUnit

        // update button text
        addBtn.text = btnText

        addBtn.setOnClickListener {
            // error check the input
            val amountInput = amountDisplay.text.toString()
            val unitInput = unitDisplay.text.toString()

            /* Update the stored amount and unit to the new input
                if the text is update, change to new value,
                if its add to pantry then add to the current amount
                unit wil always change to the input*/
            if (addBtn.text == "UPDATE") {
            } else {
            }

            Toast.makeText(view.context, "add item clicked", Toast.LENGTH_SHORT).show()
            dismiss() // close fragment
        }

        return view
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


