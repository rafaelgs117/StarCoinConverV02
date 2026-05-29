package com.example

data class ExchangeRateResponse(
    val result: String,
    val base_code: String,
    val conversion_rates: Map<String, Double>,
    val time_last_update_utc: String
)
