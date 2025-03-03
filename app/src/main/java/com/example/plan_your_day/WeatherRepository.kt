package com.example.plan_your_day

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WeatherRepository {
    private val weatherApiService = RetrofitClient.weatherApiService
    private val geocodingApiService = RetrofitClient.geocodingApiService

    suspend fun getWeatherByCoordinates(latitude: Double, longitude: Double, locationName: String): Result<WeatherUiModel> {
        return withContext(Dispatchers.IO) {
            try {
                val response = weatherApiService.getWeather(latitude, longitude)
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    if (weatherData != null) {
                        Result.success(mapToWeatherUiModel(weatherData, locationName))
                    } else {
                        Result.failure(Exception("Empty response"))
                    }
                } else {
                    Result.failure(Exception("API error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun searchLocation(query: String): Result<List<GeocodingResult>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = geocodingApiService.searchLocation(query)
                if (response.isSuccessful) {
                    val results = response.body()?.results ?: emptyList()
                    Result.success(results)
                } else {
                    Result.failure(Exception("API error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun mapToWeatherUiModel(weatherData: WeatherResponse, locationName: String): WeatherUiModel {
        val current = weatherData.current
        val daily = weatherData.daily

        // Format the forecast items
        val forecastItems = mutableListOf<ForecastItem>()
        for (i in daily.time.indices) {
            val date = parseDate(daily.time[i])
            forecastItems.add(
                ForecastItem(
                    day = getDayOfWeek(date),
                    date = getFormattedDate(date),
                    weatherCode = daily.weather_code[i],
                    highTemp = "${daily.temperature_2m_max[i].toInt()}°",
                    lowTemp = "${daily.temperature_2m_min[i].toInt()}°"
                )
            )
        }

        // Check for weather alerts (simplified - in a real app, you'd use a weather alerts API)
        val alert = if (current.weather_code in listOf(95, 96, 99) || current.precipitation > 5.0) {
            WeatherAlert(
                title = "Weather Alert",
                description = "Heavy rain expected. Potential for local flooding."
            )
        } else {
            null
        }

        return WeatherUiModel(
            location = locationName,
            temperature = "${current.temperature_2m.toInt()}°",
            weatherDescription = getWeatherDescription(current.weather_code),
            feelsLike = "${current.apparent_temperature.toInt()}°",
            humidity = "${current.relative_humidity_2m}%",
            windSpeed = "${current.wind_speed_10m.toInt()} mph",
            weatherCode = current.weather_code,
            lastUpdated = "Updated ${getTimeAgo(current.time)}",
            alert = alert,
            forecast = forecastItems
        )
    }

    private fun parseDate(dateString: String): Date {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.parse(dateString) ?: Date()
    }

    private fun getDayOfWeek(date: Date): String {
        val format = SimpleDateFormat("EEE", Locale.getDefault())
        return format.format(date)
    }

    private fun getFormattedDate(date: Date): String {
        val format = SimpleDateFormat("MMM d", Locale.getDefault())
        return format.format(date)
    }

    private fun getTimeAgo(timeString: String): String {
        // Simplified - in a real app, calculate actual time difference
        return "5 mins ago"
    }

    private fun getWeatherDescription(weatherCode: Int): String {
        return when (weatherCode) {
            0 -> "Clear Sky"
            1, 2, 3 -> "Partly Cloudy"
            45, 48 -> "Foggy"
            51, 53, 55 -> "Light Drizzle"
            56, 57 -> "Freezing Drizzle"
            61, 63, 65 -> "Rain"
            66, 67 -> "Freezing Rain"
            71, 73, 75 -> "Snow"
            77 -> "Snow Grains"
            80, 81, 82 -> "Heavy Rain"
            85, 86 -> "Heavy Snow"
            95 -> "Thunderstorm"
            96, 99 -> "Thunderstorm with Hail"
            else -> "Unknown"
        }
    }
}