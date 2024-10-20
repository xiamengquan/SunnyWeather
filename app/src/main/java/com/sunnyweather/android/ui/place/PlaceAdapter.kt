package com.sunnyweather.android.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.sunnyweather.android.R
import com.sunnyweather.android.WeatherActivity
import com.sunnyweather.android.logic.model.Place

class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) : Adapter<PlaceAdapter.ViewHolder>() {

  inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val placeName: TextView = view.findViewById(R.id.placeName)
    val placeAddress: TextView = view.findViewById(R.id.placeAddress)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.place_item, parent, false)
    val holder = ViewHolder(view)
    holder.itemView.setOnClickListener {
      val position = holder.bindingAdapterPosition
      val place = placeList[position]
      val intent = Intent(parent.context, WeatherActivity::class.java).apply {
        putExtra("location_lng", place.location.lng)
        putExtra("location_lat", place.location.lat)
        putExtra("place_name", place.name)
      }
      fragment.viewModel.savePlace(place)
      fragment.startActivity(intent)
      fragment.activity?.finish()
    }
    return holder
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val place = placeList[position]
    holder.placeName.text = place.name
    holder.placeAddress.text = place.address
  }

  override fun getItemCount() = placeList.size
}