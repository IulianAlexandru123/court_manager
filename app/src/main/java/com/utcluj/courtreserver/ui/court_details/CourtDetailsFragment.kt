package com.utcluj.courtreserver.ui.court_details

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs


class CourtDetailsFragment : Fragment() {

    private val courtDetailsViewModel: CourtDetailsViewModel by viewModels()
    private var _binding: FragmentCourtDetailsBinding? = null

    private val binding get() = _binding!!

    private val args: CourtDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourtDetailsBinding.inflate(inflater, container, false)
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
            title = args.courtDto.name
            setNavigationOnClickListener {
                findNavController().popBackStack();
            }
        }
    }

    private fun initViews() {
        courtDetailsViewModel.scheduledDate.observe(viewLifecycleOwner) {
            binding.dateTextView.text = it
        }

        courtDetailsViewModel.startHour.observe(viewLifecycleOwner) {
            binding.startHourTextView.text = it
            updatePrice()
        }

        courtDetailsViewModel.endHour.observe(viewLifecycleOwner) {
            binding.endHourTextView.text = it
            updatePrice()
        }

        binding.dateTextView.setOnClickListener {
            showDatePicker()
        }

        binding.startHourTextView.setOnClickListener {
            showStartTimePicker()
        }

        binding.endHourTextView.setOnClickListener {
            showEndTimePicker()
        }

        binding.reserveButton.setOnClickListener {
            courtDetailsViewModel.reserveCourt(args.courtDto.uuid)
        }
    }

    private fun updatePrice() {
        val startHourInt = Integer.parseInt(
            courtDetailsViewModel.startHour.value?.split(":")?.get(0) ?: "0")
        val endHourInt = Integer.parseInt(courtDetailsViewModel.endHour.value?.split(":")?.get(0) ?: "0")
        val numberOfHours = abs(startHourInt - endHourInt)

        binding.priceTextView.text = (numberOfHours * args.courtDto.pricePerHour).toString()
    }

    private fun showDatePicker() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        datePicker.addOnPositiveButtonClickListener {
            courtDetailsViewModel.setDate(getDate(it))
        }
        datePicker.show(parentFragmentManager, "tag")
    }

    private fun showStartTimePicker() {
        val timePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            .setTitleText("Select time")
            .setHour(8)
            .setMinute(0)
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .build()
        timePicker.addOnPositiveButtonClickListener {
            courtDetailsViewModel.setStartHour(getTime(timePicker))
        }
        timePicker.show(parentFragmentManager, "tag")
    }

    private fun showEndTimePicker() {
        val timePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            .setTitleText("Select time")
            .setHour(8)
            .setMinute(0)
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()
        timePicker.addOnPositiveButtonClickListener {
            courtDetailsViewModel.setEndHour(getTime(timePicker))
        }
        timePicker.show(parentFragmentManager, "tag")
    }

    private fun getDate(timestamp: Long): String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp
        val date = DateFormat.format("dd.MM.yyyy", calendar).toString()
        return date
    }

    private fun getTime(materialTimePicker: MaterialTimePicker): String {
        val pickedHour: Int = materialTimePicker.hour
        val pickedMinute: Int = materialTimePicker.minute
        return when (pickedHour) {
            0 -> {
                if (pickedMinute < 10) {
                    "${materialTimePicker.hour + 12}:0${materialTimePicker.minute}"
                } else {
                    "${materialTimePicker.hour + 12}:${materialTimePicker.minute}"
                }
            }
            else -> {
                if (pickedMinute < 10) {
                    "${materialTimePicker.hour}:0${materialTimePicker.minute}"
                } else {
                    "${materialTimePicker.hour}:${materialTimePicker.minute}"
                }
            }
        }
    }

    private fun initObservers() {
        courtDetailsViewModel.errorMessage.observe(viewLifecycleOwner) {
            if(it == true) {
                Toast.makeText(context,"Date Range already booked!",Toast.LENGTH_SHORT).show()
            }
        }

        courtDetailsViewModel.successMessage.observe(viewLifecycleOwner) {
            if(it == true) {
                Toast.makeText(context,"Booking done!",Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }
}