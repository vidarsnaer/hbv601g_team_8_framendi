package com.example.hbv601g_t8

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hbv601g_t8.databinding.DiscListFragmentBinding
import kotlin.math.ceil
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


interface FilterListener {
    fun onFiltersApplied(priceMin: String, priceMax: String, state: String, type: String)
}

class DiscListFragment : Fragment() {

    private var _binding: DiscListFragmentBinding? = null
    private lateinit var newDiscList: List<Disc>
    private lateinit var filteredDiscList: List<Disc>
    private lateinit var filterMinPrice: String
    private lateinit var filterMaxPrice: String
    private lateinit var filterType: String
    private lateinit var filterState: String
    private lateinit var clearFilterButton: Button
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
    ): View? {

        newDiscList = emptyList()

        _binding = DiscListFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestLocationPermissionIfNeeded()
        // Update the slider to reflect hardcoded location settings

        newDiscList = emptyList()

        runBlocking {
            selectAllDiscsFromDatabase()
        }

        updateSliderMaxDistance()

        discAdapter = DiscAdapter(newDiscList)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = discAdapter
        }

        binding.radiusSlider.addOnChangeListener { slider, value, fromUser ->
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
                // Show UI to explain why the permission is needed
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
                updateSliderMaxDistance()
            } else -> {
            // Consider handling the case where location is null
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
                valueTo = safeMaxDistance.toFloat()  // Maximum distance based on the farthest disc
                stepSize = 10f  // Step size of 10 km
                value = valueTo  // Start with the slider at "All"
            }

            updateSliderText(10000000f)  // Show "All" initially
        }

    }


    private fun updateSliderText(distance: Float) {
        binding.radiusText.text = if (distance >= binding.radiusSlider.valueTo) {
            "All"
        } else {
            "${distance.toInt()} km"
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
        discAdapter.updateData(filteredDiscs)
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
        withContext(Dispatchers.IO) {
            newDiscList = SupabaseManager.supabase.from("discs").select().decodeList<Disc>()
        }
    }

    fun filterAndRefreshView() {
        println("refreshView Called")
        println("$filterMinPrice - $filterMaxPrice, $filterState, $filterType")
        filteredDiscList = emptyList()

        runBlocking {
            withContext(Dispatchers.IO) {
                filteredDiscList = SupabaseManager.supabase.from("discs").select {
                    filter {
                        if (filterState != "Any")  {
                            eq("condition", filterState)
                        }
                        if (filterType != "Any") {
                            eq("type", filterType)
                        }
                        and {
                            gte("price", filterMinPrice)
                            lte("price", filterMaxPrice)
                        }
                    }
                }.decodeList<Disc>()
            }
        }

        if (filteredDiscList.isNotEmpty()) {
            val recyclerView = binding.recyclerView
            recyclerView.adapter = DiscAdapter(filteredDiscList)
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
        recyclerView.adapter = DiscAdapter(newDiscList)
        println("filters have been cleared")
    }

}
