package com.example.task1.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import androidx.annotation.UiContext
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.task1.R
import com.example.task1.data.repository.WeatherRepository
import com.example.weather.Forecast
import com.example.weather.Forecastday
import com.example.weather.Hour
import com.example.weather.WeatherDays
import dev.materii.pullrefresh.PullRefreshLayout
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.internal.wait
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun WeatherCard(repository: WeatherRepository) {
    val factory = WeatherViewModelFactory(repository)
    val viewModel: WeatherViewModel = viewModel(factory = factory)
    val weather by viewModel.weather.observeAsState()
    val checkedState = remember { mutableStateOf(false) }
    val city = remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState(LocalTime.now().hour)
    val background = remember { mutableStateOf(R.drawable.sunny) }
    val visible = remember { mutableStateOf(true) }


    var isRefreshing by remember {
        mutableStateOf(false)
    }
    val rotationAngle = remember { Animatable(0f) }


    val state = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = {
        isRefreshing = true
    })

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            rotationAngle.animateTo(
                targetValue = rotationAngle.value,

            )
            viewModel.fetchWeather(city.value, 3)
            lazyListState.scrollToItem(LocalTime.now().hour)
            delay(1000)
            isRefreshing = false

        }
    }

    weather?.current?.condition?.text?.let { condition ->
        UpdateBackgroundAndAnimate(condition, background, visible)
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(state)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = visible.value,
                enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
                exit = fadeOut(animationSpec = tween(durationMillis = 1000))
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(background.value),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            }
        }


        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            weather?.let { currentWeather ->
                Text(
                    text = "Текущее место",
                    modifier = Modifier.padding(top = 40.dp),
                    fontSize = 30.sp
                )
                currentWeather.location?.name?.let { it1 ->
                    Text(
                        text = it1,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontSize = 20.sp
                    )
                }
                Text(
                    text = "${SwitcherCurrentTemp(checkedState.value, currentWeather)}°",
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
                        text = SwitcherCurrentMaxMinTemp(checkedState.value, currentWeather),
                        fontSize = 20.sp,
                        modifier = Modifier.offset(y = (-30).dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp, start = 5.dp, end = 5.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.LightGray)
                ) {
                    Text(
                        text = "Погода на сегодня",
                        fontSize = 28.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.W500
                    )
                    LazyRow(state = lazyListState) {
                        currentWeather.forecast?.forecastday?.get(0)?.let { forecastDay ->
                            items(forecastDay.hour) { hourData ->
                                val hour = hourData.time?.substring(11..12)
                                val tempHour = if (checkedState.value) hourData.tempC else hourData.tempF
                                val iconTempHour = hourData.condition?.icon
                                WeatherHours(hour = hour, image = iconTempHour, temp = tempHour)
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 30.dp, start = 5.dp, end = 5.dp, bottom = 20.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.LightGray)
                ) {
                    for (i in 0 until 3) {
                        currentWeather.forecast?.forecastday?.get(i)?.let { forecastDay ->
                            WeatherWeek(forecastDay, checkedState.value)
                        }
                    }
                }
            }
        }
        TopAppBar(checkedState = checkedState, cityState = city, viewModel, background)
        PullRefreshIndicator(refreshing = isRefreshing, state = state,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .graphicsLayer(
                    rotationZ = rotationAngle.value
                )
        )
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpdateBackgroundAndAnimate(
    newCondition: String,
    background: MutableState<Int>,
    visible: MutableState<Boolean>
) {
    LaunchedEffect(newCondition) {
        visible.value = false
        delay(500)
        background.value = BackgroundWeather(newCondition)
        delay(500)
        visible.value = true
    }
}


fun BackgroundWeather(condition: String): Int{
    when(condition){
        "Sunny" -> return R.drawable.sunny
        "Partly cloudy" -> return R.drawable.partly_cloudy
        "Cloudy" -> return R.drawable.cloudy
        "Overcast" -> return R.drawable.cloudy
        "Mist" -> return R.drawable.mist
        "Patchy rain possible" -> return R.drawable.rain
        "Patchy snow possible" -> return R.drawable.snow
        "Patchy sleet possible" -> return R.drawable.snow
        "Patchy freezing drizzle possible" -> return R.drawable.rain
        "Thundery outbreaks possible" -> return R.drawable.rain
        "Blowing snow" -> return R.drawable.snow
        "Fog" -> return R.drawable.mist
        "Freezing fog" -> return R.drawable.mist
        "Patchy light drizzle" -> return R.drawable.rain
        "Light drizzle" -> return R.drawable.rain
        "Freezing drizzle" -> return R.drawable.rain
        "Heavy freezing drizzle" -> return R.drawable.rain
        "Patchy light rain" -> return R.drawable.rain
        "Light rain" -> return R.drawable.rain
        else -> return R.drawable.cloudy
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherHours(hour: String?, image: String?, temp: Double?) {
    Column(
        modifier = Modifier.padding(end = 5.dp, start = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text =
                if (hour!!.toInt() == LocalTime.now().hour) "Сейчас"
                else hour,
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
fun WeatherWeek(forecastday: Forecastday, state: Boolean){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = getDayOfWeek(forecastday.date!!),
            modifier = Modifier
                .weight(0.25f),
            fontSize = 25.sp,
            fontWeight = FontWeight.W500
        )
        AsyncImage(

            model = "https:${forecastday.day?.condition?.icon}",
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .weight(0.2f)
                .align(Alignment.CenterVertically)
        )
        Column(
            Modifier
                .weight(0.25f)
        ) {
            Text(
                text = "Мин.: ${
                    if(state == true) {
                        "${ forecastday.day?.mintempC } C°"
                    }
                    else {
                        "${ forecastday.day?.mintempF } F°"
                    }
                }",
                fontSize = 22.sp
            )
            Text(
                text = "Макс.:${
                    if(state == true) {
                        "${ forecastday.day?.maxtempC } C°"
                    }
                    else {
                        "${ forecastday.day?.maxtempF } F°"
                    }
                }",
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
    return dayOfWeek.name
}

fun SwitcherCurrentTemp(state: Boolean, currentWeather: WeatherDays): Int? {
    if(state == true) return currentWeather.current?.tempC?.toInt()
    else return currentWeather.current?.tempF?.toInt()
}

fun SwitcherCurrentMaxMinTemp(state: Boolean, currentWeather: WeatherDays): String {
    if(state == true) return "Макс.: ${currentWeather.forecast?.forecastday?.get(0)?.day?.maxtempC}, мин.: ${currentWeather.forecast?.forecastday?.get(0)?.day?.mintempC}"
    else return "Макс.: ${currentWeather.forecast?.forecastday?.get(0)?.day?.maxtempF}, мин.: ${currentWeather.forecast?.forecastday?.get(0)?.day?.mintempF}"
}

@Composable
fun SearchCity(
    city: MutableState<String>,
    viewModel: WeatherViewModel,
    onClose: () -> Unit,
    background: MutableState<Int>
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier
        .fillMaxSize()
        .zIndex(1f)) {
        Box(
            modifier = Modifier
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            TextField(
                value = city.value,
                onValueChange = { new -> city.value = new },
                leadingIcon = {
                    IconButton(onClick = {
                        viewModel.fetchWeather(city.value, 3)
                        onClose()
                    }) {

                    }
                    Icon(
                        Icons.Default.Search,
                        "Поиск"
                    )
                },
                modifier = Modifier.fillMaxWidth(0.8f),
                singleLine = true
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    checkedState: MutableState<Boolean>,
    cityState: MutableState<String>,
    viewModel: WeatherViewModel,
    background: MutableState<Int>
) {
    var showSearch by remember { mutableStateOf(false) }

    TopAppBar(
        title = {},
        actions = {
            Switch(
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                },
                modifier = Modifier.padding(5.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White,
            navigationIconContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(
                onClick = {
                    showSearch = !showSearch
                }
            ) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Поменять город",
                    modifier = Modifier.size(35.dp)
                )
            }
        }
    )
    if (showSearch) {
        SearchCity(cityState, viewModel, onClose = { showSearch = false }, background)
    }
}
