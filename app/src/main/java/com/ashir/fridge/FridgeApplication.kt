package com.ashir.fridge

import android.app.Application
import android.util.Log
import com.ashir.fridge.firebase.FirebaseManager
import com.google.android.gms.net.CronetProviderInstaller
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.ref.WeakReference

class FridgeApplication : Application() {
    companion object {
        const val TAG  = "FridgeApplication"
        @JvmStatic
        lateinit var instance : FridgeApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CronetProviderInstaller.installProvider(this).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.i(TAG, "Successfully installed Play Services provider: $it")
            } else {
                Log.w(TAG, "Unable to load Cronet from Play Services", it.exception)
            }
        }
        setFirebaseTokenIfExist()
    }

    private fun setFirebaseTokenIfExist() {
//        Firebase.auth
        Firebase.auth.currentUser?.let {
            FirebaseManager.getUserToken(WeakReference(this),it)
        }
    }
}