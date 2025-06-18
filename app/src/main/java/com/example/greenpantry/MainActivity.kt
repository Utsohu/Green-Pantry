package com.example.greenpantry

/*
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

 */

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isVisible
import com.example.greenpantry.ui.login.LoginScreen
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var isLoggedIn = false // Replace with actual logic later if using Firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val composeLoginView = findViewById<ComposeView>(R.id.compose_login_view)
        val fragmentContainer = findViewById<View>(R.id.fragment_container)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        if (!isLoggedIn) {
            composeLoginView.setContent {
                LoginScreen(
                    onLoginClicked = {
                        isLoggedIn = true
                        composeLoginView.visibility = View.GONE
                        fragmentContainer.visibility = View.VISIBLE
                        bottomNav.visibility = View.VISIBLE
                        loadHomeFragment()
                    },
                    onRegisterClicked = {
                        // TODO: navigate to register screen
                    }
                )
            }
            composeLoginView.visibility = View.VISIBLE
            fragmentContainer.visibility = View.GONE
            bottomNav.visibility = View.GONE
        } else {
            composeLoginView.visibility = View.GONE
            fragmentContainer.visibility = View.VISIBLE
            bottomNav.visibility = View.VISIBLE
            loadHomeFragment()
        }

        bottomNav.setOnItemSelectedListener {
            val fragment = when (it.itemId) {
                R.id.nav_home -> com.example.greenpantry.ui.home.HomeFragment()
                R.id.nav_camera -> com.example.greenpantry.ui.camera.CameraFragment()
                R.id.nav_search -> com.example.greenpantry.ui.search.SearchFragment()
                R.id.nav_setting -> com.example.greenpantry.ui.setting.SettingFragment()
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

    private fun loadHomeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, com.example.greenpantry.ui.home.HomeFragment())
            .commit()
    }
}