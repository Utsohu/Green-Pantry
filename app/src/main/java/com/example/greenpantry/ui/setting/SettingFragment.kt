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
import com.example.greenpantry.ui.sharedcomponents.setupNotifBtn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting) {
    
    private val authViewModel: AuthViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNotifBtn(view)

        val switchIngredientLow = view.findViewById<Switch>(R.id.switchIngredientLow)
        switchIngredientLow.setOnCheckedChangeListener { _, isChecked: Boolean ->
            // Use isChecked safely
        }

        val switchRecipesNotif = view.findViewById<Switch>(R.id.switchRecipes)
        switchRecipesNotif.setOnCheckedChangeListener { _, isChecked: Boolean ->
            // Use isChecked safely
        }

        val switchReminders = view.findViewById<Switch>(R.id.remindersSwitch)
        switchReminders.setOnCheckedChangeListener { _, isChecked: Boolean ->
            // Use isChecked safely
        }

        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Call logout method from AuthViewModel
            authViewModel.logout()
        }

        // Observe logout success
        viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
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
                kotlinx.coroutines.delay(100) // Check state every 100ms
            }
        }
    }
}