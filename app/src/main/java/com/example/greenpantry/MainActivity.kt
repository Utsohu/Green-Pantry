package com.example.greenpantry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.greenpantry.ui.theme.GreenPantryTheme
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GreenPantryTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = "Green Pantry",
            color = Color.Black,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(50.dp))

        // Username
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                unfocusedLabelColor = Color.LightGray,
                unfocusedIndicatorColor = Color.LightGray,
                focusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                focusedLabelColor = Color.DarkGray,
                focusedIndicatorColor = Color.DarkGray
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                unfocusedLabelColor = Color.LightGray,
                unfocusedIndicatorColor = Color.LightGray,
                focusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                focusedLabelColor = Color.DarkGray,
                focusedIndicatorColor = Color.DarkGray
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp)
        )

        // Forgot Password
        Button(
            onClick = {
                // start forgot password workflow
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color.Transparent),
        ) {
            Text(
                text = "Forgot Password?",
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(100.dp))

        // Login
        Button(
            onClick = {
                // check account validity and go to home
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color.Black),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text("Log In")

        }

        // No account
        Button(
            onClick = {
                // go to sign up page
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color.Transparent),
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Don't have an account? ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.DarkGray)) {
                        append("Sign up")
                    }
                },
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}