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
    private var onCancelListener: (() -> Unit)? = null
    
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

    fun setOnCancelListener(listener: () -> Unit) {
        onCancelListener = listener
    }

    fun setRecognizedItem(item: RecognizedFoodItem) {
        this.recognizedItem = item
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val item = recognizedItem ?: run {
            dismiss()
            return super.onCreateDialog(savedInstanceState)
        }

        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_recognition_result, null)
        
        // Set up views
        val etItemName = view.findViewById<EditText>(R.id.etItemName)
        etItemName.setText(item.name)
        view.findViewById<TextView>(R.id.tvCategory).text = item.category.name
        
        item.brand?.let {
            view.findViewById<TextView>(R.id.tvBrand).apply {
                text = "Brand: $it"
            }
        }
        
        val amount = view.findViewById<EditText>(R.id.etDescription)
        val cancel = view.findViewById<Button>(R.id.cancel)
        val save = view.findViewById<Button>(R.id.save)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .create()

        save.setOnClickListener {
            val amtInput = amount.text.toString().ifBlank { "1" }
            val updatedName = etItemName.text.toString().trim()
            if (updatedName.isNotEmpty()) {
                val updatedItem = item.copy(name = updatedName)
                onSaveClickListener?.invoke(updatedItem, amtInput)
            } else {
                onSaveClickListener?.invoke(item, amtInput)
            }
            dialog.dismiss()
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    override fun onCancel(dialog: android.content.DialogInterface) {
        super.onCancel(dialog)
        // No-op, handled in onDismiss
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        onCancelListener?.invoke()
    }
}