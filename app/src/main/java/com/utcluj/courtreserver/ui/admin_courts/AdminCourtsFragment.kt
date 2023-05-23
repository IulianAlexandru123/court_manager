package com.utcluj.courtreserver.ui.admin_courts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.utcluj.courtreserver.databinding.FragmentAdminCourtsBinding

class AdminCourtsFragment : Fragment() {

    private val courtsViewModel: AdminCourtsViewModel by activityViewModels()
    private lateinit var courtsAdapter: AdminCourtsAdapter

    private var _binding: FragmentAdminCourtsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminCourtsBinding.inflate(inflater, container, false)
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
            courtsAdapter = AdminCourtsAdapter()
            binding.courtsRecyclerView.apply {
                adapter = courtsAdapter
                courtsAdapter.setItems(it)
            }
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(AdminCourtsFragmentDirections.actionNavigationCourtsToCreateCourt())
        }
    }
}