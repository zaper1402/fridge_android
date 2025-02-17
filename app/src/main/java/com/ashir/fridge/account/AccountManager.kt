package com.ashir.fridge.account

import com.ashir.fridge.account.pojo.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

object AccountManager {

    val uid: String?
        get() = Firebase.auth.uid
    var user : User? = null
        private set


    fun saveAccountInfo(data: User?) {
        user = data
    }

}