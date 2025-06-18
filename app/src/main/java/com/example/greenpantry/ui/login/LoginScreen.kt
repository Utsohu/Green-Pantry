package com.example.greenpantry.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
//import androidx.compose.material.icons.filled.VpnKey
//import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.greenpantry.ui.sharedcomponents.*
import com.example.greenpantry.ui.theme.green_primary
import com.example.greenpantry.ui.theme.light_green_background
import com.example.greenpantry.ui.theme.mint_green

@Composable
fun LoginScreen(
    onLoginClicked: () -> Unit,
    onRegisterClicked: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordShown by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            HeaderBackground(
                leftColor = green_primary,
                rightColor = mint_green,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = "\nThe Green Pantry",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 200.dp)
                .fillMaxWidth(0.9f)
                .shadow(5.dp, RoundedCornerShape(15.dp))
                .background(light_green_background, RoundedCornerShape(15.dp))
                .padding(10.dp, 15.dp, 10.dp, 5.dp)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            TextEntryModule(
                description = "Email address",
                hint = "Enter valid email",
                textValue = email,
                onValueChanged = { email = it },
                textColor = Color.Gray,
                cursorColor = green_primary,
                leadingIcon = Icons.Default.Email,
            )

            TextEntryModule(
                description = "Password",
                hint = "Enter password",
                textValue = password,
                onValueChanged = { password = it },
                textColor = Color.Gray,
                cursorColor = green_primary,
                //leadingIcon = Icons.Default.Add, // TODO: edit later
                trailingIcon = Icons.Default.Add, // TODO: edit later
                onTrailingIconClick = { isPasswordShown = !isPasswordShown },
                visualTransformation = if (isPasswordShown) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password
            )

            AuthButton(
                text = "Login",
                backgroundColor = green_primary,
                contentColor = Color.White,
                enabled = email.isNotBlank() && password.isNotBlank(),
                isLoading = isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .shadow(5.dp, RoundedCornerShape(25.dp)),
                onButtonClick = {
                    isLoading = true
                    // Simulate login logic or just invoke callback
                    onLoginClicked()
                }
            )

            Text(
                error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Row(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "No account yet?",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Register",
                modifier = Modifier
                    .padding(start = 5.dp)
                    .clickable { onRegisterClicked() },
                color = green_primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
