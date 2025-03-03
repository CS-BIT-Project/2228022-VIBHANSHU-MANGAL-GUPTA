package com.example.plan_your_day

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.*

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WeatherRepository()
    private val geocoder = Geocoder(application, Locale.getDefault())

    private val _weatherState = MutableLiveData<WeatherState>()
    val weatherState: LiveData<WeatherState> = _weatherState

    private val _searchResults = MutableLiveData<List<GeocodingResult>>()
    val searchResults: LiveData<List<GeocodingResult>> = _searchResults

    init {
        _weatherState.value = WeatherState.Loading
    }

    fun getWeatherByCoordinates(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading

            // Get location name from coordinates
            val locationName = getLocationName(latitude, longitude)

            val result = repository.getWeatherByCoordinates(latitude, longitude, locationName)
            _weatherState.value = result.fold(
                onSuccess = { WeatherState.Success(it) },
                onFailure = { WeatherState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    fun searchLocation(query: String) {
        if (query.length < 3) return

        viewModelScope.launch {
            val result = repository.searchLocation(query)
            result.fold(
                onSuccess = { _searchResults.value = it },
                onFailure = { /* Handle error */ }
            )
        }
    }

    private fun getLocationName(latitude: Double, longitude: Double): String {
        return try {
            @Suppress("DEPRECATION") // For simplicity, using deprecated API
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.isNullOrEmpty()) {
                "Unknown Location"
            } else {
                val address = addresses[0]
                val city = address.locality ?: address.subAdminArea ?: ""
                val country = address.countryName ?: ""
                if (city.isNotEmpty() && country.isNotEmpty()) {
                    "$city, $country"
                } else {
                    country.ifEmpty { "Unknown Location" }
                }
            }
        } catch (e: Exception) {
            "Unknown Location"
        }
    }
}

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val data: WeatherUiModel) : WeatherState()
    data class Error(val message: String) : WeatherState()
}