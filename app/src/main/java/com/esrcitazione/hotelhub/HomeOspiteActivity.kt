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
                // Add other menu options here, if needed
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
