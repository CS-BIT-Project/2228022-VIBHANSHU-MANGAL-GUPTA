package com.example.plan_your_day

data class CurrencyResponse(
    val base_code: String,
    val conversion_rates: Map<String, Double>,
    val time_last_update_utc: String
)

