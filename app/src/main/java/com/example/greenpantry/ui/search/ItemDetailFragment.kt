package com.example.greenpantry.ui.home // Or your relevant package

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import com.example.greenpantry.R

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

        // Retrieve the recipe name passed as an argument
        val itemName = arguments?.getString(ARG_ITEM_NAME) // Assuming ARG_ITEM_NAME is your bundle key

        // Find the TextView by its NEW ID from fragment_item_detail.xml
        val titleTextView = view.findViewById<TextView>(R.id.itemDetailTitle)

        // Set the text of this TextView
        titleTextView.text = itemName ?: "Item Details" // Provide a default if itemName is null

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