package com.utcluj.courtreserver.dtos

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReservationOverallDetailsDTO(
    val reservation: ReservationDTO,
    val court: CourtDTO
) : Parcelable