package com.example.plan_your_day

data class Trip(
    val tripName: String = "",
    val destination: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val travelersCount: Int = 0,
    val budget: Double = 0.0,
    val currency: String = "",
    val transportation: String = ""
) {
    // Firestore requires an empty constructor
    constructor() : this("", "", "", "", 0, 0.0, "", "")
}
