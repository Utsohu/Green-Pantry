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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenpantry.R
import com.example.greenpantry.ui.utils.ListOptimizations
import com.example.greenpantry.ui.sharedcomponents.popBack
import com.example.greenpantry.ui.sharedcomponents.resetNav
import com.google.android.material.bottomnavigation.BottomNavigationView

class NotificationsFragment : Fragment(R.layout.fragment_notif){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // turn off the selected nav icons
        resetNav(view, -1)

        // back button
        val backText: TextView = view.findViewById(R.id.back)
        popBack(backText)

        // notification RecyclerView
        val notifRecyclerView = view.findViewById<RecyclerView>(R.id.notifRecyclerView)
        val noNotifs = view.findViewById<LinearLayout>(R.id.noNotifs)

        // Sample notification data
        val items = mutableListOf<Triple<String, String, Int>>(
            Triple("June 11", "Running low on romaine lettuce!\nYou have 1 remaining.", R.drawable.img_romaine_lettuce),
            Triple("June 12", "Running low on romaine lettuce!\nYou have 1 remaining.", R.drawable.img_romaine_lettuce)
        )

        // Set up RecyclerView
        if (items.isEmpty()) {
            notifRecyclerView.visibility = View.INVISIBLE
            noNotifs.visibility = View.VISIBLE
        } else {
            noNotifs.visibility = View.INVISIBLE
            notifRecyclerView.visibility = View.VISIBLE
            
            notifRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = NotificationAdapter(items) { title ->
                    Toast.makeText(context, "$title clicked", Toast.LENGTH_SHORT).show()
                }
                
                // Apply performance optimizations
                ListOptimizations.optimizeList(this)
            }
        }
    }
}

class NotificationAdapter(
    private val items: List<Triple<String, String, Int>>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    
    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemImage)
        val titleView: TextView = view.findViewById(R.id.itemTitle)
        val descView: TextView = view.findViewById(R.id.itemDescription)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notif_item, parent, false)
        return NotificationViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val (title, description, imageRes) = items[position]
        
        holder.imageView.setImageResource(imageRes)
        holder.titleView.text = title
        holder.descView.text = description
        
        holder.itemView.setOnClickListener {
            onItemClick(title)
        }
    }
    
    override fun getItemCount(): Int = items.size
}