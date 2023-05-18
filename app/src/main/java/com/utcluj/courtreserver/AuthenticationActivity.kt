package com.utcluj.courtreserver

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AuthenticationActivity : AppCompatActivity() {
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }
    private val db = Firebase.firestore

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            val userUid = user?.uid
            val isNewUser = response?.isNewUser ?: true
            if (isNewUser && userUid != null) {
                db.collection("user_roles").document(userUid).set(
                    hashMapOf(
                        "isAdmin" to false
                    )
                )
                    .addOnSuccessListener {
                        Log.e("taag", "DocumentSnapshot successfully written!")
                        startMainActivity()
                    }
                    .addOnFailureListener { e -> Log.e("taagg", "Error writing document", e) }
            } else {
                startMainActivity()
            }
        } else {
            Log.e("taag", "FAIL $response")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_ui)

        if(FirebaseAuth.getInstance().currentUser != null) {
            startMainActivity()
        } else {
            createSignInIntent()
        }
    }

    private fun startMainActivity() {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(mainActivityIntent)
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }
}