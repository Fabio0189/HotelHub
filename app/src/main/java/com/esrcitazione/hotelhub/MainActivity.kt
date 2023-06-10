package com.esrcitazione.hotelhub

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esrcitazione.hotelhub.databinding.ActivityMainBinding
import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.JsonObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val intent = Intent(this, activity_registrazione::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.Username.text.toString()
            val password = binding.password.text.toString()

            if (!isValidEmail(email)) {
                showToast("Inserisci un email valida")
            } else if (!isValidPassword(password)) {
                showToast("La password non è valida.")
            } else {
                // Effettua il login dell'utente
                loginUser(email, password)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val selectQuery = "SELECT * FROM utente WHERE email = '$email' AND password = '$password'"

        ClientNetwork.retrofit.select(selectQuery).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val resultSet = response.body()?.get("queryset") as JsonArray
                    if (resultSet.asJsonArray.size() > 0) {
                        val userType = resultSet.asJsonArray[0].asJsonObject.get("tipo").asBoolean
                        if (userType) {
                            // Avvia la Activity "home_ospite"
                            val intent = Intent(this@MainActivity, HomeOspiteActivity::class.java)
                            startActivity(intent)
                        } else {
                            // Avvia un'altra Activity, a seconda del tipo di utente
                        }
                    } else {
                        showToast("Nessun utente corrispondente trovato.")
                    }
                } else {
                    showToast("Errore durante il login. Riprova.")
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
        // Aggiungi qui il tuo controllo sulla password
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

