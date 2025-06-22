package com.example.greenpantry.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.greenpantry.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val TAG = "AuthStateManager"
        private const val KEY_BYPASS_AUTH = "bypass_auth"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
    }
    
    fun enableAuthBypass(email: String, username: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Enabling auth bypass for testing")
            prefs.edit()
                .putBoolean(KEY_BYPASS_AUTH, true)
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_NAME, username)
                .apply()
        }
    }
    
    fun disableAuthBypass() {
        Log.d(TAG, "Disabling auth bypass")
        prefs.edit()
            .putBoolean(KEY_BYPASS_AUTH, false)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_NAME)
            .apply()
    }
    
    fun isAuthBypassed(): Boolean = BuildConfig.DEBUG && prefs.getBoolean(KEY_BYPASS_AUTH, false)
    
    fun getBypassedEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    
    fun getBypassedUsername(): String? = prefs.getString(KEY_USER_NAME, null)
}