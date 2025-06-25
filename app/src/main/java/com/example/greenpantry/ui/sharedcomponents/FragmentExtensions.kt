package com.example.greenpantry.ui.sharedcomponents

import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.greenpantry.R
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.setupNotifBtn(view: View) {
    val notifBtn = view.findViewById<ImageButton>(R.id.notificationButton)
    notifBtn.setOnClickListener {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, NotificationsFragment())
            .commit()
    }
}

fun Fragment.resetNav(view: View, pos: Int) {
    val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
    bottomNav.menu.setGroupCheckable(0, true, false)
    for (i in 0 until bottomNav.menu.size()) {
        bottomNav.menu.getItem(i).isChecked = false
    }

    if (pos != -1) { // if its setting a nav to on
        bottomNav.menu.getItem(pos).isChecked = true // change search to on
    }

    bottomNav.menu.setGroupCheckable(0, true, true)
}