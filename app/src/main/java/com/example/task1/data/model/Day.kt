package com.example.weather

import com.google.gson.annotations.SerializedName


data class Day (

  @SerializedName("maxtemp_c"            ) var maxtempC          : Double?    = null,
  @SerializedName("maxtemp_f"            ) var maxtempF          : Double?       = null,
  @SerializedName("mintemp_c"            ) var mintempC          : Double?    = null,
  @SerializedName("mintemp_f"            ) var mintempF          : Double?       = null,
  @SerializedName("avgtemp_c"            ) var avgtempC          : Double?       = null,
  @SerializedName("avgtemp_f"            ) var avgtempF          : Double?    = null,
  @SerializedName("condition"            ) var condition         : Condition? = Condition(),

)