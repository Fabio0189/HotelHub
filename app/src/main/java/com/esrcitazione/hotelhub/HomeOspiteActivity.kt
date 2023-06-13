package com.esrcitazione.hotelhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import com.esrcitazione.hotelhub.databinding.ActivityHomeOspiteBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import android.widget.Toast
import java.util.Locale

class HomeOspiteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeOspiteBinding
    private var isExitConfirmationVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeOspiteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuProfile -> {
                    openFragment(ProfileFragment()) // Open Profile fragment
                    binding.drawerLayout.closeDrawers() // Close DrawerLayout after click
                    return@setNavigationItemSelectedListener true
                }
                R.id.menuhome -> {
                    openFragment(HomeFragment()) // Open Home fragment
                    binding.drawerLayout.closeDrawers() // Close DrawerLayout after click
                    return@setNavigationItemSelectedListener true
                }
                R.id.menuPrenotazione -> {
                    openFragment(PrenotaFragment()) // Open Prenota fragment
                    binding.drawerLayout.closeDrawers() // Close DrawerLayout after click
                    return@setNavigationItemSelectedListener true
                }
                R.id.menuLanguage -> {
                    // Create a new Locale object for the language you want to switch to
                    val locale = if (Locale.getDefault().language == "en") Locale("it") else Locale("en")
                    Locale.setDefault(locale)

                    // Create a new configuration object and set its locale
                    val config = android.content.res.Configuration()
                    config.setLocale(locale)

                    // Update the configuration of the app context with the new Configuration object
                    baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

                    // Recreate the activity to apply the new language
                    recreate()

                    return@setNavigationItemSelectedListener true
                }
                else -> return@setNavigationItemSelectedListener false
            }
        }

        // Open Home fragment on activity start
        openFragment(HomeFragment())
    }

    override fun onBackPressed() {
        if (isExitConfirmationVisible) {
            // If exit confirmation is already visible, close the application
            finish()
        } else {
            // Otherwise, show exit confirmation
            isExitConfirmationVisible = true
            Toast.makeText(this, "Per uscire dall'app premi indietro un altra volta", Toast.LENGTH_SHORT).show()
            // Set a timer to hide exit confirmation after 2 seconds
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
