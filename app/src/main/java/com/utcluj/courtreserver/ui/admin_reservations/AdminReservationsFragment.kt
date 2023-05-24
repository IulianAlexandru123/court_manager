package com.utcluj.courtreserver.ui.admin_reservations

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.utcluj.courtreserver.AuthenticationActivity

import com.utcluj.courtreserver.databinding.FragmentProfileBinding
import com.utcluj.courtreserver.dtos.ReservationOverallDetailsDTO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AdminReservationsFragment : Fragment() {

    private val reservationsViewModel: AdminReservationsViewModel by activityViewModels()
    private var _binding: FragmentProfileBinding? = null
    private lateinit var reservationsAdapter: AdminReservationsAdapter


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        performInitialServerRequest()
        initViews()
        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {
        reservationsViewModel.text.observe(viewLifecycleOwner) {
            binding.textUserName.text = it
        }

        binding.logOutButton.setOnClickListener {
            reservationsViewModel.logOutUser();
            startAuthActivity()
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                reservationsViewModel.reservationList.combine(reservationsViewModel.courtList) { res, crt ->
                    res.mapNotNull { reservation ->
                        val court = crt.firstOrNull { it.uuid == reservation.courtUuid }
                            ?: return@mapNotNull null
                        ReservationOverallDetailsDTO(reservation, court)
                    }
                }.collectLatest {
                    reservationsAdapter = AdminReservationsAdapter()
                    reservationsAdapter.callBack = {
                        reservationsViewModel.deleteReservation(it.reservationId)
                    }
                    binding.reservationsRecyclerView.apply {
                        adapter = reservationsAdapter
                        reservationsAdapter.setItems(it)
                    }
                }

                reservationsViewModel.deleteReservationResult.collectLatest {
                    if(it) {
                        performInitialServerRequest()
                    }
                }
            }
        }
    }

    private fun performInitialServerRequest() {
        reservationsViewModel.getReservations()
        reservationsViewModel.getCourts()
    }

    private fun startAuthActivity() {
        val mainActivityIntent = Intent(requireActivity(), AuthenticationActivity::class.java)
        startActivity(mainActivityIntent)
    }
}