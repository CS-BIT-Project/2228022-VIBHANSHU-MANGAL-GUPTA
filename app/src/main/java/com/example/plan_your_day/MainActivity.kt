package com.example.plan_your_day

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.splash_screen)


        // ProgressBar Animation
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)

        // Animate ProgressBar from 0 to 100 over 3 seconds (3000ms)

        val animation = ObjectAnimator.ofInt(progressBar,"progress",0,100)
        animation.duration = 3000 // 3 seconds
        animation.start()

        // Start login activity after animation completes
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)
            finish() // Close splash screen
        }, 3000) // Delay matches animation duration

    }
}