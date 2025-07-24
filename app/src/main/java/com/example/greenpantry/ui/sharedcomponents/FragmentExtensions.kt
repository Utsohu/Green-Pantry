package com.example.greenpantry.ui.sharedcomponents

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.greenpantry.R
import com.example.greenpantry.data.database.FoodItem
import com.example.greenpantry.data.database.PantryItem
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

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

fun Fragment.setNutrition(view: View, calAmt: Int, fiberAmt: Int, totFatAmt: Int,
                          sugarsAmt: Int, transFatAmt: Int, protAmt: Int, sodiumAmt: Int,
                          ironAmt: Int, calciumAmt: Int, carbAmt: Int) {

    val calories = view.findViewById<TextView>(R.id.calAmt)
    val fiber = view.findViewById<TextView>(R.id.fiberAmt)
    val totFat = view.findViewById<TextView>(R.id.totFatAmt)
    val sugars = view.findViewById<TextView>(R.id.sugarAmt)
    val transFat = view.findViewById<TextView>(R.id.transFatAmt)
    val protein = view.findViewById<TextView>(R.id.proteinAmt)
    val sodium = view.findViewById<TextView>(R.id.sodiumAmt)
    val iron = view.findViewById<TextView>(R.id.ironAmt)
    val calcium = view.findViewById<TextView>(R.id.calciumAmt)
    val carbs = view.findViewById<TextView>(R.id.carbAmt)

    calories.text = calAmt.toString()
    fiber.text = fiberAmt.toString()
    totFat.text = totFatAmt.toString()
    sugars.text = sugarsAmt.toString()
    transFat.text = transFatAmt.toString()
    protein.text = protAmt.toString()
    sodium.text = sodiumAmt.toString()
    iron.text = ironAmt.toString()
    calcium.text = calciumAmt.toString()
    carbs.text = carbAmt.toString()
}

fun Fragment.popBack(backText: TextView){
    backText.setOnClickListener {
        parentFragmentManager.popBackStack()
    }
}

// used in fragment and bindviewholder
fun groupImg(group: String): Int {
    var newImg = R.drawable.logo
    if (group == "VEGETABLE") {
        newImg = R.drawable.vege_group
    } else if (group == "FRUIT") {
        newImg = R.drawable.fruit_group
    } else if (group == "PROTEIN") {
        newImg = R.drawable.protein_group
    } else if (group == "GRAIN") {
        newImg = R.drawable.grain_group
    } else if (group == "DAIRY") {
        newImg = R.drawable.dairy_group
    } else { // other
        newImg = R.drawable.other_group
    }

    return newImg
}

fun itemImageSetup(item: FoodItem?, pantry: PantryItem?, view: ImageView) {
    if (item != null) {
        var foodGroup = item.category?.let { groupImg(it) }
        try {
            if (foodGroup == null) {
                foodGroup = R.drawable.logo
            }
            if (item.imageURL.isNotBlank()) {
                val assetManager = view.context.assets
                val inputStream = assetManager.open("itemImages/${item.imageURL}.jpg")
                val drawable = Drawable.createFromStream(inputStream, null)
                view.setImageDrawable(drawable)
            } else {
                view.setImageResource(foodGroup)
            }
        } catch (e: Exception) {
            if (foodGroup != null) {
                view.setImageResource(foodGroup)
            }
        }
    } else if (pantry != null){
        var foodGroup = pantry.category?.let { groupImg(it) }
        try {
            if (foodGroup == null) {
                foodGroup = R.drawable.logo
            }
            if (pantry.imageURL.isNotBlank()) {
                val assetManager = view.context.assets
                val inputStream = assetManager.open("itemImages/${pantry.imageURL}.jpg")
                val drawable = Drawable.createFromStream(inputStream, null)
                view.setImageDrawable(drawable)
            } else {
                view.setImageResource(foodGroup)
            }
        } catch (e: Exception) {
            if (foodGroup != null) {
                view.setImageResource(foodGroup)
            }
        }
    }
}