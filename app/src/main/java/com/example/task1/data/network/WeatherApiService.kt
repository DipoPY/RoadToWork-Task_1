package com.example.task1.data.network

import com.example.weather.WeatherDays
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast.json")
    suspend fun getWeather(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("days") days: Int,
        @Query("aqi") aqi: String,
        @Query("alerts") alerts: String
    ): WeatherDays
}