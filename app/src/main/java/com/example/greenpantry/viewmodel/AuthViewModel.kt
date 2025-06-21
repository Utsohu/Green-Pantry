package com.example.greenpantry.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greenpantry.domain.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
): ViewModel() {

    var uiState = mutableStateOf(AuthUiState())
        private set

    fun login(email: String, password: String) = viewModelScope.launch {
        uiState.value = uiState.value.copy(isLoading = true, errorMessage = null)
        val ok = repo.login(email, password)
        uiState.value = uiState.value.copy(
            isLoading    = false,
            loginSuccess = ok,
            errorMessage = if (!ok) "Login failed" else null
        )
    }

    fun register(email: String, username: String, password: String) = viewModelScope.launch {
        uiState.value = uiState.value.copy(isLoading = true, errorMessage = null)
        val ok = repo.register(email, username, password)
        uiState.value = uiState.value.copy(
            isLoading        = false,
            registerSuccess  = ok,
            errorMessage     = if (!ok) "Registration failed" else null
        )
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val registerSuccess: Boolean = false,
    val errorMessage: String? = null
)
