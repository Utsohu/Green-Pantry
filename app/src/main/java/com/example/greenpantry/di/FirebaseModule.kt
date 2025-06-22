package com.example.greenpantry.di

import android.content.Context
import android.util.Log
import com.example.greenpantry.BuildConfig
import com.example.greenpantry.config.FirebaseConfig
import com.example.greenpantry.data.AuthStateManager
import com.example.greenpantry.data.FirebaseAuthRepository
import com.example.greenpantry.domain.repositories.AuthRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    private const val TAG = "FirebaseModule"

    @Provides
    @Singleton
    fun provideFirebaseConfig(@ApplicationContext context: Context): FirebaseConfig =
        FirebaseConfig(context)

    @Provides
    @Singleton
    fun provideFirebaseAuth(firebaseConfig: FirebaseConfig): FirebaseAuth {
        Log.d(TAG, "Providing FirebaseAuth instance")
        
        // Check if Firebase is initialized
        val firebaseApp = FirebaseApp.getInstance()
        Log.d(TAG, "FirebaseApp name: ${firebaseApp.name}")
        Log.d(TAG, "FirebaseApp project ID: ${firebaseApp.options.projectId}")
        
        val auth = Firebase.auth
        val firestore = Firebase.firestore
        
        // Configure emulators if running on Android emulator
        firebaseConfig.configureEmulators(auth, firestore)
        
        // Disable app verification in debug builds
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Debug build - disabling app verification")
            auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
            auth.firebaseAuthSettings.forceRecaptchaFlowForTesting(false)
        }
        
        return auth
    }

    @Provides
    @Singleton
    fun provideFirestore(firebaseConfig: FirebaseConfig): FirebaseFirestore {
        val firestore = Firebase.firestore
        // Emulator configuration is handled in provideFirebaseAuth
        return firestore
    }

    @Provides
    @Singleton
    fun provideAuthStateManager(@ApplicationContext context: Context): AuthStateManager =
        AuthStateManager(context)

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        authStateManager: AuthStateManager
    ): AuthRepository = FirebaseAuthRepository(auth, authStateManager)
}