package com.example.task1.ui.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task1.data.repository.WeatherRepository
import com.example.weather.WeatherDays
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _weather = MutableLiveData<WeatherDays>()
    val weather: LiveData<WeatherDays> get() = _weather


    init {
        fetchWeather("London", 10)
    }

    fun fetchWeather(city: String, days: Int) {
        viewModelScope.launch {
            try {
                Log.d("WeatherViewModel", "Fetching weather data for city: $city")
                val response = repository.getWeatherForecast(city, days)
                _weather.value = response
                Log.d("myy", response.forecast?.forecastday?.count().toString())
                Log.d("WeatherViewModel", "Weather data fetched successfully")
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather data", e)
            }
        }
    }
}