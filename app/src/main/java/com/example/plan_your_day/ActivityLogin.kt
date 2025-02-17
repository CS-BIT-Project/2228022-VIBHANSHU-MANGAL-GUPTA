package com.example.plan_your_day

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plan_your_day.databinding.ActivityLoginBinding
import com.example.plan_your_day.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class ActivityLogin : AppCompatActivity() {

    private  lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        // Navigate to Signup Activity
        binding.textViewSignUp.setOnClickListener {
            startActivity(Intent(this, ActivitySignup::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        binding.buttonLogin.setOnClickListener{
            val email = binding.editTextEmail.text.toString().trim()
            val pass = binding.editTextPassword.text.toString().trim()

            if (isValidInput(email, pass)) {
                loginUser(email, pass)
            }
        }
    }
    private fun isValidInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() || password.isEmpty() -> {
                showToast("Empty fields are not allowed")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Please enter a valid email address")
                false
            }
            password.length < 6 -> {
                showToast("Password must be at least 6 characters")
                false
            }
            else -> true
        }
    }

    private fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, ActivityHomepage::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish() // Prevent going back to login screen
                } else {
                    showToast(task.exception?.localizedMessage ?: "Login failed")
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}