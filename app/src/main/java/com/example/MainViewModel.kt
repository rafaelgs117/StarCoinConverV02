package com.example

import androidx.lifecycle.*
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {
    private val API_KEY = if (BuildConfig.EXCHANGERATE_API_KEY.isNotEmpty() && BuildConfig.EXCHANGERATE_API_KEY != "SUA_API_KEY_AQUI") {
        BuildConfig.EXCHANGERATE_API_KEY
    } else {
        "e0bd6e667ba74d1c9a9969fc" // Fallback demo key from step 2.2 of PDF instructions
    }

    private val _rates = MutableLiveData<Map<String, Double>>()
    val rates: LiveData<Map<String, Double>> = _rates

    private val _lastUpdate = MutableLiveData<String>()
    val lastUpdate: LiveData<String> = _lastUpdate

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentBase = "USD"

    init {
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                fetchRates(currentBase)
                delay(60 * 60 * 1000L) // updates every 1 hour
            }
        }
    }

    fun fetchRates(base: String) {
        currentBase = base
        viewModelScope.launch {
            _isLoading.postValue(true)
            _error.postValue(null)
            try {
                val response = RetrofitClient.api.getRates(API_KEY, base)
                if (response.result == "success") {
                    _rates.postValue(response.conversion_rates)
                    _lastUpdate.postValue(formatDate(response.time_last_update_utc))
                } else {
                    _error.postValue("Erro ao buscar taxas.")
                }
            } catch (e: java.net.UnknownHostException) {
                _error.postValue("Sem conexão com a internet.")
            } catch (e: Exception) {
                _error.postValue("Erro: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun convert(amount: Double, toCurrency: String): Double {
        return amount * (_rates.value?.get(toCurrency) ?: 0.0)
    }

    fun getRate(currency: String): Double {
        return _rates.value?.get(currency) ?: 0.0
    }

    private fun formatDate(raw: String): String = try {
        // Raw returns date format like "Thu, 28 May 2026 12:00:01 +0000"
        val p = raw.split(", ", " ")
        "${p[1]} ${p[2]} ${p[3]} às ${p[4].substring(0, 5)}"
    } catch (e: Exception) {
        raw
    }
}
