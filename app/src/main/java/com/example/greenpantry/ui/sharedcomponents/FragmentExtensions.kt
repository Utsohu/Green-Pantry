package com.example.greenpantry.ui.sharedcomponents

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.greenpantry.R
import com.example.greenpantry.ui.notifs.NotificationsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.setupNotifBtn(view: View) {
    val notifBtn = view.findViewById<ImageButton>(R.id.notificationButton)
    notifBtn.setOnClickListener {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, NotificationsFragment())
            .addToBackStack(null)
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

fun Fragment.setNutrition(view: View, calAmt: Int, fiberAmt: Int, totFatAmt: Int,
                          sugarsAmt: Int, transFatAmt: Int, protAmt: Int, sodiumAmt: Int,
                          ironAmt: Int, calciumAmt: Int, vitDAmt: Int) {

    val calories = view.findViewById<TextView>(R.id.calAmt)
    val fiber = view.findViewById<TextView>(R.id.fiberAmt)
    val totFat = view.findViewById<TextView>(R.id.totFatAmt)
    val sugars = view.findViewById<TextView>(R.id.sugarAmt)
    val transFat = view.findViewById<TextView>(R.id.transFatAmt)
    val protein = view.findViewById<TextView>(R.id.proteinAmt)
    val sodium = view.findViewById<TextView>(R.id.sodiumAmt)
    val iron = view.findViewById<TextView>(R.id.ironAmt)
    val calcium = view.findViewById<TextView>(R.id.calciumAmt)
    val vitaminD = view.findViewById<TextView>(R.id.vitDAmt)

    calories.text = calAmt.toString()
    fiber.text = fiberAmt.toString()
    totFat.text = totFatAmt.toString()
    sugars.text = sugarsAmt.toString()
    transFat.text = transFatAmt.toString()
    protein.text = protAmt.toString()
    sodium.text = sodiumAmt.toString()
    iron.text = ironAmt.toString()
    calcium.text = calciumAmt.toString()
    vitaminD.text = vitDAmt.toString()
}

fun Fragment.popBack(backText: TextView){
    backText.setOnClickListener {
        parentFragmentManager.popBackStack()
    }
}