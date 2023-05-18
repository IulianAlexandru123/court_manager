package com.utcluj.courtreserver.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.utcluj.courtreserver.dtos.CourtDTO
import com.utcluj.courtreserver.dtos.ReservationDTO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val db = Firebase.firestore

    private val _courtList = MutableStateFlow<List<CourtDTO>>(listOf())
    val courtList: SharedFlow<List<CourtDTO>> = _courtList.asStateFlow()

    private val _reservationList = MutableStateFlow<List<ReservationDTO>>(listOf())
    val reservationList: SharedFlow<List<ReservationDTO>> = _reservationList.asStateFlow()

    private val _deleteReservationResult = MutableStateFlow<Boolean>(false)
    val deleteReservationResult: SharedFlow<Boolean> = _deleteReservationResult.asStateFlow()

    fun getReservations() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        db.collection("reservations").whereEqualTo("user_id", userId).get().addOnSuccessListener {
            val reservations = it.documents.mapNotNull { doc ->
                val courtUuid = doc.data?.get("court_id") as String
                val date = doc.data?.get("date") as String
                val startHour = doc.data?.get("start_hour") as String
                val endHour = doc.data?.get("end_hour") as String

                ReservationDTO(doc.id, courtUuid, date, startHour, endHour)
            }
            _reservationList.value = reservations
        }
    }

    fun getCourts() {
        db.collection("courts").get().addOnSuccessListener {
            val courts = it.documents.map { doc ->
                val courtName = doc.data?.get("name") as String
                val courtLat = doc.data?.get("lat") as Double
                val courtLong = doc.data?.get("long") as Double
                val pricePerHour = doc.data?.get("pricePerHour") as Long

                CourtDTO(doc.id, courtName, courtLong, courtLat, pricePerHour)
            }
            _courtList.value = courts
        }
    }

    fun deleteReservation(reservationUuid: String) {
        db.collection("reservations").document(reservationUuid).delete().addOnCanceledListener {
            _deleteReservationResult.value = true
        }
    }

    private val _text = MutableLiveData<String>().apply {
        value = "Welcome, " + firebaseAuth.currentUser?.displayName
    }
    val text: LiveData<String> = _text

    fun logOutUser() {
        firebaseAuth.signOut()
    }
}