 package com.example.hbv601g_t8

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hbv601g_t8.SupabaseManager.supabase
import com.example.hbv601g_t8.databinding.DiscListFragmentBinding
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.ceil


 interface FilterListener {
    fun onFiltersApplied(priceMin: String, priceMax: String, state: String, type: String)
}

class DiscListFragment : Fragment() {

    private var _binding: DiscListFragmentBinding? = null
    private lateinit var newDiscList: List<Disc>
    private lateinit var discImages: MutableMap<Int, Bitmap>
    private lateinit var filteredDiscList: List<Disc>
    private lateinit var filterMinPrice: String
    private lateinit var filterMaxPrice: String
    private lateinit var filterType: String
    private lateinit var filterState: String
    //private lateinit var clearFilterButton: Button
    private lateinit var discAdapter: DiscAdapter


    private val binding get() = _binding!!

    private var userLocation: Location? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("DiscListFragment", "Location permission granted.")
            fetchUserLocationOnce()
            binding.radiusSlider.isEnabled = true
        } else {
            Log.d("DiscListFragment", "Location permission denied.")
            binding.radiusSlider.isEnabled = false
            showEnableLocationDialog()
        }
    }

    private fun showEnableLocationDialog() {
        TODO("Not yet implemented")
    }

    fun updateFilters(priceMin: String, priceMax: String, state: String, type: String) {
        filterMinPrice = priceMin
        filterMaxPrice = priceMax
        filterState = state
        filterType = type

        println("$filterMinPrice - $filterMaxPrice, $filterState, $filterType")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        newDiscList = emptyList()

        _binding = DiscListFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestLocationPermissionIfNeeded()
        // Update the slider to reflect hardcoded location settings

        newDiscList = emptyList()
        discImages = mutableMapOf()

        suspend fun loadImageFromUrl(imageUrl: String): Bitmap? = withContext(Dispatchers.IO) {
            return@withContext try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        suspend fun getImages(){
            for (disc in newDiscList) {
                if(disc.discId != null) {
                    val intDiscId = disc.discId.toInt()
                    val imageUrl = supabase.storage.from("Images").publicUrl("${intDiscId}/image")
                    val bitmap = loadImageFromUrl(imageUrl)
                    bitmap?.let {
                        discImages[intDiscId] = it
                    }
                }
            }
        }

        lifecycleScope.launch {
            selectAllDiscsFromDatabase()
            getImages()
        }

        updateSliderMaxDistance()

        discAdapter = DiscAdapter(newDiscList, discImages)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = discAdapter
        }

        binding.radiusSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                updateSliderText(value)
                filterDiscsByRadius(value.toDouble())
            }
        }
        filterDiscsByRadius(Double.MAX_VALUE)
    }

    private fun requestLocationPermissionIfNeeded() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                fetchUserLocationOnce()  // Fetch the location once permission is confirmed
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // TODO: Show UI to explain why the permission is needed?
                showInContextUI()
            }
            else -> {
                // Directly request for permission
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun fetchUserLocationOnce() {
        // Assuming you have an instance of LocationManager
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Use the best provider for the last known location
        val provider = when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> return  // No provider available, consider handling this case
        }

        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                // Fetch the location once permission is confirmed
                userLocation = locationManager.getLastKnownLocation(provider)
                var userLatitude = userLocation?.latitude
                var userLongitude = userLocation?.longitude
                if (userLatitude != null) {
                    GlobalVariables.USER_LATITUDE = userLatitude
                }
                if (userLongitude != null) {
                    GlobalVariables.USER_LONGITUDE = userLongitude
                }
                updateSliderMaxDistance()
            } else -> {
                // TODO: Consider handling the case where location is null?
                // Remove radius filter/slider?
            }
        }

    }

    private fun showInContextUI() {
        //TODO: implement
    }

    private fun updateSliderMaxDistance() {
        userLocation?.let {
            val maxDistanceKm = calculateMaxDistance() / 1000  // Convert meters to kilometers
            var safeMaxDistance = ceil(maxDistanceKm / 10) * 10  // Round up to nearest 10

            // Ensure safeMaxDistance is always greater than valueFrom
            if (safeMaxDistance <= 10f) {
                safeMaxDistance = 20f  // Set to a default value greater than valueFrom
            }

            binding.radiusSlider.apply {
                valueFrom = 10f  // Minimum filtering distance is 10 km
                valueTo = safeMaxDistance  // Maximum distance based on the farthest disc
                stepSize = 10f  // Step size of 10 km
                value = valueTo  // Start with the slider at all discs
            }

            updateSliderText(10000000f)  // Show all discs initially
        }

    }


    private fun updateSliderText(distance: Float) {
        binding.radiusText.text = if (distance >= binding.radiusSlider.valueTo) {
            getString(R.string.radius_all)
        } else {
            "Showing available discs within ${distance.toInt()} km from your location."
        }
    }

    private fun filterDiscsByRadius(radius: Double) {

        val filteredDiscs = if (radius >= 10000000.0) {
            newDiscList  // Use a copy of the master list
        } else {
            newDiscList.filter { disc ->
                val results = FloatArray(1)
                userLocation?.let { Location.distanceBetween(it.latitude, userLocation!!.longitude, disc.latitude, disc.longitude, results) }
                results[0] / 1000 <= radius  // Filtering by radius in kilometers
            }
        }
        // Update the display list and adapter
        discAdapter.updateData(filteredDiscs, discImages)
        Log.d("DiscListFragment", "Displayed discs count: ${filteredDiscs.size}")
    }

    private fun calculateMaxDistance(): Float {

        val mutableDiscList = newDiscList.toMutableList()

        return mutableDiscList.maxOfOrNull { disc ->
            val results = FloatArray(1)
            userLocation?.let { Location.distanceBetween(it.latitude, userLocation!!.longitude, disc.latitude, disc.longitude, results) }
            results[0]  // Distance in meters
        } ?: 10000f  // Default to 10 km if no discs
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun selectAllDiscsFromDatabase() {
        try {
            val discs = DiscService().getAllDiscs() ?: emptyList()  // Use Elvis operator for null safety
            if (discs.isEmpty()) {
                Log.d("DiscListFragment", "No discs found")
                // Update the UI to reflect no discs available
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No discs available", Toast.LENGTH_LONG).show()
                    // Possibly hide RecyclerView here if it's empty
                }
            } else {
                newDiscList = discs
                // Make sure to update the UI on the main thread if needed
                withContext(Dispatchers.Main) {
                    discAdapter.updateData(newDiscList, discImages)
                }
            }
        } catch (e: Exception) {
            Log.e("DiscListFragment", "Error fetching discs", e)
        }
    }


    fun filterAndRefreshView() {
        println("refreshView Called")
        println("$filterMinPrice - $filterMaxPrice, $filterState, $filterType")
        filteredDiscList = emptyList()

        runBlocking {
            filteredDiscList = DiscService().filterDiscs(fromPrice = filterMinPrice.toInt(), toPrice = filterMaxPrice.toInt(), type = filterType, condition = filterState, colour = null, name = null)!!
        }

        if (filteredDiscList.isNotEmpty()) {
            val recyclerView = binding.recyclerView
            recyclerView.adapter = DiscAdapter(filteredDiscList, discImages)
        } else {
            Toast.makeText(requireContext(), "No discs matches your filter", Toast.LENGTH_SHORT).show()
        }

        println("refreshView committed")
    }

    fun clearFilters() {
        runBlocking {
            selectAllDiscsFromDatabase()
        }

        val recyclerView = binding.recyclerView
        recyclerView.adapter = DiscAdapter(newDiscList, discImages)
        println("filters have been cleared")
    }


}
