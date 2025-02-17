package com.ashir.fridge.firebase

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseUser
import com.ashir.fridge.utils.sharedprefs.SharedPrefConstants
import com.ashir.fridge.utils.sharedprefs.SharedPrefUtil
import java.lang.ref.WeakReference

object FirebaseManager {
    const val TAG = "FirebaseManager"
    fun getUserToken(context : WeakReference<Context>, user: FirebaseUser?, firebaseEventsListener: FirebaseEventsListener? = null) {
        user?.getIdToken(true)?.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result.token
                if(idToken.isNullOrBlank()){
                    Toast.makeText(context.get(),"Something went wrong failed", Toast.LENGTH_SHORT).show()
                    firebaseEventsListener?.onTokenError(null)
                    return@OnCompleteListener
                }
                Log.i(TAG, "firebaseToken: $idToken")
                SharedPrefUtil.getAuthInstance().saveString(SharedPrefConstants.TOKEN,idToken)
                firebaseEventsListener?.onTokenReceived(idToken)
            } else {
                Toast.makeText(context.get(),"Something went wrong failed", Toast.LENGTH_SHORT).show()
                firebaseEventsListener?.onTokenError(null)
            }
        })?: {
            Toast.makeText(context.get(),"user not generated", Toast.LENGTH_SHORT).show()
            firebaseEventsListener?.onTokenError(null)
        }
    }
}