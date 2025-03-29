package com.example.plan_your_day

data class Trip(
    val tripName: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val travelersCount: Int,
    val budget: Double,
    val currency: String,
    val transportation: String
)
