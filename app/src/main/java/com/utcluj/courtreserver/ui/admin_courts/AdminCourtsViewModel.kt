package com.utcluj.courtreserver.ui.admin_courts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.utcluj.courtreserver.dtos.CourtDTO

class AdminCourtsViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _courtList = MutableLiveData<List<CourtDTO>>().apply {
        value = listOf()
    }
    val courtList: LiveData<List<CourtDTO>> = _courtList

    fun getCourts() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("courts").whereEqualTo("ownerUid", currentUserUid).get().addOnSuccessListener {
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
}