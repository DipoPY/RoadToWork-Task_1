package com.example.task1.data.repository

import com.example.weather.WeatherDays

interface WeatherRepository {
    suspend fun getWeatherForecast(city: String, days: Int): WeatherDays
}