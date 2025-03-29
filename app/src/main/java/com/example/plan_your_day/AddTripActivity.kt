package com.example.plan_your_day

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.plan_your_day.databinding.ActivityAddTripBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddTripActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTripBinding
    private var selectedTransportation: String? = null
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTripBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupCurrencySpinner()
        setupDatePickers()
        setupTravelersCounter()
        setupTransportationSelection()
        setupButtons()
    }

    private fun setupCurrencySpinner() {
        val currencies = arrayOf("USD", "EUR", "GBP", "JPY", "CAD", "AUD")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies)
        binding.spinnerCurrency.adapter = adapter
    }

    private fun setupDatePickers() {
        val dateSetListener = { view: android.widget.DatePicker, year: Int, month: Int, day: Int ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            val formattedDate = dateFormat.format(calendar.time)

            if (view.tag == "START") {
                binding.etStartDate.setText(formattedDate)
            } else {
                binding.etEndDate.setText(formattedDate)
            }
        }

        binding.etStartDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { view, year, month, day ->
                    view.tag = "START"
                    dateSetListener(view, year, month, day)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.etEndDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { view, year, month, day ->
                    view.tag = "END"
                    dateSetListener(view, year, month, day)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }

    private fun setupTravelersCounter() {
        var travelersCount = 1
        binding.tvTravelersCount.text = travelersCount.toString()

        binding.btnDecreaseTravelers.setOnClickListener {
            if (travelersCount > 1) {
                travelersCount--
                binding.tvTravelersCount.text = travelersCount.toString()
            }
        }

        binding.btnIncreaseTravelers.setOnClickListener {
            travelersCount++
            binding.tvTravelersCount.text = travelersCount.toString()
        }
    }

    private fun setupTransportationSelection() {
        val transportationOptions = listOf(
            binding.cvPlane to "Plane",
            binding.cvTrain to "Train",
            binding.cvCar to "Car",
            binding.cvBus to "Bus"
        )

        transportationOptions.forEach { (cardView, transportType) ->
            cardView.setOnClickListener {
                // Reset all card backgrounds
                transportationOptions.forEach { (cv, _) ->
                    cv.setCardBackgroundColor(getColor(R.color.transportation_unselected))
                }

                // Highlight selected card
                cardView.setCardBackgroundColor(getColor(R.color.transportation_selected))
                selectedTransportation = transportType
            }
        }
    }

    private fun setupButtons() {
        binding.btnCancel.setOnClickListener {
            navigateToHome()
        }

        binding.btnAddTrip.setOnClickListener {
            if (validateInputs()) {
                saveTrip()
                navigateToHome()
            }
        }

        binding.btnBack.setOnClickListener{
            val intent = Intent(this, ActivityHomepage::class.java) // Replace with your homepage activity
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    // Function to navigate to home
    private fun navigateToHome() {
        val intent = Intent(this, ActivityHomepage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish() // Close current activity
    }

    private fun validateInputs(): Boolean {
        val tripName = binding.etTripName.text.toString()
        val destination = binding.etDestination.text.toString()
        val startDate = binding.etStartDate.text.toString()
        val endDate = binding.etEndDate.text.toString()
        val budget = binding.etBudget.text.toString()

        when {
            tripName.isEmpty() -> {
                showError("Please enter a trip name")
                return false
            }
            destination.isEmpty() -> {
                showError("Please enter a destination")
                return false
            }
            startDate.isEmpty() -> {
                showError("Please select a start date")
                return false
            }
            endDate.isEmpty() -> {
                showError("Please select an end date")
                return false
            }
            budget.isEmpty() -> {
                showError("Please enter a budget")
                return false
            }
            selectedTransportation == null -> {
                showError("Please select a transportation method")
                return false
            }
        }
        return true
    }

    private fun saveTrip() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            showError("User not authenticated!")
            return
        }

        val db = FirebaseFirestore.getInstance()
        val tripId = db.collection("users").document(userId).collection("trips").document().id

        val tripData = hashMapOf(
            "tripName" to binding.etTripName.text.toString(),
            "destination" to binding.etDestination.text.toString(),
            "startDate" to binding.etStartDate.text.toString(),
            "endDate" to binding.etEndDate.text.toString(),
            "budget" to binding.etBudget.text.toString().toDoubleOrNull(),
            "currency" to binding.spinnerCurrency.selectedItem.toString(),
            "travelersCount" to binding.tvTravelersCount.text.toString().toIntOrNull(),
            "transportation" to selectedTransportation,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users").document(userId)
            .collection("trips").document(tripId)
            .set(tripData)
            .addOnSuccessListener {
                Toast.makeText(this, "Trip added successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to add trip: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}