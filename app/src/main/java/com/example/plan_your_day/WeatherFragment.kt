package com.example.plan_your_day

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plan_your_day.databinding.FragmentWeatherBinding

class WeatherFragment : Fragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private lateinit var forecastAdapter: ForecastAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupForecastRecyclerView()
        setupSearchBar()
    }

    private fun setupForecastRecyclerView() {
        forecastAdapter = ForecastAdapter()
        binding.forecastRecyclerView.apply {
            adapter = forecastAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        // Add sample forecast data
        forecastAdapter.submitList(getSampleForecastData())
    }

    private fun setupSearchBar() {
        binding.searchInput.setOnClickListener {
            // Handle search click
        }
        binding.locationButton.setOnClickListener {
            // Handle location button click
        }
    }

    private fun getSampleForecastData(): List<ForecastItem> {
        return listOf(
            ForecastItem("Mon", "Jun 12", "sunny", 24, 18),
            ForecastItem("Tue", "Jun 13", "cloudy", 22, 16),
            ForecastItem("Wed", "Jun 14", "rainy", 19, 15),
            // Add more items...
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class ForecastItem(
    val day: String,
    val date: String,
    val weather: String,
    val highTemp: Int,
    val lowTemp: Int
)