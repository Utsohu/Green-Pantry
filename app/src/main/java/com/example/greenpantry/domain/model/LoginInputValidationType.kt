package com.example.greenpantry.domain.model
import android.util.Patterns

enum class LoginInputValidationType {
    EmptyField,
    NoEmail,
    Valid
}

fun validateLoginInput(
    email: String,
    password: String
): List<LoginInputValidationType> {
    val errors = mutableListOf<LoginInputValidationType>()
    if (email.isBlank() || password.isBlank()) {
        errors.add(LoginInputValidationType.EmptyField)
    }
    if (email.isNotBlank() &&
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        errors.add(LoginInputValidationType.NoEmail)
    }
    if (errors.isEmpty()) {
        errors.add(LoginInputValidationType.Valid)
    }
    return errors
}