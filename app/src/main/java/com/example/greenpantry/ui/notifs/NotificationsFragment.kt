package com.example.greenpantry.ui.notifs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.greenpantry.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class NotificationsFragment : Fragment(R.layout.fragment_notif){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // turn off the selected nav icons
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.menu.setGroupCheckable(0, true, false)
        for (i in 0 until bottomNav.menu.size()) {
            bottomNav.menu.getItem(i).isChecked = false
        }
        bottomNav.menu.setGroupCheckable(0, true, true)
    }
}