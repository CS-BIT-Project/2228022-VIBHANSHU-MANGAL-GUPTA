package com.example.plan_your_day

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TripAdapter(private val trips: List<Trip>) :
    RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTripName: TextView = itemView.findViewById(R.id.tvTripName)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvDestinations: TextView = itemView.findViewById(R.id.tvDestinations)
        val tvDates: TextView = itemView.findViewById(R.id.tvDates)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        val tvTravelers: TextView = itemView.findViewById(R.id.tvTravelers)
        val tvCost: TextView = itemView.findViewById(R.id.tvCost)
        val tvTransport: TextView = itemView.findViewById(R.id.tvTransport)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]

        // Prevent null values from crashing the app
        holder.tvTripName.text = trip.tripName?.trim() ?: "Unknown Trip"
        holder.tvDestinations.text = trip.destination?.trim() ?: "Unknown Destination"
        holder.tvDates.text = "${formatDate(trip.startDate)} - ${formatDate(trip.endDate)}"
        holder.tvDuration.text = "${calculateDuration(trip.startDate, trip.endDate)} days"
        holder.tvTravelers.text = "${trip.travelersCount} travelers"
        holder.tvCost.text = "${trip.currency} ${trip.budget}"
        holder.tvTransport.text = trip.transportation?.trim() ?: "Unknown Transport"

        // Determine and set the trip status
        val status = getTripStatus(trip.startDate, trip.endDate)
        holder.tvStatus.text = status

        // Set status background dynamically
        val context = holder.itemView.context
        val backgroundRes = when (status) {
            "Active" -> R.drawable.bg_status_active
            "Completed" -> R.drawable.bg_status_completed
            else -> R.drawable.bg_status_planning // Planning
        }
        holder.tvStatus.setBackgroundResource(backgroundRes)
    }

    override fun getItemCount(): Int = trips.size

    @SuppressLint("SimpleDateFormat")
    private fun calculateDuration(startDate: String?, endDate: String?): Int {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return try {
            val start = sdf.parse(startDate ?: "") ?: return 0
            val end = sdf.parse(endDate ?: "") ?: return 0
            val diff = end.time - start.time
            (diff / (1000 * 60 * 60 * 24)).toInt() + 1
        } catch (e: Exception) {
            0
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTripStatus(startDate: String?, endDate: String?): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return try {
            val start = sdf.parse(startDate ?: "") ?: return "Unknown"
            val end = sdf.parse(endDate ?: "") ?: return "Unknown"
            val currentDate = Date()

            when {
                currentDate.before(start) -> "Planning"
                currentDate.after(end) -> "Completed"
                else -> "Active"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(date: String?): String {
        return try {
            val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) // Updated format
            val parsedDate = inputFormat.parse(date ?: "")
            outputFormat.format(parsedDate!!)
        } catch (e: Exception) {
            date ?: "Unknown Date"
        }
    }
}
