package com.utcluj.courtreserver.ui.courts

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.utcluj.courtreserver.R
import com.utcluj.courtreserver.databinding.FragmentCourtsBinding
import com.utcluj.courtreserver.databinding.ItemCourtBinding
import com.utcluj.courtreserver.dtos.CourtDTO
import java.util.Locale

class CourtsAdapter : RecyclerView.Adapter<CourtsAdapter.ItemCourtViewHolder>() {

    private var courtList: List<CourtDTO> = listOf()

    var callBack: ((Event)->Unit)? = null

    fun setItems(courts: List<CourtDTO>) {
        this.courtList = courts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CourtsAdapter.ItemCourtViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        return ItemCourtViewHolder(ItemCourtBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = this.courtList.size

    override fun onBindViewHolder(holder: ItemCourtViewHolder, position: Int) {
        val court = courtList[position]

        holder.apply {
            courtName.text = court.name
            pricePerHour.text = "Price per hour ${court.pricePerHour} LEI"
            address.text = court.getAddress(holder.itemView.context).locality
            itemView.setOnClickListener {
                callBack?.invoke(Event(court))
            }
        }
    }

    inner class ItemCourtViewHolder(binding: ItemCourtBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val courtName = binding.courtNameTextView
        val pricePerHour = binding.pricePerHourTextView
        val address = binding.addressTextView
    }

    private fun CourtDTO.getAddress(context: Context) : Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(this.lat, this.long, 1)?.first() ?: Address(Locale.getDefault())
    }

    data class Event(val courtDTO: CourtDTO)
}