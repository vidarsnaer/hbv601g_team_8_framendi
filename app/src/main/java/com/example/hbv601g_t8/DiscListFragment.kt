package com.example.hbv601g_t8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hbv601g_t8.databinding.DiscListFragmentBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

class DiscListFragment : Fragment() {

    private var _binding: DiscListFragmentBinding? = null

    //private lateinit var newArrayList: ArrayList<Disc>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DiscListFragmentBinding.inflate(inflater, container, false)

        /*newArrayList = arrayListOf(
            Disc(1, "used", "red disc slightly used", "Red Driver", 1000, "driver", 1, "red"),
            Disc(2, "used", "pink disc which is new", "Pink Driver", 1000, "driver", 1, "pink"),
            Disc(3, "used", "driver disc, not used", "Driver", 1000, "driver", 1, "black")
        )*/

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //binding.recyclerView.adapter = DiscAdapter(newArrayList)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}