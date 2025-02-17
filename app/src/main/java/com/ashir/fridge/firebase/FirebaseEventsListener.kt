package com.ashir.fridge.firebase

interface FirebaseEventsListener {

    fun onTokenReceived(idToken: String)

    fun onTokenError(error: String?)
}