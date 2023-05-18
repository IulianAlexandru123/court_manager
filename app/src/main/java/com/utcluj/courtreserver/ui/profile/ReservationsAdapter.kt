package com.utcluj.courtreserver.ui.profile

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.utcluj.courtreserver.databinding.ItemReservationBinding
import com.utcluj.courtreserver.dtos.CourtDTO
import com.utcluj.courtreserver.dtos.ReservationDTO
import com.utcluj.courtreserver.dtos.ReservationOverallDetailsDTO
import org.joda.time.Days
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.OffsetTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale


class ReservationsAdapter : RecyclerView.Adapter<ReservationsAdapter.ItemReservationViewHolder>() {

    private var reservationList: List<ReservationOverallDetailsDTO> = listOf()

    var callBack: ((Event) -> Unit)? = null

    fun setItems(reservations: List<ReservationOverallDetailsDTO>) {
        this.reservationList = reservations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReservationsAdapter.ItemReservationViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        return ItemReservationViewHolder(ItemReservationBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = this.reservationList.size

    override fun onBindViewHolder(holder: ItemReservationViewHolder, position: Int) {
        val reservation = reservationList[position].reservation
        val court = reservationList[position].court

        holder.apply {
            courtName.text = court.name
            address.text = court.getAddress(holder.itemView.context).locality
            date.text = reservation.date
            startHour.text = reservation.startHour
            endHour.text = reservation.endHour

            cancelButton.apply {
                isEnabled = reservation.isCancelButtonAvailable()
                setOnClickListener {
                    callBack?.invoke(Event(reservation.uuid))
                }
            }
        }
    }

    inner class ItemReservationViewHolder(binding: ItemReservationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val courtName = binding.courtNameTextView
        val address = binding.addressTextView
        val date = binding.dateTextView
        val startHour = binding.startHourTextView
        val endHour = binding.endHourTextView
        val cancelButton = binding.cancelButton
    }

    private fun CourtDTO.getAddress(context: Context): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(this.lat, this.long, 1)?.first()
            ?: Address(Locale.getDefault())
    }

    private fun ReservationDTO.isCancelButtonAvailable(): Boolean {
        val parser = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val dateTime = org.joda.time.LocalDateTime(parser.parse("${this.date} ${this.startHour}"))
        val daysBetween = Days.daysBetween(org.joda.time.LocalDateTime.now(), dateTime).days

        return daysBetween >= 1
    }

    data class Event(val reservationId: String)
}