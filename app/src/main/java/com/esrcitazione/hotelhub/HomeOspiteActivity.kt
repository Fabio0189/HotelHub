package com.esrcitazione.hotelhub

import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.esrcitazione.hotelhub.databinding.ActivityHomeOspiteBinding
import com.google.android.material.datepicker.MaterialDatePicker

class HomeOspiteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeOspiteBinding
    private var isExitConfirmationVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeOspiteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT) // Or GravityCompat.START for support libraries
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuProfile -> {
                    openFragment(ProfileFragment()) // Apri il fragment Profile
                    binding.drawerLayout.closeDrawers() // Chiudi il DrawerLayout dopo il clic
                    return@setNavigationItemSelectedListener true
                }
                R.id.menuhome -> {
                    openFragment(HomeFragment()) // Apri il fragment Home
                    binding.drawerLayout.closeDrawers() // Chiudi il DrawerLayout dopo il clic
                    return@setNavigationItemSelectedListener true
                }
                // Aggiungi altre opzioni del menu qui, se necessario
                else -> return@setNavigationItemSelectedListener false
            }
        }

        // Apri il fragment Home all'avvio dell'activity
        openFragment(HomeFragment())

        setupDatePicker()
    }

    private fun setupDatePicker() {
        val datePickerBuilder = MaterialDatePicker.Builder.dateRangePicker()
        val datePicker = datePickerBuilder.build()

        binding.datePickerButton.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener {
            // Qui puoi effettuare la tua query al database per controllare la disponibilità delle camere
            // e visualizzare i risultati all'utente.
            // Ricorda che il MaterialDatePicker restituisce le date in millisecondi dall'epoca Unix.
        }
    }

    // Verifica la validità dei dettagli della carta di credito
    private fun validateCardDetails(cardNumber: String, cvv: String): Boolean {
        if (cardNumber.length != 16) {
            showToast("Inserisci un numero di carta valido.")
            return false
        }

        if (cvv.length != 3) {
            showToast("Inserisci un CVV valido.")
            return false
        }

        // Potresti aggiungere ulteriori controlli qui, se necessario

        return true
    }

    // Calcola il costo totale della prenotazione
    private fun calculateTotalCost(nights: Int, roomType: String): Double {
        val roomCost = when (roomType) {
            "single" -> 50
            "double" -> 100
            "family" -> 200
            else -> 0
        }
        return (nights * roomCost).toDouble()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isExitConfirmationVisible) {
            // Se la conferma di uscita è già visibile, chiudi l'applicazione
            finish()
        } else {
            // Altrimenti, mostra la conferma di uscita
            isExitConfirmationVisible = true
            Toast.makeText(this, "Per uscire dall'app premi indietro un altra volta", Toast.LENGTH_SHORT).show()
            // Imposta un timer per nascondere la conferma di uscita dopo 2 secondi
            binding.drawerLayout.postDelayed({
                isExitConfirmationVisible = false
            }, 2000)
        }
    }
}
