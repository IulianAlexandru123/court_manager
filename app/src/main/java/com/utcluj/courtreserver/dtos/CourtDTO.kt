package com.utcluj.courtreserver.dtos

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CourtDTO(
    val uuid: String,
    val name: String,
    val long: Double,
    val lat: Double,
    val pricePerHour: Long
) : Parcelable