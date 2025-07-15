package com.example.greenpantry.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greenpantry.domain.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.map
import androidx.lifecycle.distinctUntilChanged

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
): ViewModel() {

    private val _uiState = MutableLiveData<AuthUiState>()
    val uiState: LiveData<AuthUiState> = _uiState

    val loginSuccess: LiveData<Boolean> = uiState
        .map { it.loginSuccess }
        .distinctUntilChanged()

    init {
        _uiState.value = AuthUiState() // initial state
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _uiState.value = _uiState.value?.copy(isLoading = true, errorMessage = null)
        val ok = repo.login(email, password)
        _uiState.value = _uiState.value?.copy(
            isLoading    = false,
            loginSuccess = ok,
            errorMessage = if (!ok) "Login failed" else null
        )
    }

    fun register(email: String, username: String, password: String) = viewModelScope.launch {
        _uiState.value = _uiState.value?.copy(isLoading = true, errorMessage = null)
        val ok = repo.register(email, username, password)
        _uiState.value = _uiState.value?.copy(
            isLoading        = false,
            registerSuccess  = ok,
            errorMessage     = if (!ok) "Registration failed" else null
        )
    }

    fun logout() = viewModelScope.launch {
        _uiState.value = _uiState.value?.copy(isLoading = true, errorMessage = null)
        val ok = repo.logout()
        _uiState.value = _uiState.value?.copy(
            isLoading = false,
            logoutSuccess = ok,
            errorMessage = if (!ok) "Logout failed" else null
        )
    }

    fun reauthenticateUser(password: String, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val ok = repo.reauthenticate(password)
        onResult(ok)
    }

    fun updateEmail(newEmail: String) = viewModelScope.launch {
        val ok = repo.updateEmail(newEmail)
        // optionally update UI state here
    }

    fun updatePassword(newPassword: String) = viewModelScope.launch {
        val ok = repo.updatePassword(newPassword)
    }

    fun updateUsername(newUsername: String) = viewModelScope.launch {
        val ok = repo.updateUsername(newUsername)
    }

}

data class AuthUiState(
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val registerSuccess: Boolean = false,
    val logoutSuccess: Boolean = false,
    val errorMessage: String? = null
)
