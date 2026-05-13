package com.example.senzori_kot_sqlite_tema3

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)

        // primul ecran care apare atunci cand se deschide aplicatia este implicit sensors data
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SensorsFragment()).commit()

        // asculta clickurile pe butoanele de jos
        bottomNav.setOnItemSelectedListener { item ->
            var fragmentSelectat: androidx.fragment.app.Fragment = SensorsFragment()

            when(item.itemId) {
                R.id.nav_sensors -> fragmentSelectat = SensorsFragment()
                R.id.nav_visualization -> fragmentSelectat = VisualizationFragment()
                R.id.nav_api -> fragmentSelectat = ApiConfigFragment()
            }

            // inlocuim fragmentul vechi cu cel nou selectat
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragmentSelectat).commit()
            true
        }
    }
}