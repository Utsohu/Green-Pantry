package com.example.greenpantry

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GreenPantryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        Log.d("GreenPantryApp", "Firebase initialized")
        
        // For emulator testing - uncomment the line below
        // This will use the Firebase Auth emulator instead of production
        // Firebase.auth.useEmulator("10.0.2.2", 9099)
    }
}