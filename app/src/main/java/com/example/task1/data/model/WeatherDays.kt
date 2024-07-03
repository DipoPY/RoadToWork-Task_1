package com.example.weather

import com.google.gson.annotations.SerializedName


data class WeatherDays (

  @SerializedName("location" ) var location : Location? = Location(),
  @SerializedName("current"  ) var current  : Current?  = Current(),
  @SerializedName("forecast" ) var forecast : Forecast? = Forecast()

)