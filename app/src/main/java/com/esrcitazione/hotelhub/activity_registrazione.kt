package com.esrcitazione.hotelhub

import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esrcitazione.hotelhub.databinding.ActivityRegistrazioneBinding
import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.JsonObject
import java.util.regex.Pattern

class activity_registrazione : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrazioneBinding

    private val specialCharacters = "!\"#\$%&'()*+,-./:;"

    // per escludere i caratteri speciali non desiderati
    private val passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[$specialCharacters])[a-zA-Z0-9$specialCharacters]{8,}\$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrazioneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aggiungi questo listener per il CheckBox
        binding.cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            val method = if (isChecked) {
                HideReturnsTransformationMethod.getInstance()
            }
            else{
                PasswordTransformationMethod.getInstance()
            }
            binding.etPassword.transformationMethod = method
            binding.etConfirmPassword.transformationMethod = method
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            val nome = binding.regNome.text.toString()
            val cognome = binding.regCognome.text.toString()


            if (!isValidEmail(email)) {
                showToast("Inserisci un email valida")
            } else if (!isValidPassword(password)) {
                showToast("La password deve contenere almeno 8 caratteri, un numero e un carattere speciale.")
            } else if (password != confirmPassword) {
                showToast("La password e la conferma password non corrispondono.")
            } else {
                // Verifica se l'email esiste già nel database prima di registrare l'utente
                checkIfEmailExists(nome, cognome, email, password)
            }
        }
    }
    private fun checkIfEmailExists(nome: String, cognome: String, email: String, password: String) {
        val selectQuery = "SELECT * FROM utenti WHERE email = '$email'"

        ClientNetwork.retrofit.select(selectQuery).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val resultSet = response.body()?.get("queryset") as JsonArray
                    if (resultSet.asJsonArray.size() > 0) {
                        showToast("Esiste già un utente con questa email. Per favore usa un'altra email.")
                    } else {
                        // Se l'email non esiste nel database, registra il nuovo utente
                        registerUser(nome ,cognome, email, password)
                    }
                } else {
                    showToast("Errore durante il controllo dell'email. Riprova.")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                showToast("Errore di connessione. Riprova.")
            }
        })
    }

    private fun registerUser(nome: String, cognome: String,email: String, password: String) {
        val insertQuery = "INSERT INTO utenti (nome, cognome, email, password, tipo) VALUES ('$nome','$cognome','$email', '$password', true)"

        ClientNetwork.retrofit.insert(insertQuery).enqueue(object : Callback<JsonObject> {
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