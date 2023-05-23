package com.utcluj.courtreserver.ui.court_details

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.type.DateTime
import com.utcluj.courtreserver.databinding.FragmentCourtDetailsBinding
import com.utcluj.courtreserver.databinding.FragmentCourtsBinding
import com.utcluj.courtreserver.databinding.FragmentCreateCourtDetailsBinding
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs


class CreateCourtDetailsFragment : Fragment() {

    private val courtDetailsViewModel: CreateCourtDetailsViewModel by viewModels()
    private var _binding: FragmentCreateCourtDetailsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateCourtDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        performInitialServerRequest()

        initToolbar()
        initViews()
        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun performInitialServerRequest() {

    }

    private fun initToolbar() {
        binding.toolbar.apply {
            title = "Create new court"
            setNavigationOnClickListener {
                findNavController().popBackStack();
            }
        }
    }

    private fun initViews() {
        binding.createButton.setOnClickListener {
            courtDetailsViewModel.reserveCourt();
        }

        binding.courtNameTextField.editText?.doOnTextChanged { text, _, _, _ ->
            courtDetailsViewModel.setName(text.toString())
            isCreateButtonEnabled()
        }

        binding.latTextField.editText?.doOnTextChanged { text, _, _, _ ->
            courtDetailsViewModel.setLat(text.toString())
            isCreateButtonEnabled()
        }

        binding.longTextField.editText?.doOnTextChanged { text, _, _, _ ->
            courtDetailsViewModel.setLong(text.toString())
            isCreateButtonEnabled()
        }

        binding.priceTextField.editText?.doOnTextChanged { text, _, _, _ ->
            courtDetailsViewModel.setPrice(text.toString())
            isCreateButtonEnabled()
        }
    }

    private fun initObservers() {
        courtDetailsViewModel.errorMessage.observe(viewLifecycleOwner) {
            if(it == true) {
                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
            }
        }

        courtDetailsViewModel.successMessage.observe(viewLifecycleOwner) {
            if(it == true) {
                Toast.makeText(context,"Court created!",Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun isCreateButtonEnabled() {
        val isNameNotEmpty = courtDetailsViewModel.courtName.value?.isNotEmpty() ?: false
        val isLatNotEmpty = courtDetailsViewModel.courtLat.value?.isNotEmpty() ?: false
        val isLongNotEmpty = courtDetailsViewModel.courtLong.value?.isNotEmpty() ?: false
        val isPriceNotEmpty = courtDetailsViewModel.pricePerHour.value?.isNotEmpty() ?: false

        binding.createButton.isEnabled = isNameNotEmpty && isLatNotEmpty && isLongNotEmpty && isPriceNotEmpty
    }
}