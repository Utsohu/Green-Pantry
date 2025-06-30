package com.example.greenpantry.data

import android.util.Log
import com.example.greenpantry.BuildConfig
import com.example.greenpantry.domain.repositories.AuthRepository
import com.example.greenpantry.viewmodel.DeleteAccountResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val authStateManager: AuthStateManager
) : AuthRepository {

    companion object {
        private const val TAG = "FirebaseAuthRepo"
        private const val TIMEOUT_MS = 10_000L // 10 seconds timeout
    }

    override suspend fun login(email: String, password: String): Boolean =
        try {
            Log.d(TAG, "Attempting login for email: $email")
            
            // Try Firebase authentication with timeout
            try {
                withTimeout(TIMEOUT_MS) {
                    val result = auth.signInWithEmailAndPassword(email, password).await()
                    Log.d(TAG, "Login successful for user: ${result.user?.uid}")
                    authStateManager.disableAuthBypass() // Clear any bypass on successful login
                    true
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Login timeout - checking for debug bypass")
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "DEBUG: Enabling auth bypass due to network timeout")
                    authStateManager.enableAuthBypass(email, "Debug User")
                    true
                } else {
                    false
                }
            }
        } catch (e: FirebaseAuthException) {
            Log.e(TAG, "FirebaseAuth error code: ${e.errorCode}")
            Log.e(TAG, "FirebaseAuth error message: ${e.message}")
            
            // Handle specific network error in debug mode
            if (BuildConfig.DEBUG && e.message?.contains("network error", ignoreCase = true) == true) {
                Log.w(TAG, "DEBUG: Network error detected, enabling auth bypass")
                authStateManager.enableAuthBypass(email, "Debug User")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login error: ${e.javaClass.simpleName} - ${e.message}")
            e.printStackTrace()
            false
        }

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): Boolean = try {
        Log.d(TAG, "Attempting registration for email: $email")
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        Log.d(TAG, "User created successfully: ${result.user?.uid}")
        
        val profileUpdates = userProfileChangeRequest {
            displayName = username
        }
        auth.currentUser?.updateProfile(profileUpdates)?.await()
        Log.d(TAG, "Profile updated with username: $username")
        true
    } catch (e: FirebaseAuthException) {
        Log.e(TAG, "FirebaseAuth register error code: ${e.errorCode}")
        Log.e(TAG, "FirebaseAuth register error message: ${e.message}")
        e.printStackTrace()
        false
    } catch (e: Exception) {
        Log.e(TAG, "Registration error: ${e.javaClass.simpleName} - ${e.message}")
        e.printStackTrace()
        false
    }

    override suspend fun logout(): Boolean =
        try {
            auth.signOut()
            authStateManager.disableAuthBypass() // Clear any auth bypass state
            true
        } catch (e: Exception) {
            false
        }

    override suspend fun getLoginState(): Boolean =
        auth.currentUser != null || authStateManager.isAuthBypassed()

    override suspend fun deleteAccount(userPassword: String): DeleteAccountResult {
        val user = auth.currentUser ?: return DeleteAccountResult.NotLoggedIn
        return try {
            val credential = com.google.firebase.auth.EmailAuthProvider
                .getCredential(user.email ?: "", userPassword)
            user.reauthenticateAndRetrieveData(credential).await()
            user.delete().await()
            DeleteAccountResult.Success
        } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
            DeleteAccountResult.WrongPassword
        } catch (e: Exception) {
            DeleteAccountResult.Error(e.message ?: "Unknown error")
        }
    }
}