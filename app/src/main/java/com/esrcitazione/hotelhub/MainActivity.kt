package com.esrcitazione.hotelhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.esrcitazione.hotelhub.databinding.ActivityMainBinding // Importa la classe binding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // Dichiara un'istanza della classe binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Inizializza l'istanza della classe binding
        setContentView(binding.root) // Usa l'oggetto root della classe binding per impostare il layout

        binding.registerButton.setOnClickListener {
            val intent = Intent(this, activity_registrazione::class.java)
            startActivity(intent)
        }
    }
}
