package com.ashir.fridge.account

import com.ashir.fridge.account.pojo.User
import com.ashir.fridge.utils.sharedprefs.SharedPrefConstants
import com.ashir.fridge.utils.sharedprefs.SharedPrefUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

object AccountManager {

    var uid: String? = null
        private set
        get() = field ?: SharedPrefUtil.getAuthInstance().getString(SharedPrefConstants.UID) ?: Firebase.auth.uid
    var user : User? = null
        private set


    fun saveAccountInfo(data: User?) {
        data ?: return
        SharedPrefUtil.getAuthInstance().saveString(SharedPrefConstants.UID, data?.id.toString())
        uid = data.id.toString()
        user = data
    }

    fun resetAccountInfo() {
        SharedPrefUtil.getAuthInstance().remove(SharedPrefConstants.UID)
        uid = null
        user = null
    }

}