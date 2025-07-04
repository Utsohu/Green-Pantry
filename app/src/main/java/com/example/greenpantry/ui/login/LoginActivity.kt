package com.example.greenpantry.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.example.greenpantry.MainActivity
import com.example.greenpantry.databinding.ActivityLoginBinding

import com.example.greenpantry.R
import com.example.greenpantry.domain.model.LoginInputValidationType
import com.example.greenpantry.domain.model.validateLoginInput
import com.example.greenpantry.presentation.viewmodel.AuthViewModel
import com.example.greenpantry.ui.home.HomeFragment
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val username = findViewById<TextInputEditText>(R.id.username)
        val password = findViewById<TextInputEditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val emailError = findViewById<TextView>(R.id.emailError)
        val passError = findViewById<TextView>(R.id.passError)
        val loginError = findViewById<TextView>(R.id.loginError)

        login.setOnClickListener {
            // validation
            val results = validateLoginInput(username.text.toString(), password.text.toString())
            var emailBlank = false

            if (results.contains(LoginInputValidationType.EmptyField)) {
                if (username.text.toString().isBlank()) {
                    emailError.text = "Required"
                    emailError.visibility = View.VISIBLE
                    emailBlank = true
                } else {
                    emailError.visibility = View.GONE
                    emailBlank = false
                }
                if (password.text.toString().isBlank()) {
                    passError.text = "Required"
                    passError.visibility = View.VISIBLE
                } else {
                    passError.visibility = View.GONE
                }
            } else {
                emailBlank = false
                passError.visibility = View.GONE
            }

            if (results.contains(LoginInputValidationType.NoEmail) && !emailBlank) {
                emailError.text = "Invalid email"
                emailError.visibility = View.VISIBLE
            } else {
                if (!emailBlank) emailError.visibility = View.GONE
            }

            if (results.size == 1 && results.contains(LoginInputValidationType.Valid)) {
                viewModel.login(username.text.toString(), password.text.toString())
            }
        }

        viewModel.uiState.observe(this) { state ->
            if (state.loginSuccess) {
                // Navigate to main/home
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            state.errorMessage?.let {
                loginError.visibility = View.VISIBLE
                loginError.text = it
            } ?: run {
                loginError.visibility = View.GONE
            }
        }

        val signUp = findViewById<TextView>(R.id.signUp)
        signUp.setOnClickListener {
            val intent = Intent(this, RegisterScreen::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun TextInputEditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
