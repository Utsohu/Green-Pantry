package com.example.greenpantry.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.greenpantry.R
import com.example.greenpantry.ui.notifs.NotificationsFragment

class SearchFragment : Fragment(R.layout.fragment_search) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notifBtn = view.findViewById<ImageButton>(R.id.notificationButton)
        notifBtn.setOnClickListener {
            // go to notification fragment
            notifBtn.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, NotificationsFragment())
                    .commit()
            }
        }
    }
}