package com.utcluj.courtreserver.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.utcluj.courtreserver.AuthenticationActivity

import com.utcluj.courtreserver.databinding.FragmentProfileBinding
import com.utcluj.courtreserver.dtos.ReservationOverallDetailsDTO
import com.utcluj.courtreserver.ui.courts.CourtsAdapter
import com.utcluj.courtreserver.ui.courts.CourtsFragmentDirections
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by activityViewModels()
    private var _binding: FragmentProfileBinding? = null
    private lateinit var reservationsAdapter: ReservationsAdapter


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
        profileViewModel.text.observe(viewLifecycleOwner) {
            binding.textUserName.text = it
        }

        binding.logOutButton.setOnClickListener {
            profileViewModel.logOutUser();
            startAuthActivity()
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.reservationList.combine(profileViewModel.courtList) { res, crt ->
                    res.mapNotNull { reservation ->
                        val court = crt.firstOrNull { it.uuid == reservation.courtUuid }
                            ?: return@mapNotNull null
                        ReservationOverallDetailsDTO(reservation, court)
                    }
                }.collectLatest {
                    reservationsAdapter = ReservationsAdapter()
                    reservationsAdapter.callBack = {
                        profileViewModel.deleteReservation(it.reservationId)
                    }
                    binding.reservationsRecyclerView.apply {
                        adapter = reservationsAdapter
                        reservationsAdapter.setItems(it)
                    }
                }

                profileViewModel.deleteReservationResult.collectLatest {
                    if(it) {
                        performInitialServerRequest()
                    }
                }
            }
        }
    }

    private fun performInitialServerRequest() {
        profileViewModel.getReservations()
        profileViewModel.getCourts()
    }

    private fun startAuthActivity() {
        val mainActivityIntent = Intent(requireActivity(), AuthenticationActivity::class.java)
        startActivity(mainActivityIntent)
    }
}