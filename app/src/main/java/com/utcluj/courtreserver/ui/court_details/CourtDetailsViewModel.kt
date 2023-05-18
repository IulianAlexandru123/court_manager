package com.utcluj.courtreserver.ui.court_details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.joda.time.Days
import org.joda.time.LocalDateTime
import java.text.SimpleDateFormat

class CourtDetailsViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _scheduledDate = MutableLiveData<String>().apply {
        value = "24.04.2023"
    }
    val scheduledDate: LiveData<String> = _scheduledDate

    private var _startHour = MutableLiveData<String>().apply {
        value = "10:00"
    }
    val startHour: LiveData<String> = _startHour

    private val _endHour = MutableLiveData<String>().apply {
        value = "12:00"
    }
    val endHour: LiveData<String> = _endHour

    private val _errorMessage = MutableLiveData<Boolean>().apply {
        value = false
    }
    val errorMessage: LiveData<Boolean> = _errorMessage

    private val _successMessage = MutableLiveData<Boolean>().apply {
        value = false
    }
    val successMessage: LiveData<Boolean> = _successMessage

    fun setDate(date: String) {
        _scheduledDate.value = date
    }

    fun setStartHour(startHour: String) {
        _startHour.value = startHour
    }

    fun setEndHour(endHour: String) {
        _endHour.value = endHour
    }

    private fun canReserve(courtId: String): Boolean {
        var reservationLength = 0
        db.collection("reservations").whereEqualTo("court_id", courtId)
            .whereEqualTo("date", scheduledDate.value).get().addOnSuccessListener {
                val date = scheduledDate.value ?: return@addOnSuccessListener
                val startHour = startHour.value ?: return@addOnSuccessListener
                val endHour = endHour.value ?: return@addOnSuccessListener

                val startDateTime = convertStringToDate(date, startHour)
                val endDateTime = convertStringToDate(date, endHour)

                reservationLength = it.documents.filter { doc ->
                    Log.e("taag", doc.toString())
                    val date = doc.data?.get("date") as String
                    val startHour = doc.data?.get("start_hour") as String
                    val endHour = doc.data?.get("end_hour") as String

                    val scheduledStartDateTime =  convertStringToDate(date, startHour)
                    val scheduledEndDateTime =  convertStringToDate(date, endHour)

                    scheduledStartDateTime.isBefore(startDateTime) && scheduledEndDateTime.isAfter(endDateTime)
                }.size
            }
        Log.e("taag", reservationLength.toString())
        //return reservationLength == 0
        return true
    }

    private fun convertStringToDate(date: String, hour: String): LocalDateTime {
        val parser = SimpleDateFormat("dd.MM.yyyy HH:mm")
        return org.joda.time.LocalDateTime(parser.parse("$date $hour"))
    }

    fun reserveCourt(courtId: String) {
        if (canReserve(courtId)) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            _errorMessage.value = false
            db.collection("reservations").add(
                hashMapOf(
                    "user_id" to userId,
                    "court_id" to courtId,
                    "date" to _scheduledDate.value,
                    "start_hour" to _startHour.value,
                    "end_hour" to _endHour.value
                )
            ).addOnSuccessListener {
                _successMessage.value = true
            }
        } else {
            _errorMessage.value = true
        }
    }
}