package com.example.task1.ui.view

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.task1.data.repository.WeatherRepository
import com.example.weather.Forecast
import com.example.weather.Forecastday
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WeatherCard(repository: WeatherRepository) {
    val factory = WeatherViewModelFactory(repository)
    val viewModel: WeatherViewModel = viewModel(factory = factory)
    val weather by viewModel.weather.observeAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            weather?.let { currentWeather ->
                Text(
                    text = "Текущее место",
                    modifier = Modifier
                        .padding(top = 35.dp),
                    fontSize = 30.sp
                )
                currentWeather.location?.name?.let { it1 ->
                    Text(
                        text = it1,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        fontSize = 20.sp
                    )
                }
                Text(
                    text = "${currentWeather.current?.tempC?.toInt()}°",
                    fontSize = 100.sp,
                    fontWeight = FontWeight.W300,
                    modifier = Modifier.offset(y = (-19).dp)
                )

                currentWeather.current?.condition?.text?.let { it1 ->
                    Text(
                        text = it1,
                        fontSize = 20.sp,
                        modifier = Modifier.offset(y = (-30).dp)
                    )
                }
                Row {
                    Text(
                        text = "Макс.: ${currentWeather.forecast?.forecastday?.get(0)?.day?.maxtempC}, мин.: ${currentWeather.forecast?.forecastday?.get(0)?.day?.mintempC}",
                        fontSize = 20.sp,
                        modifier = Modifier.offset(y = (-30).dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp, start = 5.dp, end = 5.dp)
                        .clip(
                            RoundedCornerShape(15.dp)
                        )
                        .background(Color.LightGray)
                ) {
                    Text(text = "Погода на сегодня",
                        fontSize = 28.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.W500
                    )
                    LazyRow{
                        currentWeather.forecast?.forecastday?.get(0)?.let { it1 ->
                            items(it1.hour) { hourData ->
                                val hour = hourData.time?.substring(11..12)
                                val tempHour = hourData.tempC
                                val iconTempHour = hourData.condition?.icon
                                WeatherHours(hour = hour, image = iconTempHour, temp = tempHour)
                            }
                        }
                    }
                }
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 30.dp, start = 5.dp, end = 5.dp, bottom = 20.dp)
                    .clip(
                        RoundedCornerShape(15.dp)
                    )
                    .background(Color.LightGray)
                ) {

                        for (i in 1 until 7)
                            currentWeather.forecast?.forecastday?.get(i)
                                ?.let { it1 -> WeatherWeek(forecastday = it1) }

                }
            }
        }
    }
}

@Composable
fun WeatherHours(hour: String?, image: String?, temp: Double?) {
    Column(
        modifier = Modifier.padding(end = 5.dp, start = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = hour!!,
            fontSize = 24.sp
        )
        AsyncImage(

            model = "https:$image",
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
        )
        Text(
            text = "${temp?.toInt().toString()}°",
            fontSize = 24.sp
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherWeek(forecastday: Forecastday){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = getDayOfWeek(forecastday.date!!),
            modifier = Modifier
                .weight(0.2f),
            fontSize = 25.sp,
            fontWeight = FontWeight.W500
        )
        AsyncImage(

            model = "https:${forecastday.day?.condition?.icon}",
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .weight(0.2f)
        )
        Column(
            Modifier
                .weight(0.2f)
        ) {
            Text(
                text = "Мин.: ${forecastday.day?.mintempC} C°",
                fontSize = 22.sp
            )
            Text(
                text = "Макс.:${forecastday.day?.maxtempC} C°",
                fontSize = 22.sp
            )
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDayOfWeek(dateString: String, pattern: String = "yyyy-MM-dd"): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val date = LocalDate.parse(dateString, formatter)
    val dayOfWeek = date.dayOfWeek
    val today = LocalDate.now()
    if(today == date) return "TODAY"
    else return dayOfWeek.name
}





