package com.esrcitazione.hotelhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import com.esrcitazione.hotelhub.databinding.ActivityHomeOspiteBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import android.widget.Toast

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

    private fun openFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}
