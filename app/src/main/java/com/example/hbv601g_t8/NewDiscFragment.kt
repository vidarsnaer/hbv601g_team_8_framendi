package com.example.hbv601g_t8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.fragment.app.Fragment

/**
 * A fragment for creating a new disc listing.
 * Allows users to input information such as disc state, type, description, color, price, and quantity.
 */
class NewDiscFragment : Fragment() {

    private lateinit var submitButton: Button
    private lateinit var selectedType: String
    private lateinit var selectedState: String

    /**
     * Inflates the layout for this fragment.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.newdisc, container, false)
    }

    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been restored in to the view.
     * @param view The View returned by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate the state spinner
        val states = arrayOf("New", "Used")
        val stateAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, states)
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stateSpinner.adapter = stateAdapter

        // Set selected state when an item is selected
        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedState = states[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Populate the type spinner
        val discTypes = arrayOf("Putter", "Mid-Range", "Fairway Driver", "Distance Driver")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, discTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapter

        // Set selected type when an item is selected
        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedType = discTypes[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        //Store information when submitButton is clicked
        submitButton.setOnClickListener {
            val description = descriptionText.text.toString()
            val title = titleText.text.toString()
            val color = colorText.text.toString()
            val price = priceText.text.toString().toDoubleOrNull() ?: 0.0
            val quantity = quantityText.text.toString().toDoubleOrNull() ?: 0.0

            //Send this info to the database
            val discInfo = "$selectedState, $title, $description, $price, $selectedType, $quantity, $color"
        }
    }

}