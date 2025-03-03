package com.example.plan_your_day

data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val current_units: CurrentUnits,
    val current: Current,
    val daily_units: DailyUnits,
    val daily: Daily
)

data class CurrentUnits(
    val time: String,
    val interval: String,
    val temperature_2m: String,
    val relative_humidity_2m: String,
    val apparent_temperature: String,
    val is_day: String,
    val precipitation: String,
    val rain: String,
    val weather_code: String,
    val wind_speed_10m: String
)

data class Current(
    val time: String,
    val interval: Int,
    val temperature_2m: Double,
    val relative_humidity_2m: Int,
    val apparent_temperature: Double,
    val is_day: Int,
    val precipitation: Double,
    val rain: Double,
    val weather_code: Int,
    val wind_speed_10m: Double
)

data class DailyUnits(
    val time: String,
    val weather_code: String,
    val temperature_2m_max: String,
    val temperature_2m_min: String
)

data class Daily(
    val time: List<String>,
    val weather_code: List<Int>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>
)

data class GeocodingResponse(
    val results: List<GeocodingResult>?
)

data class GeocodingResult(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val admin1: String?
)

// UI models
data class WeatherUiModel(
    val location: String,
    val temperature: String,
    val weatherDescription: String,
    val feelsLike: String,
    val humidity: String,
    val windSpeed: String,
    val weatherCode: Int,
    val lastUpdated: String,
    val alert: WeatherAlert?,
    val forecast: List<ForecastItem>
)

data class WeatherAlert(
    val title: String,
    val description: String
)

data class ForecastItem(
    val day: String,
    val date: String,
    val weatherCode: Int,
    val highTemp: String,
    val lowTemp: String
)
