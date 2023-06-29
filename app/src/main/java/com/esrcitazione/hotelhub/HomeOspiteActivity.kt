package com.esrcitazione.hotelhub

import ServizioCameraFragment
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import com.esrcitazione.hotelhub.databinding.ActivityHomeOspiteBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import android.widget.Toast
import android.view.MenuItem
import com.google.gson.JsonArray
import java.util.Locale
import java.time.LocalDate
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeOspiteActivity : AppCompatActivity() {
    private lateinit var db: DatabaseHelper
    private lateinit var binding: ActivityHomeOspiteBinding
    private var isExitConfirmationVisible = false
    private lateinit var itemServizioCamera: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db= DatabaseHelper(this)

        val currentLanguage = getLanguageFromPreferences()
        val locale = Locale(currentLanguage)
        Locale.setDefault(locale)
        val config = android.content.res.Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        binding = ActivityHomeOspiteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }

        itemServizioCamera = binding.navigationView.menu.findItem(R.id.menuServizioCamera)
        itemServizioCamera.isVisible = false

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuProfile -> {
                    openFragment(ProfileFragment())
                    binding.drawerLayout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }
                R.id.menuhome -> {
                    openFragment(HomeFragment())
                    binding.drawerLayout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }
                R.id.menuPrenotazione -> {
                    openFragment(PrenotaFragment())
                    binding.drawerLayout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }
                R.id.menuLanguage -> {
                    val newLanguage = if (currentLanguage == "en") "it" else "en"
                    val locale = Locale(newLanguage)
                    Locale.setDefault(locale)

                    val config = android.content.res.Configuration()
                    config.setLocale(locale)

                    baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

                    saveLanguageToPreferences(newLanguage)

                    recreate()

                    return@setNavigationItemSelectedListener true
                }
                R.id.menuServizioCamera -> {
                    openFragment(ServizioCameraFragment())
                    binding.drawerLayout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }
                R.id.menuPrenotazioniEffettuate -> {
                    openFragment(PrenotazioniEffettuateFragment())
                    binding.drawerLayout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }
                R.id.menuRecensioni -> {
                    openFragment(RecensioniFragment())
                    binding.drawerLayout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }

                R.id.menuInfo -> {
                    openFragment(InfoFragment())
                    binding.drawerLayout.closeDrawers()
                    return@setNavigationItemSelectedListener true
                }
                R.id.menuLogOut -> {
                    val dbHelper = DatabaseHelper(this)
                    dbHelper.deleteTabella()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    return@setNavigationItemSelectedListener true
                }
                else -> return@setNavigationItemSelectedListener false
            }
        }

        verificaPrenotazioneOdierna(db.getId())

        openFragment(HomeFragment())
    }

    override fun onBackPressed() {
        if (isExitConfirmationVisible) {
            finish()
        } else {
            isExitConfirmationVisible = true
            Toast.makeText(this, "Per uscire dall'app premi indietro un altra volta", Toast.LENGTH_SHORT).show()
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

    private fun saveLanguageToPreferences(language: String) {
        val preferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("language", language)
        editor.apply()
    }

    private fun getLanguageFromPreferences(): String {
        val preferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        return preferences.getString("language", "en") ?: "en"
    }

    private fun verificaPrenotazioneOdierna(userId: Int) {
        val oggi = LocalDate.now()

        val query = """
        SELECT * FROM prenotazioni 
        WHERE id_utente = $userId 
        AND data_check_in <= '$oggi' 
        AND data_check_out >= '$oggi'
        """

        ClientNetwork.retrofit.select(query).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val resultSet = response.body()?.get("queryset") as JsonArray
                    if (resultSet.asJsonArray.size() > 0) {
                        itemServizioCamera.isVisible = true
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                // Gestisci l'errore di rete o del server qui
            }
        })
    }
}
