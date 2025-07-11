package com.example.greenpantry.ui.home

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenpantry.R
import com.example.greenpantry.ui.home.ItemDetailFragment.Companion
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.example.greenpantry.ui.sharedcomponents.popBack
import com.example.greenpantry.ui.sharedcomponents.setupNotifBtn

class FilterFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_filter, container, false)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveBtn = view.findViewById<Button>(R.id.saveBtn)
        val vegeCheck = view.findViewById<CheckBox>(R.id.vegeCheck)
        val fruitCheck = view.findViewById<CheckBox>(R.id.fruitCheck)
        val protCheck = view.findViewById<CheckBox>(R.id.protCheck)
        val grainCheck = view.findViewById<CheckBox>(R.id.grainCheck)
        val dairyCheck = view.findViewById<CheckBox>(R.id.dairyCheck)
        val otherCheck = view.findViewById<CheckBox>(R.id.otherCheck)

        val viewModel: FilterViewModel by activityViewModels()

        vegeCheck.isChecked = viewModel.vege.value ?: false
        fruitCheck.isChecked = viewModel.fruit.value ?: false
        protCheck.isChecked = viewModel.prot.value ?: false
        grainCheck.isChecked = viewModel.grain.value ?: false
        dairyCheck.isChecked = viewModel.dairy.value ?: false
        otherCheck.isChecked = viewModel.oth.value ?: false

        saveBtn.setOnClickListener {
            // update filters
            viewModel.vege.value = vegeCheck.isChecked
            viewModel.fruit.value = fruitCheck.isChecked
            viewModel.prot.value = protCheck.isChecked
            viewModel.grain.value = grainCheck.isChecked
            viewModel.dairy.value = dairyCheck.isChecked
            viewModel.oth.value = otherCheck.isChecked

            parentFragmentManager.setFragmentResult("filter_changed", Bundle().apply {
                putBoolean("updated", true)
            })
            dismiss() // close fragment
        }
    }
    
    // adjust the layout of the fragment since it doesn't take from the parent linearlayout
    override fun onStart() {
        super.onStart()

        // calculate custom height in px
        val desiredHeightInDp = 550 // this is what's set in xml
        val scale = resources.displayMetrics.density
        val desiredHeightInPx = (desiredHeightInDp * scale + 0.5f).toInt()

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, desiredHeightInPx)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_rectangle)

    }

}


