package com.example.greenpantry.config

import android.content.Context
import android.util.Log
import com.example.greenpantry.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseConfig @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "FirebaseConfig"
        
        // Android emulator uses 10.0.2.2 to reach host machine
        const val EMULATOR_HOST = "10.0.2.2"
        const val AUTH_EMULATOR_PORT = 9099
        const val FIRESTORE_EMULATOR_PORT = 8080
    }
    
    fun configureEmulators(auth: FirebaseAuth, firestore: FirebaseFirestore) {
        if (BuildConfig.DEBUG) {
            try {
                // Check if we're running on an emulator
                val isEmulator = android.os.Build.FINGERPRINT.contains("generic") ||
                        android.os.Build.FINGERPRINT.contains("unknown") ||
                        android.os.Build.MODEL.contains("google_sdk") ||
                        android.os.Build.MODEL.contains("Emulator") ||
                        android.os.Build.MODEL.contains("Android SDK")
                
                if (isEmulator) {
                    Log.d(TAG, "Configuring Firebase emulators for Android emulator")
                    
                    // Configure Auth emulator
                    auth.useEmulator(EMULATOR_HOST, AUTH_EMULATOR_PORT)
                    Log.d(TAG, "Auth emulator configured at $EMULATOR_HOST:$AUTH_EMULATOR_PORT")
                    
                    // Configure Firestore emulator
                    firestore.useEmulator(EMULATOR_HOST, FIRESTORE_EMULATOR_PORT)
                    Log.d(TAG, "Firestore emulator configured at $EMULATOR_HOST:$FIRESTORE_EMULATOR_PORT")
                } else {
                    Log.d(TAG, "Not running on emulator, using production Firebase")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error configuring emulators: ${e.message}")
            }
        }
    }
}