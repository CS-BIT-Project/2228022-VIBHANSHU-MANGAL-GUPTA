package com.example.plan_your_day

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plan_your_day.databinding.FragmentWeatherBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar

class WeatherFragment : Fragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WeatherViewModel
    private lateinit var forecastAdapter: ForecastAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            Snackbar.make(
                binding.root,
                "Location permission is required to show weather for your location",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

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

        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setupForecastRecyclerView()
        setupSearchBar()
        setupObservers()

        // Check for location permission and get current location
        checkLocationPermission()
    }

    private fun setupForecastRecyclerView() {
        forecastAdapter = ForecastAdapter()
        binding.forecastRecyclerView.apply {
            adapter = forecastAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupSearchBar() {
        binding.searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Show search suggestions
                viewModel.searchLocation(binding.searchInput.text.toString())
            }
        }

        binding.searchInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                s?.toString()?.let { query ->
                    if (query.length >= 3) {
                        viewModel.searchLocation(query)
                    }
                }
            }
        })

        binding.locationButton.setOnClickListener {
            checkLocationPermission()
        }
    }

    private fun setupObservers() {
        viewModel.weatherState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is WeatherState.Loading -> showLoading(true)
                is WeatherState.Success -> {
                    showLoading(false)
                    updateUI(state.data)
                }
                is WeatherState.Error -> {
                    showLoading(false)
                    showError(state.message)
                }
            }
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                results.map { "${it.name}, ${it.country}" }
            )

            // Set adapter for AutoCompleteTextView
            binding.searchInput.setAdapter(adapter)

            // Handle item selection from suggestions
            binding.searchInput.setOnItemClickListener { _, _, position, _ ->
                val selectedLocation = results[position]
                viewModel.getWeatherByCoordinates(selectedLocation.latitude, selectedLocation.longitude)
                binding.searchInput.clearFocus() // Hide keyboard after selection
            }
        }
    }


    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Snackbar.make(
                    binding.root,
                    "Location permission is required to show weather for your location",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Grant") {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                viewModel.getWeatherByCoordinates(location.latitude, location.longitude)
            } else {
                showError("Could not get your location. Please try again later.")
            }
        }.addOnFailureListener {
            showError("Failed to get your location: ${it.message}")
        }
    }

    private fun updateUI(weather: WeatherUiModel) {
        binding.apply {
            locationName.text = weather.location
            updateTime.text = weather.lastUpdated
            temperature.text = weather.temperature
            weatherDescription.text = weather.weatherDescription
            feelsLike.text = weather.feelsLike
            humidity.text = weather.humidity
            wind.text = weather.windSpeed

            // Update weather icon based on weather code
            weatherIcon.setImageResource(getWeatherIconResource(weather.weatherCode))

            // Show or hide alert
            if (weather.alert != null) {
                alertCard.visibility = View.VISIBLE
                alertText.text = weather.alert.description
            } else {
                alertCard.visibility = View.GONE
            }

            // Update forecast
            forecastAdapter.submitList(weather.forecast)
        }
    }

    private fun getWeatherIconResource(weatherCode: Int): Int {
        return when (weatherCode) {
            0 -> if (isDay()) R.drawable.ic_sunny else R.drawable.ic_clear_night
            1, 2, 3 -> if (isDay()) R.drawable.ic_partly_cloudy else R.drawable.ic_partly_cloudy_night
            45, 48 -> R.drawable.ic_foggy
            51, 53, 55, 56, 57 -> R.drawable.ic_drizzle
            61, 63, 65, 66, 67 -> R.drawable.ic_rainy
            71, 73, 75, 77 -> R.drawable.ic_snowy
            80, 81, 82 -> R.drawable.ic_rainy
            85, 86 -> R.drawable.ic_snowy
            95, 96, 99 -> R.drawable.ic_thunderstorm
            else -> if (isDay()) R.drawable.ic_sunny else R.drawable.ic_clear_night
        }
    }

    private fun isDay(): Boolean {
        // Simplified - in a real app, check the current time against sunrise/sunset
        return true
    }

    private fun showLoading(isLoading: Boolean) {
        // Implement loading UI
        if (isLoading) {
            binding.apply {
                // Show loading indicators or shimmer effect
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}