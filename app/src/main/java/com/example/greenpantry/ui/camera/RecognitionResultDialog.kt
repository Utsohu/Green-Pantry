package com.example.greenpantry.ui.camera

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.greenpantry.R
import com.example.greenpantry.data.model.RecognizedFoodItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RecognitionResultDialog : DialogFragment() {
    
    private var recognizedItem: RecognizedFoodItem? = null
    private var onSaveClickListener: ((RecognizedFoodItem, String) -> Unit)? = null
    
    companion object {
        fun newInstance(item: RecognizedFoodItem): RecognitionResultDialog {
            return RecognitionResultDialog().apply {
                recognizedItem = item
            }
        }
    }
    
    fun setOnSaveClickListener(listener: (RecognizedFoodItem, String) -> Unit) {
        onSaveClickListener = listener
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val item = recognizedItem ?: run {
            dismiss()
            return super.onCreateDialog(savedInstanceState)
        }
        
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_recognition_result, null)
        
        // Set up views
        view.findViewById<TextView>(R.id.tvItemName).text = item.name
        view.findViewById<TextView>(R.id.tvCategory).text = "Category: ${item.category.name}"
        view.findViewById<TextView>(R.id.tvConfidence).text = "Confidence: ${(item.confidence * 100).toInt()}%"
        
        item.brand?.let {
            view.findViewById<TextView>(R.id.tvBrand).apply {
                text = "Brand: $it"
                visibility = View.VISIBLE
            }
        }
        
        item.quantity?.let {
            view.findViewById<TextView>(R.id.tvQuantity).apply {
                text = "Quantity: $it"
                visibility = View.VISIBLE
            }
        }
        
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Food Item Recognized")
            .setView(view)
            .setPositiveButton("Save to Pantry") { _, _ ->
                val description = etDescription.text.toString()
                onSaveClickListener?.invoke(item, description)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}