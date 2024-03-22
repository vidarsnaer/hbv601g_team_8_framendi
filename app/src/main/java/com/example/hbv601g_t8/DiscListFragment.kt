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


class DiscListFragment : Fragment() {

    private var _binding: DiscListFragmentBinding? = null

    // Master list that holds all discs, remains unchanged
    private lateinit var masterDiscList: ArrayList<Disc>

    // List used for display, can be modified based on filters
    private lateinit var displayDiscList: ArrayList<Disc>

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DiscListFragmentBinding.inflate(inflater, container, false)

        masterDiscList = arrayListOf(
            Disc(1, "used", "red disc slightly used", "Red Driver", 1000, "driver", 1, "red", 66.497650, -19.202146),
            Disc(2, "used", "pink disc which is new", "Pink Driver", 1000, "driver", 1, "pink", 64.825525, -17.429893),
            Disc(3, "used", "driver disc, not used", "Driver", 1000, "driver", 1, "black", 66.326720, -21.481743)
        )

        displayDiscList = ArrayList(masterDiscList) // Initially, display all discs

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        discAdapter = DiscAdapter(displayDiscList)
        binding.recyclerView.adapter = discAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestLocationPermissionIfNeeded()
        discAdapter = DiscAdapter(displayDiscList)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = discAdapter
        }

        // Update the slider to reflect hardcoded location settings
        updateSliderMaxDistance()

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

        // This requires ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION permission
        val location = locationManager.getLastKnownLocation(provider)
        if (location != null) {
            userLocation = location
            updateSliderMaxDistance()  // Now you can safely update the slider with the user location
        } else {
            // Consider handling the case where location is null
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
            ArrayList(masterDiscList)  // Use a copy of the master list
        } else {
            masterDiscList.filter { disc ->
                val results = FloatArray(1)
                userLocation?.let { Location.distanceBetween(it.latitude, userLocation!!.longitude, disc.latitude, disc.longitude, results) }
                results[0] / 1000 <= radius  // Filtering by radius in kilometers
            }
        }
        // Update the display list and adapter
        displayDiscList.clear()
        displayDiscList.addAll(filteredDiscs)
        discAdapter.notifyDataSetChanged()
        Log.d("DiscListFragment", "Displayed discs count: ${filteredDiscs.size}")
    }

    private fun calculateMaxDistance(): Float {
        return masterDiscList.maxOfOrNull { disc ->
            val results = FloatArray(1)
            userLocation?.let { Location.distanceBetween(it.latitude, userLocation!!.longitude, disc.latitude, disc.longitude, results) }
            results[0]  // Distance in meters
        } ?: 10000f  // Default to 10 km if no discs
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
