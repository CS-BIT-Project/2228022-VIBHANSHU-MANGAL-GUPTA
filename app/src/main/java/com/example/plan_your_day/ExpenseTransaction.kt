package com.example.plan_your_day

import com.google.firebase.Timestamp

data class ExpenseTransaction(
    val amount: Double = 0.0,
    val category: String = "",
    val description: String = "",
    val timestamp: Timestamp? = null // Needed for Firestore sorting
)

