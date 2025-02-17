package com.ashir.fridge.ui.onboarding.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ashir.fridge.R
import com.ashir.fridge.databinding.SignupFormLayoutBinding
import com.ashir.fridge.firebase.FirebaseEventsListener
import com.ashir.fridge.firebase.FirebaseManager
import com.ashir.fridge.ui.onboarding.interfaces.OnboardingStateListener
import com.ashir.fridge.ui.onboarding.viemodel.OnboardingSharedViewModel
import com.ashir.fridge.utils.sharedprefs.SharedPrefUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.threemusketeers.dliverCustomer.main.utils.sharedprefs.SharedPrefConstants
import java.lang.ref.WeakReference


class SignupFormFragment : Fragment() {

    private var mBinding: SignupFormLayoutBinding? = null
    private val auth = Firebase.auth
    private var onboardingStateListener : OnboardingStateListener? = null
    private val mOnboardingSharedViewModel : OnboardingSharedViewModel by activityViewModels()

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    private var isValid:Boolean = false

    companion object {
        const val TAG = "PhoneNumberFragment"
        fun getInstance(onboardingStateListener: OnboardingStateListener?) = SignupFormFragment().apply {
            this.onboardingStateListener = onboardingStateListener
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = SignupFormLayoutBinding.inflate(inflater, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        setupUi()
        setupGoogleSignIn()
        setupClickListeners()
    }

    private fun setupUi() {
        disableEnableLoginButton(false)





        disableEnableLoginButton(mBinding?.editEmail?.text?.isNotEmpty() == true)
        mBinding?.editEmail?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val phoneNumber = s.toString()
                // Remove any non-numeric characters if they somehow get through
                if (phoneNumber.isNotEmpty() && !phoneNumber.matches(Regex("\\d+"))) {
                    val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")
                    mBinding?.editEmail?.setText(cleanNumber)
                    mBinding?.editEmail?.setSelection(cleanNumber.length)
                    return
                }

                isValid = isValidIndianPhoneNumber(phoneNumber)
                disableEnableLoginButton(phoneNumber.length ==10 )
            }
        })
    }

    private fun setupGoogleSignIn() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }



    private fun disableEnableLoginButton(isEnabled: Boolean) {
        mBinding?.textSignUpButton?.isEnabled = isEnabled
        mBinding?.textSignUpButton?.alpha = if (isEnabled) 1f else 0.5f
    }

    private fun isValidIndianPhoneNumber(phoneNumber: String): Boolean {
        val regex = Regex("^[6-9]\\d{9}$")
        return phoneNumber.length == 10 && regex.matches(phoneNumber)
    }
    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }


    private fun setupClickListeners() {
        mBinding?.textSignUpButton?.setOnClickListener {
            if(!isValid){
                showToast("Phone number not valid")
            } else{
                val phoneNumber = mBinding?.editEmail?.text.toString()
                showToast("OTP is send to your number")
//                mBinding?.frame?.id?.let { it1 ->
//                    activity?.supportFragmentManager?.beginTransaction()?.replace(it1,
//                        OTPVerificationFragment.getInstance(onboardingStateListener, phoneNumber)
//                    )
//                        ?.addToBackStack(OTPVerificationFragment.TAG)
//                        ?.commitAllowingStateLoss()
//                }
            }

        }

        mBinding?.editEmail?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                if(!isValid){
                    showToast("Phone number not valid")
                } else{
                    val phoneNumber = mBinding?.editEmail?.text.toString()
                    showToast("OTP is send to your number")
//                    mBinding?.frame?.id?.let { it1 ->
//                        activity?.supportFragmentManager?.beginTransaction()?.replace(it1,
//                            OTPVerificationFragment.getInstance(onboardingStateListener, phoneNumber)
//                        )
//                            ?.addToBackStack(OTPVerificationFragment.TAG)
//                            ?.commitAllowingStateLoss()
//                    }
                }
            }
            false
        })
        mBinding?.layoutGoogleSignUp?.setOnClickListener {
//            showLoading()
            signInWithGoogle()
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
//                hideLoading()
                Log.w(TAG, "Google sign in failed", e)
                // Handle failure
                showToast("Google Sign In Failed")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {

                    SharedPrefUtil.getAuthInstance().saveString(SharedPrefConstants.LOGIN_METHOD,SharedPrefConstants.GOOGLE_METHOD)
                    val user = auth.currentUser
                    handleSuccessfulLogin(user)
                } else {
//                    hideLoading()
                    // Sign in failed
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    showToast("Authentication Failed")
                }
            }
    }

    private fun firebaseAuthWithFacebook(idToken: String) {
        val credential = FacebookAuthProvider.getCredential(idToken)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    handleSuccessfulLogin(user)
                } else {
                    // Sign in failed
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun handleSuccessfulLogin(user: FirebaseUser?) {
        user?.let {
            FirebaseManager.getUserToken(WeakReference(context), user, object :
                FirebaseEventsListener {
                override fun onTokenReceived(idToken: String) {
                    // Verify user
//                    mOnboardingSharedViewModel.verifyUser()
                }

                override fun onTokenError(error: String?) {
//                    hideLoading()
                    showToast("Something went wrong!")
                    Log.e(TAG, "Token Error: $error")
                }
            })
        }
    }

    private fun setupObserver() {
//        mOnboardingSharedViewModel.verifyStatusLiveData.observe(viewLifecycleOwner) {
//            when(it){
//                is Result.Success -> {
//                    hideLoading()
//                }
//                is Result.InProgress -> {}
//                is Result.Error<*> -> {}
//            }
//        }
    }




//    private fun showLoading(){
//        mBinding?.progressBar2?.progressOverlay?.visibility = View.VISIBLE
//    }
//    private fun hideLoading(){
//        mBinding?.progressBar2?.progressOverlay?.visibility = View.GONE
//    }

    private fun showToast(text: String){
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}