package com.sunnyweather.android.ui.place

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.R
import com.sunnyweather.android.WeatherActivity
import com.sunnyweather.android.databinding.FragmentPlaceBinding

class PlaceFragment : Fragment(R.layout.fragment_place) {

  val viewModel by lazy {
    ViewModelProvider(this)[PlaceViewModel::class.java]
  }

  private lateinit var adapter: PlaceAdapter

  private var _binding: FragmentPlaceBinding? = null

  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentPlaceBinding.inflate(inflater, container, false)
    val view = binding.root
    return view
  }

  @SuppressLint("NotifyDataSetChanged")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    if (viewModel.isPlaceSaved()) {
      val place = viewModel.getSavedPlace()
      val intent = Intent(context, WeatherActivity::class.java).apply {
        putExtra("location_lng", place.location.lng)
        putExtra("location_lat", place.location.lat)
        putExtra("place_name", place.name)
      }
      startActivity(intent)
      activity?.finish()
      return
    }

    val layoutManager = LinearLayoutManager(activity)
    binding.recyclerView.layoutManager = layoutManager
    adapter = PlaceAdapter(this, viewModel.placeList)
    binding.recyclerView.adapter = adapter
    binding.searchPlaceEdit.addTextChangedListener { editable ->
      val content = editable.toString()
      if (content.isNotEmpty()) {
        viewModel.searchPlaces(content)
      } else {
        binding.recyclerView.visibility = View.GONE
        binding.bgImageView.visibility = View.VISIBLE
        viewModel.placeList.clear()
        adapter.notifyDataSetChanged()
      }
    }
    viewModel.placeLiveData.observe(viewLifecycleOwner) { result ->
      val places = result.getOrNull()
      Log.d("PlaceFragment", places.toString())
      if (places != null) {
        binding.recyclerView.visibility = View.VISIBLE
        binding.bgImageView.visibility = View.GONE
        viewModel.placeList.clear()
        viewModel.placeList.addAll(places)
        adapter.notifyDataSetChanged()
      } else {
        Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
        result.exceptionOrNull()?.printStackTrace()
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }
}