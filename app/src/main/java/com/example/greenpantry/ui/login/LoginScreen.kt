package com.example.greenpantry.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
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
import com.example.greenpantry.R
import com.example.greenpantry.domain.model.LoginInputValidationType
import com.example.greenpantry.domain.model.validateLoginInput
import com.example.greenpantry.ui.sharedcomponents.AuthButton
import com.example.greenpantry.ui.sharedcomponents.HeaderBackground
import com.example.greenpantry.ui.sharedcomponents.TextEntryModule
import com.example.greenpantry.ui.theme.green_primary
import com.example.greenpantry.ui.theme.light_green_background
import com.example.greenpantry.ui.theme.logo_green
import com.example.greenpantry.ui.theme.mint_green

@Composable
fun LoginScreen(
    onLoginClicked: () -> Unit,
    onRegisterClicked: () -> Unit
) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var isPasswordShown by remember { mutableStateOf(false) }
    var isLoading       by remember { mutableStateOf(false) }

    var emailError    by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

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
                leftColor  = logo_green,
                rightColor = logo_green,
                modifier   = Modifier.fillMaxSize()
            )
            Image(
                painter            = painterResource(R.drawable.logo),
                contentDescription = "The Green Pantry Logo",
                modifier           = Modifier.size(220.dp)
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
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {

            TextEntryModule(
                description    = "Email address",
                hint           = "Enter valid email",
                textValue      = email,
                onValueChanged = { email = it; emailError = null },
                textColor      = Color.Gray,
                cursorColor    = green_primary,
                leadingIcon    = Icons.Default.Email
            )
            emailError?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }


            TextEntryModule(
                description            = "Password",
                hint                   = "Enter password",
                textValue              = password,
                onValueChanged         = { password = it; passwordError = null },
                textColor              = Color.Gray,
                cursorColor            = green_primary,
                trailingIcon           = Icons.Default.Email,
                onTrailingIconClick    = { isPasswordShown = !isPasswordShown },
                visualTransformation   = if (isPasswordShown)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardType           = KeyboardType.Password
            )
            passwordError?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }


            AuthButton(
                text            = "Login",
                backgroundColor = green_primary,
                contentColor    = Color.White,
                enabled         = !isLoading,
                isLoading       = isLoading,
                modifier        = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .shadow(5.dp, RoundedCornerShape(25.dp)),
                onButtonClick   = {

                    emailError = null; passwordError = null


                    val results = validateLoginInput(email, password)


                    if (results.contains(LoginInputValidationType.EmptyField)) {
                        if (email.isBlank())    emailError = "Required"
                        if (password.isBlank()) passwordError = "Required"
                    }
                    if (results.contains(LoginInputValidationType.NoEmail)) {
                        emailError = "Invalid email"
                    }


                    if (results.size == 1 && results.contains(LoginInputValidationType.Valid)) {
                        isLoading = true
                        onLoginClicked()
                    }
                }
            )
        }


        Row(
            modifier           = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "No account yet?",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "Register",
                modifier = Modifier.clickable { onRegisterClicked() },
                color    = green_primary,
                fontWeight = FontWeight.Bold,
                style    = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
