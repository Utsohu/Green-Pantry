package com.example.greenpantry.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.greenpantry.R
import com.example.greenpantry.presentation.viewmodel.AuthViewModel
import com.example.greenpantry.ui.notifs.NotificationsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import android.widget.RadioGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.EditText
import android.widget.TextView
import com.example.greenpantry.ui.home.EditItemFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await



@AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting) {
    
    private val authViewModel: AuthViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val usernameText = view.findViewById<TextView>(R.id.usernameValue)
        val emailText = view.findViewById<TextView>(R.id.emailValue)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            usernameText.text = user.displayName ?: "Unknown"
            emailText.text = user.email ?: "No email"
        }

        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener("account_update", viewLifecycleOwner) { _, result ->
            val updated = result.getBoolean("username_updated", false)
            if (updated) {
                updateUsernameUI()
                updateEmailUI()
                updatePasswordUI()
            }
        }

        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Call logout method from AuthViewModel
            authViewModel.logout()
        }

        val editButton = view.findViewById<ImageButton>(R.id.editButton)
        editButton.setOnClickListener {
            EditAccountFragment().show(parentFragmentManager, "custom_dialog")
        }


        // Observe logout success
        viewLifecycleOwner.lifecycleScope.launch {
            while (!isDetached) {
                val state = authViewModel.uiState.value
                if (state.logoutSuccess) {
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    // Navigate to login screen by triggering a complete app restart
                    val intent = requireActivity().packageManager.getLaunchIntentForPackage(requireActivity().packageName)
                    if (intent != null) {
                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    break
                }
                if (state.errorMessage != null && state.errorMessage.contains("Logout")) {
                    Toast.makeText(context, state.errorMessage, Toast.LENGTH_LONG).show()
                    break
                }
                kotlinx.coroutines.delay(100) // Check every 100ms
            }
        }
    }

   private fun updateUsernameUI() {
        val user = FirebaseAuth.getInstance().currentUser
        view?.findViewById<TextView>(R.id.usernameValue)?.text = user?.displayName ?: "Unknown"
    }

    private fun updateEmailUI() {
        val user = FirebaseAuth.getInstance().currentUser
        view?.findViewById<TextView>(R.id.emailValue)?.text = user?.email ?: "No email"
    }

    private fun updatePasswordUI() {
        val user = FirebaseAuth.getInstance().currentUser

        // randomize password length for UI
        val fakeLength = (8..12).random()
        val hiddenPassword = "*".repeat(fakeLength)
        view?.findViewById<TextView>(R.id.passwordValue)?.text = hiddenPassword
    }
}