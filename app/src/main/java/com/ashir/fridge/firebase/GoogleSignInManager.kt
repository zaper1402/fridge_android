package com.ashir.fridge.firebase

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ashir.fridge.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import java.lang.ref.WeakReference

class GoogleSignInManager(private val context : WeakReference<Context>,private val fragment: WeakReference<Fragment>) {
    companion object{
        const val RC_SIGN_IN = 9001
    }
    private lateinit var googleSignInClient: GoogleSignInClient

    fun setupGoogleSignIn() {
        // Configure Google Sign In
        val ref = context.get()
        if(ref == null){
            Toast.makeText(ref, "Activity is null", Toast.LENGTH_SHORT).show()
            return
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(ref.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(ref, gso)
    }

    fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            fragment.get()?.startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }
}