package com.example.triathlon360

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_app)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        // load home by default
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        // ensure home is selected by default
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit()
                    true
                }
                R.id.nav_activity -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ActivityFragment())
                        .commit()
                    true
                }
                R.id.nav_diet -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, DietFragment())
                        .commit()
                    true
                }
                R.id.nav_more -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MoreFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}
