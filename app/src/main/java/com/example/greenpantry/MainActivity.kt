package com.example.greenpantry

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isVisible
import com.example.greenpantry.ui.login.LoginScreen
import com.example.greenpantry.ui.login.RegisterScreen
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var isLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val composeView      = findViewById<ComposeView>(R.id.compose_login_view)
        val fragmentContainer = findViewById<View>(R.id.fragment_container)
        val bottomNav        = findViewById<BottomNavigationView>(R.id.bottom_navigation)


        fun loadHome() {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, com.example.greenpantry.ui.home.HomeFragment())
                .commit()
        }


        composeView.setContent {
            var showingRegister by remember { mutableStateOf(false) }

            if (!isLoggedIn) {
                if (showingRegister) {
                    RegisterScreen(
                        onRegisterClicked = {
                            isLoggedIn = true
                            composeView.isVisible      = false
                            fragmentContainer.isVisible = true
                            bottomNav.isVisible         = true
                            loadHome()
                        },
                        onLoginClicked = {
                            showingRegister = false
                        }
                    )
                } else {
                    LoginScreen(
                        onLoginClicked = {

                            isLoggedIn = true
                            composeView.isVisible      = false
                            fragmentContainer.isVisible = true
                            bottomNav.isVisible         = true
                            loadHome()
                        },
                        onRegisterClicked = {
                            showingRegister = true
                        }
                    )
                }
            }
        }


        if (!isLoggedIn) {
            composeView.isVisible       = true
            fragmentContainer.isVisible = false
            bottomNav.isVisible         = false
        } else {
            composeView.isVisible       = false
            fragmentContainer.isVisible = true
            bottomNav.isVisible         = true
            loadHome()
        }


        bottomNav.setOnItemSelectedListener {
            val frag = when (it.itemId) {
                R.id.nav_home    -> com.example.greenpantry.ui.home.HomeFragment()
                R.id.nav_camera  -> com.example.greenpantry.ui.camera.CameraFragment()
                R.id.nav_search  -> com.example.greenpantry.ui.search.SearchFragment()
                R.id.nav_setting -> com.example.greenpantry.ui.setting.SettingFragment()
                else             -> null
            }
            frag?.let { f ->
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, f)
                    .commit()
            }
            true
        }
    }
}
