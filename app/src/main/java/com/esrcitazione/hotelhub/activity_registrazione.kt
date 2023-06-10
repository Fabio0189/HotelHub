package com.esrcitazione.hotelhub

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.regex.Pattern

class activity_registrazione : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button

    private val specialCharacters = "!\"#\$%&'()*+,-./:;"

    // Modificato l'espressione regolare per escludere i caratteri speciali non desiderati
    private val passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[$specialCharacters])[a-zA-Z0-9$specialCharacters]{8,}\$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrazione)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (!isValidEmail(email)) {
                showToast("Inserisci un email valida")
            } else if (!isValidPassword(password)) {
                showToast("La password deve contenere almeno 8 caratteri, un numero e un carattere speciale.")
            } else if (password != confirmPassword) {
                showToast("La password e la conferma password non corrispondono.")
            } else {
                // TODO: Qui dovresti creare il nuovo account
                showToast("Account creato con successo!")
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return passwordPattern.matcher(password).matches()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

