package com.example.task1.data.repository

import com.example.task1.data.network.WeatherApiService
import com.example.weather.WeatherDays

class WeatherRepositoryImpl(private val apiService: WeatherApiService): WeatherRepository {
    override suspend fun getWeatherForecast(city: String, days: Int): WeatherDays {
        return apiService.getWeather(
            apiKey = "0be8b228b80947eda81145325242606",
            query = city,
            days = days,
            aqi = "no",
            alerts = "no"
        )
    }

}