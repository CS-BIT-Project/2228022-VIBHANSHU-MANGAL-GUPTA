package com.example.plan_your_day

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.plan_your_day.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ActivitySignup : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ViewBinding Initialization
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Navigate to Login Activity
        binding.textViewSignIn.setOnClickListener {
            startActivity(Intent(this, ActivityLogin::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            finish()
        }

        binding.buttonSignup.setOnClickListener {
            val userName = binding.edittextName.text.toString().trim()
            val email = binding.edittextEmail.text.toString().trim()
            val pass = binding.edittextPassword.text.toString().trim()
            val confirmPass = binding.edittextConfirmPass.text.toString().trim()

            if (isValidInput(userName, email, pass, confirmPass)) {
                registerUser(userName, email, pass)
            }
        }
    }

    private fun isValidInput(user: String, email: String, pass: String, confirmPass: String): Boolean {
        return when {
            user.isEmpty() || email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty() -> {
                showToast("Empty fields are not allowed")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Please enter a valid email address")
                false
            }
            pass.length < 6 -> {
                showToast("Password must be at least 6 characters")
                false
            }
            pass != confirmPass -> {
                showToast("Passwords do not match")
                false
            }
            else -> true
        }
    }

    private fun registerUser(userName: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let {
                        saveUserDataToFirestore(it.uid, userName, email)
                    }
                } else {
                    showToast(task.exception?.localizedMessage ?: "Registration failed")
                }
            }
    }

    private fun saveUserDataToFirestore(uid: String, userName: String, email: String) {
        val userData = hashMapOf(
            "uid" to uid,
            "name" to userName,
            "email" to email
        )

        firestore.collection("users").document(uid)
            .set(userData)
            .addOnSuccessListener {
                showToast("Registration successful!")
                startActivity(Intent(this, ActivityLogin::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
            .addOnFailureListener { e ->
                showToast("Failed to save user data: ${e.message}")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
