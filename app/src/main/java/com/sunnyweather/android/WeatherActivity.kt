package com.sunnyweather.android

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.databinding.ActivityWeatherBinding
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.ui.weather.WeatherViewModel

class WeatherActivity : AppCompatActivity(R.layout.activity_weather) {

  private val viewModel by lazy {
    ViewModelProvider(this)[WeatherViewModel::class.java]
  }

  private var _binding: ActivityWeatherBinding? = null

  private val binding get() = _binding!!

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    _binding = ActivityWeatherBinding.inflate(layoutInflater)

    if (viewModel.locationLng.isEmpty()) {
      viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
    }

    if (viewModel.locationLat.isEmpty()) {
      viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
    }

    if (viewModel.placeName.isEmpty()) {
      viewModel.placeName = intent.getStringExtra("place_name") ?: ""
    }

    viewModel.weatherLiveData.observe(this) { result ->
      val weather = result.getOrNull()
      if (weather != null) {
        showWeatherInfo(weather)
      } else {
        Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
        result.exceptionOrNull()?.printStackTrace()
      }
    }
    viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
  }

  private fun showWeatherInfo(weather: Weather) {

  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }
}