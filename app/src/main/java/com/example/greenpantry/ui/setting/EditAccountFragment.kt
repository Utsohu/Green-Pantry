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
import kotlinx.coroutines.launch
import android.util.Log
import androidx.fragment.app.viewModels
import com.example.greenpantry.data.database.PantryItemDao
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
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

        // refesh button for user authentication before changing email
//        val refreshButton = view.findViewById<Button>(R.id.refreshVerificationButton)
//        refreshButton.visibility = View.GONE
//
//        refreshButton.setOnClickListener {
//            FirebaseAuth.getInstance().currentUser?.reload()?.addOnCompleteListener {
//                val refreshedUser = FirebaseAuth.getInstance().currentUser
//                if (refreshedUser?.isEmailVerified == true) {
//                    Toast.makeText(requireContext(), "Email verified! You can now update your email.", Toast.LENGTH_SHORT).show()
//                    refreshButton.visibility = View.GONE
//                } else {
//                    Toast.makeText(requireContext(), "Still not verified. Try again.", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }



        super.onViewCreated(view, savedInstanceState)
        val updateButton = view.findViewById<Button>(R.id.updateButton)
        val usernameField = view.findViewById<EditText>(R.id.editUsername)
//        val emailField = view.findViewById<EditText>(R.id.editEmail)
        val newPasswordField = view.findViewById<EditText>(R.id.editPassword)
        val confirmPasswordField = view.findViewById<EditText>(R.id.confirmPassword)
        val originalPasswordField = view.findViewById<EditText>(R.id.originalPassword)

        updateButton.setOnClickListener {
            Toast.makeText(requireContext(), "Update clicked", Toast.LENGTH_SHORT).show()


            val newUsername = usernameField.text.toString()
//            val newEmail = emailField.text.toString()
            val originalPassword = originalPasswordField.text.toString()
            val newPassword = newPasswordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()


            if (newPassword.isNotBlank() && newPassword != confirmPassword) {
                // TODO: Show error message
                confirmPasswordField.error = "Passwords do not match"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {

                    // Updating Username
                    if (newUsername.isNotBlank()) {
                        try {
                            val ok = authViewModel.updateUsername(newUsername)
                            if (!ok) Toast.makeText(requireContext(), "Username update failed", Toast.LENGTH_SHORT).show()
                            else {
                                val updatedName = FirebaseAuth.getInstance().currentUser?.displayName
                                Log.d("UPDATE", "New username: $updatedName")
                            }
                        } catch (e: Exception) {
                            Log.e("UPDATE", "Username update error: ${e.message}", e)
                            Toast.makeText(requireContext(), "Failed to update username", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Updating password
                    if (newPassword.isNotBlank() && newPassword == confirmPassword) {
                        try {
                            val ok = authViewModel.updatePassword(newPassword)
                            if (!ok) Toast.makeText(requireContext(), "Password update failed", Toast.LENGTH_SHORT).show()

                        } catch (e: Exception) {
                            Log.e("UPDATE", "Password update error: ${e.message}", e)
                            Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Updating email
//                    if (newEmail.isNotBlank()) {
//                        try {
//                            // Authentication of current email
//                            val user = FirebaseAuth.getInstance().currentUser
////                            if (user != null && !user.isEmailVerified) {
////                                try {
////                                    user.sendEmailVerification().addOnCompleteListener { task ->
////                                        if (task.isSuccessful) {
////                                            Toast.makeText(
////                                                requireContext(),
////                                                "Verification email sent to ${user.email}. Please verify before updating.",
////                                                Toast.LENGTH_LONG
////                                            ).show()
////                                            refreshButton.visibility = View.VISIBLE
////
////                                        } else {
////                                            Toast.makeText(
////                                                requireContext(),
////                                                "Failed to send verification email: ${task.exception?.message}",
////                                                Toast.LENGTH_LONG
////                                            ).show()
////                                        }
////                                    }
////                                } catch (e: Exception) {
////                                    Log.e("VERIFY", "Error sending verification email: ${e.message}", e)
////                                    Toast.makeText(requireContext(), "Unexpected error occurred", Toast.LENGTH_SHORT).show()
////                                }
////                                return@launch  // exit early — don’t proceed with update
////                            }
//
//                            // Update email
//                            val ok = authViewModel.updateEmail(newEmail)
//                            if (!ok) {
//                                Toast.makeText(requireContext(), "Email update failed", Toast.LENGTH_SHORT).show()
//                            } else {
//                                val updatedEmail = FirebaseAuth.getInstance().currentUser?.email
//                                Log.d("UPDATE", "New email: $updatedEmail")
//                            }
//                        } catch (e: Exception) {
//                            Log.e("UPDATE", "Email update error: ${e.message}", e)
//                            Toast.makeText(requireContext(), "Failed to update email", Toast.LENGTH_SHORT).show()
//                        }
//                    }


                    val bundle = Bundle().apply {
                        putBoolean("username_updated", true)
                    }
                    parentFragmentManager.setFragmentResult("account_update", bundle)

                    dismiss()

                } catch (e: Exception) {
                    Log.e("UPDATE", "Crash during update: ${e.message}", e)
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show()
                }
            }
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