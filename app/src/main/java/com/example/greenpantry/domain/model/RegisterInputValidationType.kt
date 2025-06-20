package com.example.greenpantry.domain.model

enum class RegisterInputValidationType {
    EmptyField,
    NoEmail,
    UsernameTooLong,
    UsernameTooShort,
    PasswordsDoNotMatch,
    PasswordUpperCaseMissing,
    PasswordNumberMissing,
    PasswordSpecialCharMissing,
    PasswordTooShort,
    Valid
}

fun validateRegisterInput(
    email: String,
    username: String,
    password: String,
    confirm: String
): List<RegisterInputValidationType> {
    val errors = mutableListOf<RegisterInputValidationType>()
    if (email.isBlank() || username.isBlank() || password.isBlank() || confirm.isBlank())
        errors.add(RegisterInputValidationType.EmptyField)
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        errors.add(RegisterInputValidationType.NoEmail)
    if (username.length < 3)
        errors.add(RegisterInputValidationType.UsernameTooShort)
    if (username.length > 20)
        errors.add(RegisterInputValidationType.UsernameTooLong)
    if (password != confirm)
        errors.add(RegisterInputValidationType.PasswordsDoNotMatch)
    if (password.length < 8)
        errors.add(RegisterInputValidationType.PasswordTooShort)
    if (!password.any { it.isUpperCase() })
        errors.add(RegisterInputValidationType.PasswordUpperCaseMissing)
    if (!password.any { it.isDigit() })
        errors.add(RegisterInputValidationType.PasswordNumberMissing)
    if (!password.any { !it.isLetterOrDigit() })
        errors.add(RegisterInputValidationType.PasswordSpecialCharMissing)
    if (errors.isEmpty()) errors.add(RegisterInputValidationType.Valid)
    return errors
}