package com.example.greenpantry.ui.notifs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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

        // notification list
        val notifItems = view.findViewById<LinearLayout>(R.id.notifList)
        val noNotifs = view.findViewById<LinearLayout>(R.id.noNotifs)

        val items = mutableListOf<Triple<String, String, Int>>(
            Triple("June 11", "Running low on chicken breast!\nYou have 1 remaining.", R.drawable.ic_launcher_background),
            Triple("June 12", "Running low on chicken breast!\nYou have 1 remaining.", R.drawable.ic_launcher_background)
        )

        // notif list view
        if (items.isEmpty()) {  // display empty notif message
            notifItems.visibility = View.INVISIBLE
            noNotifs.visibility = View.VISIBLE
        } else {
            noNotifs.visibility = View.INVISIBLE
            notifItems.visibility = View.VISIBLE

            for ((index, item) in items.withIndex()) {
                val (title, description, imageRes) = item
                val itemView = LayoutInflater.from(context).inflate(R.layout.notif_item, notifItems, false)

                val imageView = itemView.findViewById<ImageView>(R.id.itemImage)
                val titleView = itemView.findViewById<TextView>(R.id.itemTitle)
                val descView = itemView.findViewById<TextView>(R.id.itemDescription)

                imageView.setImageResource(imageRes)
                titleView.text = title
                descView.text = description

                itemView.setOnClickListener {
                    Toast.makeText(context, "$title clicked", Toast.LENGTH_SHORT).show()
                }

                notifItems.addView(itemView)

                // Only add divider if not the last item
                if (index < items.size - 1) {
                    val divider = View(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            5
                        )
                        setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
                    }
                    notifItems.addView(divider)
                }
            }
        }
    }
}