package com.example.greenpantry.domain.repositories

import com.example.greenpantry.viewmodel.DeleteAccountResult

interface AuthRepository {
    suspend fun login(email: String, password: String): Boolean
    suspend fun register(email: String, username: String, password: String): Boolean
    suspend fun logout(): Boolean
    suspend fun getLoginState(): Boolean
    suspend fun deleteAccount(userPassword: String): DeleteAccountResult
}
