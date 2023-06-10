package com.esrcitazione.hotelhub

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esrcitazione.hotelhub.databinding.ActivityRegistrazioneBinding //importa la classe binding
import java.util.regex.Pattern

class activity_registrazione : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrazioneBinding //dichiara un'istanza della classe binding

    private val specialCharacters = "!\"#\$%&'()*+,-./:;"

    // Modificato l'espressione regolare per escludere i caratteri speciali non desiderati
    private val passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[$specialCharacters])[a-zA-Z0-9$specialCharacters]{8,}\$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrazioneBinding.inflate(layoutInflater) //inizializza l'istanza della classe binding
        setContentView(binding.root) //usa l'oggetto root della classe binding per impostare il layout

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString() //usa la classe binding per accedere a etEmail
            val password = binding.etPassword.text.toString() //usa la classe binding per accedere a etPassword
            val confirmPassword = binding.etConfirmPassword.text.toString() //usa la classe binding per accedere a etConfirmPassword

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
