package com.example.weather

import com.google.gson.annotations.SerializedName


data class Hour (

  @SerializedName("time_epoch"     ) var timeEpoch    : Int?       = null,
  @SerializedName("time"           ) var time         : String?    = null,
  @SerializedName("temp_c"         ) var tempC        : Double?       = null,
  @SerializedName("temp_f"         ) var tempF        : Double?    = null,
  @SerializedName("condition"      ) var condition    : Condition? = Condition(),

)