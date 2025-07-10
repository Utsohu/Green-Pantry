package com.example.greenpantry.ui.utils

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.graphics.Rect

/**
 * Utilities for optimizing list performance and scrolling
 */
object ListOptimizations {
    
    private val sharedViewPool = RecyclerView.RecycledViewPool()
    
    init {
        // Pre-configure view pool sizes for different view types
        sharedViewPool.setMaxRecycledViews(0, 50) // Standard items
        sharedViewPool.setMaxRecycledViews(1, 30) // Alternative items
    }
    
    /**
     * Apply performance optimizations to any RecyclerView for smooth scrolling
     */
    fun optimizeList(recyclerView: RecyclerView) {
        recyclerView.apply {
            // Share view pool across different lists for memory efficiency
            setRecycledViewPool(sharedViewPool)
            
            // Increase cache size for smoother scrolling
            setItemViewCacheSize(25)
            
            // Optimize for fixed size content when possible
            setHasFixedSize(true)
            
            // Disable animations for better performance
            itemAnimator = null
            
            // Enable hardware acceleration
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
    }
    
    /**
     * Item decoration for consistent spacing
     */
    class ItemSpacing(private val spacing: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.bottom = spacing
        }
    }
}
