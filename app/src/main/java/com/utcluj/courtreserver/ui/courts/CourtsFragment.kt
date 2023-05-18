package com.utcluj.courtreserver.ui.courts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.utcluj.courtreserver.databinding.FragmentCourtsBinding


class CourtsFragment : Fragment() {

    private val courtsViewModel: CourtsViewModel by activityViewModels()
    private lateinit var courtsAdapter: CourtsAdapter

    private var _binding: FragmentCourtsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourtsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        performInitialServerRequest()
        initViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun performInitialServerRequest() {
        courtsViewModel.getCourts()
    }

    private fun initViews() {
        courtsViewModel.courtList.observe(viewLifecycleOwner) {
            courtsAdapter = CourtsAdapter()
            courtsAdapter.callBack = {
                findNavController().navigate(
                    CourtsFragmentDirections.actionNavigationCourtsToCourtDetailsFragment(
                        it.courtDTO
                    )
                )
            }
            binding.courtsRecyclerView.apply {
                adapter = courtsAdapter
                courtsAdapter.setItems(it)
            }
        }
    }
}