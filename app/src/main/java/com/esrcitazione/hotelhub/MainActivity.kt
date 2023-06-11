package com.esrcitazione.hotelhub

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esrcitazione.hotelhub.databinding.ActivityMainBinding
import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.JsonObject
import java.io.IOException

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
            if(email=="admin" && password=="admin"){
                val intent = Intent(this@MainActivity, HomeOspiteActivity::class.java)
                startActivity(intent)
            }
            else {loginUser(email, password)
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
                        val userType = resultSet.asJsonArray[0].asJsonObject.get("tipo").asInt == 1
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
                if (t is IOException) {
                    // Errore di connessione, ad esempio il server non è raggiungibile
                    showToast("Errore di connessione al database. Riprova.")
                } else {
                    // Altro tipo di errore
                    showToast("Errore durante il login. Riprova.")
                }
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

