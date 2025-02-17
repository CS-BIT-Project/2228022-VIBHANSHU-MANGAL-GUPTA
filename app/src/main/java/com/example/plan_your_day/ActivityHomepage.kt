package com.example.plan_your_day

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class ActivityHomepage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_homepage)
        checkUserAuthentication()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_profile) {
                // Open ProfileActivity when profile icon is clicked
                val intent = Intent(this, ActivitySettings::class.java)
                startActivity(intent)
                true
            } else {
                false
            }
        }
    }


    private fun checkUserAuthentication() {
        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null) {
            // Redirect to Login if user is not authenticated
            startActivity(Intent(this, ActivityLogin::class.java))
            finish() // Close Homepage to prevent coming back using Back button
        }
    }


}