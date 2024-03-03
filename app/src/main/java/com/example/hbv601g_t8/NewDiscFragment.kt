package com.example.hbv601g_t8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.fragment.app.Fragment

class NewDiscFragment : Fragment() {

    private lateinit var submitButton: Button
    private lateinit var selectedType: String
    private lateinit var selectedState: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.newdisc, container, false)
    }

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

        val discTypes = arrayOf("Putter", "Mid-Range", "Fairway Driver", "Distance Driver")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, discTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapter

        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedType = discTypes[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

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