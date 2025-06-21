package com.example.greenpantry.data

import com.example.greenpantry.domain.repositories.AuthRepository
import com.example.greenpantry.viewmodel.DeleteAccountResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(email: String, password: String): Boolean =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): Boolean = try {
        auth.createUserWithEmailAndPassword(email, password).await()
        val profileUpdates = userProfileChangeRequest {
            displayName = username
        }
        auth.currentUser?.updateProfile(profileUpdates)?.await()
        true
    } catch (e: Exception) {
        false
    }

    override suspend fun logout(): Boolean =
        try {
            auth.signOut()
            true
        } catch (e: Exception) {
            false
        }

    override suspend fun getLoginState(): Boolean =
        auth.currentUser != null

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
