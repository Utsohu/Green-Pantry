package com.example.greenpantry.ui.setting

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
import com.example.greenpantry.presentation.viewmodel.AuthViewModel
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
import androidx.fragment.app.viewModels
import com.example.greenpantry.data.database.PantryItemDao

class EditAccountFragment : DialogFragment() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.edit_account_bottom_sheet, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val updateButton = view.findViewById<Button>(R.id.updateButton)
        val usernameField = view.findViewById<EditText>(R.id.editUsername)
        val emailField = view.findViewById<EditText>(R.id.editEmail)
        val newPasswordField = view.findViewById<EditText>(R.id.editPassword)
        val confirmPasswordField = view.findViewById<EditText>(R.id.confirmPassword)
        val originalPasswordField = view.findViewById<EditText>(R.id.originalPassword)

        updateButton.setOnClickListener {
            val newUsername = usernameField.text.toString()
            val newEmail = emailField.text.toString()
            val originalPassword = originalPasswordField.text.toString()
            val newPassword = newPasswordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()


            if (newPassword.isNotBlank() && newPassword != confirmPassword) {
                // TODO: Show error message
//                    confirmPasswordField.error = "Passwords do not match"
//                    return@setOnClickListener
            }

            // Reauthenticate if updating email/password
            if (originalPassword.isNotBlank() && (newEmail.isNotBlank() || newPassword.isNotBlank())) {
                // TODO: create functions to handle updating email/password
                    authViewModel.reauthenticateUser(originalPassword) {
                        // Callback after successful reauthentication
                        if (newEmail.isNotBlank()) authViewModel.updateEmail(newEmail)
                        if (newPassword.isNotBlank()) authViewModel.updatePassword(newPassword)
                    }
            }

            if (newUsername.isNotBlank()) {
                // TODO: create function to handle username update
                //  authViewModel.updateUsername(newUsername)
            }

            Toast.makeText(requireContext(), "Account updated", Toast.LENGTH_SHORT).show()
            dismiss()
        }


    }

    // adjust the layout of the fragment since it doesn't take from the parent linearlayout
    override fun onStart() {
        super.onStart()

        // calculate custom height in px
        val desiredHeightInDp = 650 // this is what's set in xml
        val scale = resources.displayMetrics.density
        val desiredHeightInPx = (desiredHeightInDp * scale + 0.5f).toInt()

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, desiredHeightInPx)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_rectangle)

    }

}