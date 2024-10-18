package com.sunnyweather.android

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.databinding.ActivityWeatherBinding
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import com.sunnyweather.android.ui.weather.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity(R.layout.activity_weather) {

  private val viewModel by lazy {
    ViewModelProvider(this)[WeatherViewModel::class.java]
  }

  private var _binding: ActivityWeatherBinding? = null

  private val binding get() = _binding!!

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    _binding = ActivityWeatherBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val decorView = window.decorView
    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    window.statusBarColor = Color.TRANSPARENT

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
      Log.d("WeatherActivity", weather.toString())
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
    binding.nowLayout.placeName.text = viewModel.placeName
    val realtime = weather.realtime
    val daily = weather.daily
    val currentTempText = "${realtime.temperature.toInt()} ℃"
    binding.nowLayout.currentTemp.text = currentTempText
    binding.nowLayout.currentSky.text = getSky(realtime.skycon).info
    val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
    binding.nowLayout.currentAQI.text = currentPM25Text
    binding.nowLayout.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
    binding.forecastLayout.forecastLayout.removeAllViews()
    val days = daily.skycon.size
    for (i in 0 until days) {
      val skycon = daily.skycon[i]
      val temperature = daily.temperature[i]
      val view = LayoutInflater.from(this).inflate(
        R.layout.forecast_item,
        binding.forecastLayout.forecastLayout,
        false
      )
      val dateInfo = view.findViewById<TextView>(R.id.dateInfo)
      val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
      val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
      val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)
      val simpleDateFormat = SimpleDateFormat("yyyy-MM-DD", Locale.getDefault())
      dateInfo.text = simpleDateFormat.format(skycon.date)
      val sky = getSky(skycon.value)
      skyIcon.setImageResource(sky.icon)
      skyInfo.text = sky.info
      val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
      temperatureInfo.text = tempText
      binding.forecastLayout.forecastLayout.addView(view)
    }
    val lifeIndex = daily.lifeIndex
    binding.lifeIndexLayout.coldRiskText.text = lifeIndex.coldRisk[0].desc
    binding.lifeIndexLayout.dressingText.text = lifeIndex.dressing[0].desc
    binding.lifeIndexLayout.ultravioletText.text = lifeIndex.ultraviolet[0].desc
    binding.lifeIndexLayout.carWashingText.text = lifeIndex.carWashing[0].desc
    binding.weatherLayout.visibility = View.VISIBLE
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }
}