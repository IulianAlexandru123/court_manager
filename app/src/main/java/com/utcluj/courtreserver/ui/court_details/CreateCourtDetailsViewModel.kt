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

class CreateCourtDetailsViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val _courtName = MutableLiveData<String>().apply {
        value = ""
    }
    val courtName: LiveData<String> = _courtName

    private val _courtLat = MutableLiveData<String>().apply {
        value = ""
    }
    val courtLat: LiveData<String> = _courtLat

    private val _courtLong = MutableLiveData<String>().apply {
        value = ""
    }
    val courtLong: LiveData<String> = _courtLong

    private val _pricePerHour = MutableLiveData<String>().apply {
        value = ""
    }
    val pricePerHour: LiveData<String> = _pricePerHour

    private val _errorMessage = MutableLiveData<Boolean>().apply {
        value = false
    }
    val errorMessage: LiveData<Boolean> = _errorMessage

    private val _successMessage = MutableLiveData<Boolean>().apply {
        value = false
    }
    val successMessage: LiveData<Boolean> = _successMessage

    fun setName(name: String) {
        _courtName.value = name
    }

    fun setLat(lat: String) {
        _courtLat.value = lat
    }

    fun setLong(long: String) {
        _courtLong.value = long
    }

    fun setPrice(price: String) {
        _pricePerHour.value = price
    }

    fun reserveCourt() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("courts").add(
            hashMapOf(
                "ownerUid" to userId,
                "lat" to _courtLat.value?.toDouble(),
                "long" to _courtLong.value?.toDouble(),
                "name" to _courtName.value,
                "pricePerHour" to _pricePerHour.value?.toInt()
            )
        ).addOnSuccessListener {
            _successMessage.value = true
        }.addOnCanceledListener {
            _errorMessage.value = true
        }
    }
}