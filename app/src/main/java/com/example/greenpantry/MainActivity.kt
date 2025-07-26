package com.example.greenpantry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import com.example.greenpantry.ui.login.RegisterScreen
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.example.greenpantry.domain.repositories.AuthRepository
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.room.Room
import com.example.greenpantry.data.database.FoodItemDatabase
import com.example.greenpantry.data.database.CSVLoader
import com.example.greenpantry.data.database.RecipeDatabase
import com.example.greenpantry.presentation.viewmodel.DatabaseViewModel
import com.example.greenpantry.ui.login.LoginActivity
import com.example.greenpantry.ui.sharedcomponents.DatabaseLoadingScreen
import com.example.greenpantry.utils.DatabaseValidation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    private val databaseViewModel: DatabaseViewModel by viewModels()

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

        // Check login state on startup
        var checkedLoginState = false
        var loggedInState by mutableStateOf(false)

        // Start database initialization immediately
        databaseViewModel.initializeDatabases()

        lifecycleScope.launch {
            // ------------------ NOTE ------------------
            // set loggestInState to false to force app to always show login/register screen on load
            loggedInState = authRepository.getLoginState()
            // loggedInState = false
            checkedLoginState = true
            Log.d("LoadScreen", "Done retrieving loggedInState")
        }

        composeView.setContent {
            var showingRegister by remember { mutableStateOf(false) }
            val isLoggedInState = remember { mutableStateOf(loggedInState) }

            // Observe database loading state
            val isLoadingDatabase by databaseViewModel.isLoading.collectAsState()
            val loadingProgress by databaseViewModel.loadingProgress.collectAsState()
            val loadingMessage by databaseViewModel.loadingMessage.collectAsState()
            val initializationComplete by databaseViewModel.initializationComplete.collectAsState()

            // Observe login state changes
            LaunchedEffect(loggedInState) {
                isLoggedInState.value = loggedInState
            }

            if (!checkedLoginState || isLoadingDatabase) {
                // Show enhanced loading screen
                DatabaseLoadingScreen(
                    isLoadingAuth = !checkedLoginState,
                    isLoadingDatabase = isLoadingDatabase,
                    loadingProgress = loadingProgress,
                    loadingMessage = loadingMessage
                )
            } else if (!isLoggedInState.value) {
                if (showingRegister) {
                    val intent = Intent(this, RegisterScreen::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        // Update UI visibility based on login state and database initialization
        lifecycleScope.launch {
            databaseViewModel.initializationComplete.collect { isComplete ->
                if (isComplete && checkedLoginState) {
                    // Quick status check
                    try {
                        val foodItemDao = databaseViewModel.foodItemDao
                        val recipeDao = databaseViewModel.recipeDao
                        DatabaseValidation.quickTest(this@MainActivity, foodItemDao, recipeDao)
                    } catch (e: Exception) {
                        Log.d("MainActivity", "Could not run database test: ${e.message}")
                    }
                    
                    if (!loggedInState) {
                        composeView.isVisible       = true
                        fragmentContainer.isVisible = false
                        bottomNav.isVisible         = false
                    } else {
                        composeView.isVisible       = false
                        fragmentContainer.isVisible = true
                        bottomNav.isVisible         = true
                        loadHome()
                    }
                }
            }
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