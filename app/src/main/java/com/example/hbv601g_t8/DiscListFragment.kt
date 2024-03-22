package com.example.hbv601g_t8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hbv601g_t8.databinding.DiscListFragmentBinding
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        _binding = DiscListFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newDiscList = emptyList()

        runBlocking {
            selectAllDiscsFromDatabase()
        }

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = DiscAdapter(newDiscList)

        println("DiscListFragment created")
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