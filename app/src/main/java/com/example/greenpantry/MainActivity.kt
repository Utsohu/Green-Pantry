package com.example.greenpantry

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.greenpantry.ui.camera.CameraFragment
import com.example.greenpantry.ui.home.HomeFragment
import com.example.greenpantry.ui.search.SearchFragment
import com.example.greenpantry.ui.setting.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener {
            val fragment = when (it.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_camera -> CameraFragment()
                R.id.nav_search -> SearchFragment()
                R.id.nav_setting -> SettingFragment()
                else -> null
            }
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
            true
        }
    }
}