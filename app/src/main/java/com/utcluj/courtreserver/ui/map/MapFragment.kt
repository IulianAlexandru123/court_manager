package com.utcluj.courtreserver.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.utcluj.courtreserver.R
import com.utcluj.courtreserver.databinding.FragmentMapBinding
import com.utcluj.courtreserver.ui.courts.CourtsViewModel


class MapFragment : Fragment(), OnMarkerClickListener {

    private val courtsViewModel: CourtsViewModel by activityViewModels()

    private var _binding: FragmentMapBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        performInitialServerRequest()
        initViews()
    }

    private fun performInitialServerRequest() {
        courtsViewModel.getCourts()
    }

    private fun initViews() {
        courtsViewModel.courtList.observe(viewLifecycleOwner) { courtList ->
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
            mapFragment?.getMapAsync {googleMap ->
                googleMap.uiSettings.isZoomControlsEnabled = true
                val areaLatLong = LatLng( courtList.firstOrNull()?.lat ?: 0.0, courtList.firstOrNull()?.long ?: 0.0)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(areaLatLong, 10F));
                googleMap.setOnMarkerClickListener(this)
                courtList.forEach { court ->
                    val courtMarker = LatLng(court.lat, court.long)
                    val marker = googleMap.addMarker(
                        MarkerOptions()
                            .position(courtMarker)
                            .title(court.name)
                    )
                    marker?.tag = court.uuid
                }
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val court = courtsViewModel.courtList.value?.firstOrNull { it.uuid == marker.tag } ?: return false
        findNavController().navigate(MapFragmentDirections.actionNavigationMapToCourtDetailsFragment(court))
        return false
    }

}