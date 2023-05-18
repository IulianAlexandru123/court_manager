package com.utcluj.courtreserver.dtos

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReservationDTO(
    val uuid: String,
    val courtUuid: String,
    val date: String,
    val startHour: String,
    val endHour: String
) : Parcelable