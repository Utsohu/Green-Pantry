package com.example.greenpantry.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.greenpantry.R
import com.example.greenpantry.domain.model.RegisterInputValidationType
import com.example.greenpantry.domain.model.validateRegisterInput
import com.example.greenpantry.presentation.viewmodel.AuthViewModel
import com.example.greenpantry.presentation.viewmodel.AuthUiState
import com.example.greenpantry.ui.sharedcomponents.AuthButton
import com.example.greenpantry.ui.sharedcomponents.HeaderBackground
import com.example.greenpantry.ui.sharedcomponents.TextEntryModule
import com.example.greenpantry.ui.theme.green_primary
import com.example.greenpantry.ui.theme.light_green_background
import com.example.greenpantry.ui.theme.logo_green

@Composable
fun RegisterScreen(
    onNavigateHome: () -> Unit,
    onNavigateLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordShown by remember { mutableStateOf(false) }
    var isConfirmShown by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            onNavigateHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            HeaderBackground(
                leftColor = logo_green,
                rightColor = logo_green,
                modifier = Modifier.fillMaxSize()
            )
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "The Green Pantry Logo",
                modifier = Modifier.size(220.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 260.dp)
                .fillMaxWidth(0.9f)
                .shadow(5.dp, RoundedCornerShape(15.dp))
                .background(light_green_background, RoundedCornerShape(15.dp))
                .padding(16.dp)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            TextEntryModule(
                description = "Email address",
                hint = "Enter valid email",
                textValue = email,
                onValueChanged = {
                    email = it
                    emailError = null
                },
                textColor = Color.Gray,
                cursorColor = green_primary,
                leadingIcon = Icons.Default.Email
            )
            emailError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }

            TextEntryModule(
                description = "Username",
                hint = "Choose a username",
                textValue = username,
                onValueChanged = {
                    username = it
                    usernameError = null
                },
                textColor = Color.Gray,
                cursorColor = green_primary,
                leadingIcon = Icons.Default.Add
            )
            usernameError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }

            TextEntryModule(
                description = "Password",
                hint = "Enter password",
                textValue = password,
                onValueChanged = {
                    password = it
                    passwordError = null
                },
                textColor = Color.Gray,
                cursorColor = green_primary,
                trailingIcon = Icons.Default.Add,
                onTrailingIconClick = { isPasswordShown = !isPasswordShown },
                visualTransformation = if (isPasswordShown) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password
            )
            passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }

            TextEntryModule(
                description = "Confirm Password",
                hint = "Repeat password",
                textValue = confirmPassword,
                onValueChanged = {
                    confirmPassword = it
                    confirmError = null
                },
                textColor = Color.Gray,
                cursorColor = green_primary,
                trailingIcon = Icons.Default.Add,
                onTrailingIconClick = { isConfirmShown = !isConfirmShown },
                visualTransformation = if (isConfirmShown) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password
            )
            confirmError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }

            uiState.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

            AuthButton(
                text = "Register",
                backgroundColor = green_primary,
                contentColor = Color.White,
                enabled = !uiState.isLoading,
                isLoading = uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .shadow(5.dp, RoundedCornerShape(25.dp)),
                onButtonClick = {
                    emailError = null; usernameError = null; passwordError = null; confirmError = null
                    val errors = validateRegisterInput(email, username, password, confirmPassword)
                    if (errors.contains(RegisterInputValidationType.EmptyField)) {
                        if (email.isBlank()) emailError = "Required"
                        if (username.isBlank()) usernameError = "Required"
                        if (password.isBlank()) passwordError = "Required"
                        if (confirmPassword.isBlank()) confirmError = "Required"
                    }
                    if (errors.contains(RegisterInputValidationType.NoEmail)) emailError = "Enter a valid email"
                    if (errors.contains(RegisterInputValidationType.UsernameTooShort)) usernameError = "Too short"
                    if (errors.contains(RegisterInputValidationType.UsernameTooLong)) usernameError = "Too long"
                    if (errors.contains(RegisterInputValidationType.PasswordsDoNotMatch)) confirmError = "Do not match"
                    if (errors.contains(RegisterInputValidationType.PasswordTooShort)) passwordError = "At least 8 chars"
                    if (errors.contains(RegisterInputValidationType.PasswordUpperCaseMissing)) passwordError = "Needs uppercase"
                    if (errors.contains(RegisterInputValidationType.PasswordNumberMissing)) passwordError = "Needs number"
                    if (errors.contains(RegisterInputValidationType.PasswordSpecialCharMissing)) passwordError = "Needs symbol"
                    if (errors.size == 1 && errors.contains(RegisterInputValidationType.Valid)) {
                        viewModel.register(email, username, password)
                    }
                }
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Have an account?", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(4.dp))
            Text(
                "Login",
                modifier = Modifier.clickable { onNavigateLogin() },
                color = green_primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
