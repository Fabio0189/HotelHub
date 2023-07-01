package com.esrcitazione.hotelhub.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.esrcitazione.hotelhub.db.DatabaseHelper
import com.esrcitazione.hotelhub.R
import com.esrcitazione.hotelhub.fragment.ServizioAdminFragment
import com.esrcitazione.hotelhub.databinding.ActivityHomeAdminBinding
import com.esrcitazione.hotelhub.databinding.ActivityHomeAdminBinding.inflate
import com.esrcitazione.hotelhub.fragment.prenotazioniAdminFragment

class HomeAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)

        val prenotazioniFragment = prenotazioniAdminFragment()
        supportFragmentManager.beginTransaction()
            .replace(binding.AdminFragmentContainer.id, prenotazioniFragment)
            .commit()

        binding.logout2.setOnClickListener {
            val dbHelper = DatabaseHelper(this)
            dbHelper.deleteTabella()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomMenuPrenotazioni -> {
                    val prenotazioniFragment = prenotazioniAdminFragment()
                    openFragment(prenotazioniFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottomMenuServizio -> {
                    val servizioFragment = ServizioAdminFragment()
                    openFragment(servizioFragment)
                    return@setOnNavigationItemSelectedListener true
                }

                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }
    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.AdminFragmentContainer.id, fragment)
            .commit()
    }

}
