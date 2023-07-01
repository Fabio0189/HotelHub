package com.esrcitazione.hotelhub.activity

import com.esrcitazione.hotelhub.fragment.ServizioCameraFragment
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
import com.esrcitazione.hotelhub.db.ClientNetwork
import com.esrcitazione.hotelhub.db.DatabaseHelper
import com.esrcitazione.hotelhub.fragment.PrenotazioniEffettuateFragment
import com.esrcitazione.hotelhub.fragment.ProfileFragment
import com.esrcitazione.hotelhub.R
import com.esrcitazione.hotelhub.fragment.RecensioniFragment
import com.esrcitazione.hotelhub.fragment.HomeFragment
import com.esrcitazione.hotelhub.fragment.InfoFragment
import com.esrcitazione.hotelhub.fragment.PrenotaFragment
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
    private var currentFragment: Fragment? = null
    private lateinit var currentLanguage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = DatabaseHelper(this)
        //serve per tenere traccia della lingua corrente dell'app
        currentLanguage = getLanguageFromPreferences()
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
        //per nascondere inizialmente il Servizio in camera per permettere poi in seguito di renderlo visibile solo a chi è prenotato in quei giorni
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
                    setLanguage(newLanguage)
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
        //con l'id dell'utente loggato, si verifica l'esistenza della prenotazione
        verificaPrenotazioneOdierna(db.getId())

        openFragment(HomeFragment())
    }

    override fun onBackPressed() {
        if (currentFragment is HomeFragment) {
            if (isExitConfirmationVisible) {
                finishAffinity() // Chiude completamente l'app
            } else {
                isExitConfirmationVisible = true
                showToast(getString(R.string.exit_confirmation_message))
                binding.drawerLayout.postDelayed({
                    isExitConfirmationVisible = false
                }, 2000)
            }
        } else {
            supportFragmentManager.popBackStackImmediate(
                "HomeFragment",
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            currentFragment = HomeFragment()
            openFragment(currentFragment!!)
        }
    }

    public fun openFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        if (fragment !is HomeFragment) {
            fragmentTransaction.addToBackStack(null)
        }

        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()

        currentFragment = fragment
    }
    //per cambiare lingua
    private fun setLanguage(language: String) {
        currentLanguage = language
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = android.content.res.Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }
    //salva la preferenza della lingua
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
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
