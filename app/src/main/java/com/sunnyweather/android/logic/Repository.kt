package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Repository {

  fun searchPlaces(query: String) = liveData {
    val result = withContext(Dispatchers.IO) {
      try {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
          val places = placeResponse.places
          Result.success(places)
        } else {
          Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
      } catch (e: Exception) {
        Result.failure(e)
      }
    }
    emit(result)
  }
}