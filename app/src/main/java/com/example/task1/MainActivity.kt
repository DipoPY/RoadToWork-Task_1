package com.example.task1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.task1.data.network.RetrofitInstance
import com.example.task1.data.repository.WeatherRepositoryImpl
import com.example.task1.ui.view.WeatherCard

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = WeatherRepositoryImpl(RetrofitInstance.api)
        setContent {
            WeatherCard(repository = repository)
        }
    }
}
