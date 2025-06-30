package com.example.greenpantry.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.example.greenpantry.R
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.example.greenpantry.ui.sharedcomponents.setupNotifBtn

class SettingFragment : Fragment(R.layout.fragment_setting) {
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

    }
}