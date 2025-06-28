package com.example.greenpantry.ui.home

import android.os.Bundle
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenpantry.R
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.example.greenpantry.ui.sharedcomponents.popBack
import com.example.greenpantry.ui.sharedcomponents.setupNotifBtn

class DetailsFragment : Fragment(R.layout.fragment_pantry) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backText: TextView = view.findViewById(R.id.pantryBack)
        popBack(backText)
        setupNotifBtn(view)

        // search text value
        val inputField = view.findViewById<EditText>(R.id.searchInput)
        val userInput = inputField.text.toString()

        // filter button
        val filterBtn = view.findViewById<ImageButton>(R.id.filterButton)
        filterBtn.setOnClickListener {
            // filter popup
            FilterFragment().show(parentFragmentManager, "custom_dialog")
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.pantryGrid)
        val emptyPantry = view.findViewById<LinearLayout>(R.id.emptyPantry)

        val itemWidthDp = 135
        val displayMetrics = resources.displayMetrics
        val itemWidthPx = (itemWidthDp * displayMetrics.density).toInt()

        val spanCount = (displayMetrics.widthPixels / itemWidthPx).coerceAtLeast(1)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)

        // filter the list using values in FilterViewModel
        val dummyItems = List(20) { "Item ${it + 1}" }

        // display empty message if no items
        if (dummyItems.isEmpty()) {
            recyclerView.visibility = View.INVISIBLE
            emptyPantry.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyPantry.visibility = View.GONE
            recyclerView.adapter = DetailItemAdapter(dummyItems, this)
        }
        recyclerView.adapter = DetailItemAdapter(dummyItems, this)
    }

    // reset the filters to off when view is closed
    override fun onDestroyView() {
        super.onDestroyView()

        val viewModel: FilterViewModel by activityViewModels()
        viewModel.vege.value = false
        viewModel.fruit.value = false
        viewModel.prot.value = false
        viewModel.dairy.value = false
        viewModel.grain.value = false
        viewModel.oth.value = false
    }
}

class DetailItemAdapter(private val items: List<String>, private val fragment: Fragment) :
    RecyclerView.Adapter<DetailItemAdapter.DetailViewHolder>() {

    class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById<ImageView>(R.id.item_image)
        val label: TextView = view.findViewById<TextView>(R.id.item_label)
        val amount: TextView = view.findViewById<TextView>(R.id.item_amount)
        val editBtn: ImageButton = view.findViewById<ImageButton>(R.id.item_edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pantry_grid_items, parent, false)
        return DetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.label.text = items[position]
        val amt = 1 // change this later one
        holder.amount.text = amt.toString()

        // Set image or listeners on buttons here if needed
        val itemName = holder.label.text
        holder.editBtn.setOnClickListener {
            // item edit popup
            EditItemFragment.newInstance(itemName.toString(),"UPDATE").show(fragment.parentFragmentManager, "custom_dialog")
        }
    }

    override fun getItemCount(): Int = items.size
}


