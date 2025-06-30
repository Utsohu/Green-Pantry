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

        val switchIngredient = view.findViewById<Switch>(R.id.switchIngredient)
        switchIngredient.setOnCheckedChangeListener { _, isChecked: Boolean ->
            // Use isChecked safely
        }

    }
}