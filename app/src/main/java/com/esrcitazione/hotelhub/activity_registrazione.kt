package com.esrcitazione.hotelhub

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esrcitazione.hotelhub.databinding.ActivityRegistrazioneBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.JsonObject
import java.util.regex.Pattern

class activity_registrazione : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrazioneBinding

    private val specialCharacters = "!\"#\$%&'()*+,-./:;"

    // Modificato l'espressione regolare per escludere i caratteri speciali non desiderati
    private val passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[$specialCharacters])[a-zA-Z0-9$specialCharacters]{8,}\$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrazioneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (!isValidEmail(email)) {
                showToast("Inserisci un email valida")
            } else if (!isValidPassword(password)) {
                showToast("La password deve contenere almeno 8 caratteri, un numero e un carattere speciale.")
            } else if (password != confirmPassword) {
                showToast("La password e la conferma password non corrispondono.")
            } else {
                // Registra il nuovo utente
                registerUser(email, password)
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        val query = "INSERT INTO users (email, password) VALUES ('$email', '$password')"

        ClientNetwork.retrofit.insert(query).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    showToast("Registrazione effettuata con successo!")
                } else {
                    showToast("Registrazione fallita. Riprova.")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                showToast("Errore di connessione. Riprova.")
            }
        })
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
