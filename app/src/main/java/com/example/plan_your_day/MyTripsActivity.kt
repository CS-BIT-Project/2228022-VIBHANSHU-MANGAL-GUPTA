package com.example.plan_your_day

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyTripsActivity : AppCompatActivity() {

    private lateinit var recyclerViewTrips: RecyclerView
    private lateinit var tripAdapter: TripAdapter
    private val tripsList = mutableListOf<Trip>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_trips)

        recyclerViewTrips = findViewById(R.id.recyclerViewTrips)
        recyclerViewTrips.layoutManager = LinearLayoutManager(this)

        // Initialize adapter once and set it to RecyclerView
        tripAdapter = TripAdapter(tripsList)
        recyclerViewTrips.adapter = tripAdapter

        fetchTripsFromFirestore()
    }

    private fun fetchTripsFromFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not authenticated!", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).collection("trips")
            .get()  // Removed orderBy("timestamp") unless timestamp exists
            .addOnSuccessListener { documents ->
                tripsList.clear()
                for (document in documents) {
                    val trip = document.toObject(Trip::class.java)
                    if (trip != null) {  // Ensure trip is not null
                        tripsList.add(trip)
                    }
                }
                tripAdapter.notifyDataSetChanged() // Update adapter without recreating it
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching trips", e)
                Toast.makeText(this, "Failed to fetch trips", Toast.LENGTH_SHORT).show()
            }
    }
}
