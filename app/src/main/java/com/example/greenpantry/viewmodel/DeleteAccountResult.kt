package com.example.greenpantry.viewmodel

sealed class DeleteAccountResult {
    object Success      : DeleteAccountResult()
    object NotLoggedIn  : DeleteAccountResult()
    object WrongPassword: DeleteAccountResult()
    data class Error(val message: String): DeleteAccountResult()
}
