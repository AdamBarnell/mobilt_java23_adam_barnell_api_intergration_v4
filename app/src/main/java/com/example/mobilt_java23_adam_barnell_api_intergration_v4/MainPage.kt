package com.example.mobilt_java23_adam_barnell_api_intergration_v4

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth

class MainPage : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var logoutButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    //Main page for the 2 fragments that handles search and popular movies
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        mAuth = FirebaseAuth.getInstance()
        logoutButton = findViewById(R.id.logoutButton)
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        findViewById<Button>(R.id.navToSearchFragmentButton).setOnClickListener {
            navController.navigate(R.id.movieSearchFragment)
        }

        findViewById<Button>(R.id.navToPopularMoviesFragmentButton).setOnClickListener {
            navController.navigate(R.id.popularMoviesFragment)
        }


        logoutButton.setOnClickListener {
            logoutUser()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = (supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
