package com.example.greenpantry.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import androidx.lifecycle.ViewModelProvider
import com.example.greenpantry.MainActivity
import com.example.greenpantry.R
import com.example.greenpantry.databinding.ActivityLoginBinding
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
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterScreen : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        val username = findViewById<TextInputEditText>(R.id.username)
        val email = findViewById<TextInputEditText>(R.id.email)
        val password = findViewById<TextInputEditText>(R.id.password)
        val confirm = findViewById<TextInputEditText>(R.id.confirmPassword)
        val login = findViewById<TextView>(R.id.login)

        val register = findViewById<Button>(R.id.register)

        val emailError = findViewById<TextView>(R.id.emailError)
        val nameError = findViewById<TextView>(R.id.nameError)
        val passError = findViewById<TextView>(R.id.passError)
        val mismatch = findViewById<TextView>(R.id.mismatch)

        viewModel.uiState.observe(this) { state ->
            if (state.registerSuccess) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else if (state.errorMessage != null) {
                Toast.makeText(this, state.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        register.setOnClickListener {
            val errors = validateRegisterInput(email.text.toString(), username.text.toString(), password.text.toString(), confirm.text.toString())
            var emailBlank = false
            var nameBlank = false
            var passBlank = false
            var confirmBlank = false

            if (errors.contains(RegisterInputValidationType.EmptyField)) {
                if (email.text.toString().isBlank()) {
                    emailError.text = "Required"
                    emailError.visibility = View.VISIBLE
                    emailBlank = true
                } else {
                    emailError.visibility = View.GONE
                    emailBlank = false
                }

                if (username.text.toString().isBlank()) {
                    nameError.text = "Required"
                    nameError.visibility = View.VISIBLE
                    nameBlank = true
                } else {
                    nameError.visibility = View.GONE
                    nameBlank = false
                }

                if (password.text.toString().isBlank()) {
                    passError.text = "Required"
                    passError.visibility = View.VISIBLE
                    passBlank = true
                } else {
                    passError.visibility = View.GONE
                    passBlank = false
                }

                if (confirm.text.toString().isBlank()) {
                    mismatch.text = "Required"
                    mismatch.visibility = View.VISIBLE
                    confirmBlank = true
                } else {
                    mismatch.visibility = View.GONE
                    confirmBlank = false
                }
            }

            if (errors.contains(RegisterInputValidationType.NoEmail)) {
                emailError.text = "Enter a valid email"
                emailError.visibility = View.VISIBLE
            } else {
                if (!emailBlank) emailError.visibility = View.GONE
            }

            if (errors.contains(RegisterInputValidationType.UsernameTooShort)) {
                nameError.text = "Too short"
                nameError.visibility = View.VISIBLE
            } else if (errors.contains(RegisterInputValidationType.UsernameTooLong)) {
                nameError.text = "Too long"
                nameError.visibility = View.VISIBLE
            } else {
                if (!nameBlank) nameError.visibility = View.GONE
            }

            if (errors.contains(RegisterInputValidationType.PasswordsDoNotMatch)) {
                mismatch.text = "Do not match"
                mismatch.visibility = View.VISIBLE
            } else {
                if (!confirmBlank) mismatch.visibility = View.GONE
            }

            if (errors.contains(RegisterInputValidationType.PasswordTooShort)) {
                passError.text = "At least 8 chars"
                passError.visibility = View.VISIBLE
            } else if (errors.contains(RegisterInputValidationType.PasswordUpperCaseMissing))  {
                passError.text = "Needs uppercase"
                passError.visibility = View.VISIBLE
            } else if (errors.contains(RegisterInputValidationType.PasswordNumberMissing)) {
                passError.text = "Needs number"
                passError.visibility = View.VISIBLE
            } else if (errors.contains(RegisterInputValidationType.PasswordSpecialCharMissing)) {
                passError.text = "Needs symbol"
                passError.visibility = View.VISIBLE
            } else {
                if (!passBlank) passError.visibility = View.GONE
            }

            if (errors.size == 1 && errors.contains(RegisterInputValidationType.Valid)) {
                viewModel.register(email.text.toString(), username.text.toString(), password.text.toString())
            }

        }

        login.setOnClickListener {
            val intent = Intent(this@RegisterScreen, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}

