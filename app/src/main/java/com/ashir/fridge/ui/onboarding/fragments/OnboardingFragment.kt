package com.ashir.fridge.ui.onboarding.fragments

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ashir.fridge.R
import com.ashir.fridge.account.AccountManager
import com.ashir.fridge.account.pojo.User
import com.ashir.fridge.databinding.LoginFormLayoutBinding
import com.ashir.fridge.firebase.FirebaseEventsListener
import com.ashir.fridge.firebase.FirebaseManager
import com.ashir.fridge.firebase.GoogleSignInManager
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.onboarding.enums.OnboardingStates
import com.ashir.fridge.ui.onboarding.interfaces.OnboardingStateListener
import com.ashir.fridge.ui.onboarding.viemodel.OnboardingSharedViewModel
import com.ashir.fridge.utils.GsonUtils
import com.ashir.fridge.utils.extensions.addPressFeedback
import com.ashir.fridge.utils.sharedprefs.SharedPrefConstants
import com.ashir.fridge.utils.sharedprefs.SharedPrefUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.threemusketeers.dliverCustomer.main.utils.ViewUtils
import com.threemusketeers.dliverCustomer.main.utils.extensions.debouncedClickListener
import com.threemusketeers.dliverCustomer.main.utils.extensions.isNull
import com.threemusketeers.dliverCustomer.main.utils.extensions.setGone
import com.threemusketeers.dliverCustomer.main.utils.extensions.setVisible
import java.lang.ref.WeakReference

class OnboardingFragment : Fragment() {

    private var mBinding: LoginFormLayoutBinding? = null
    private val binding get() = mBinding!!
    private val auth = Firebase.auth
    private var onboardingStateListener : OnboardingStateListener? = null
    private val mOnboardingSharedViewModel : OnboardingSharedViewModel by activityViewModels()
    private val googleSignInManager : GoogleSignInManager by lazy {
        GoogleSignInManager(WeakReference(context),WeakReference(this))
    }
    companion object {
        const val TAG = "OnboardingFragment"
        fun getInstance(onboardingStateListener: OnboardingStateListener?) = OnboardingFragment().apply {
            this.onboardingStateListener = onboardingStateListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = LoginFormLayoutBinding.inflate(inflater, container, false)
        googleSignInManager.setupGoogleSignIn()
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupObserver()
        mBinding?.layoutGoogleSignUp?.addPressFeedback()
    }

    private fun setupUi() {
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.imagePasswordVisibility.debouncedClickListener {
            if (binding.editPassword.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                binding.editPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.imagePasswordVisibility.setColorFilter(ContextCompat.getColor(requireContext(), R.color.button_primary))
            } else {
                binding.editPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.imagePasswordVisibility.colorFilter = null
            }
            // Move the cursor to the end of the text
            binding.editPassword.setSelection(binding.editPassword.text.length)
        }

        binding.loginBtn.debouncedClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()

            if (handleFieldValidation(email, password)) {
                mOnboardingSharedViewModel.loginUser(email, password)
            }
        }

        binding.signupBtn.debouncedClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            openSignUpFormFragment(email, password,false)
        }

        binding.layoutGoogleSignUp.debouncedClickListener{
            googleSignInManager.signInWithGoogle()
        }


    }

    private fun handleFieldValidation(email: String, password: String): Boolean {
        if(email.isBlank()){
            Toast.makeText(context, "Email invalid", Toast.LENGTH_SHORT).show()
            return false
        }else if(password.isBlank()){
            Toast.makeText(context, "Password invalid", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }



    private fun setupObserver() {
        mOnboardingSharedViewModel.onboardingStateLiveData.observe(viewLifecycleOwner) {
            when(it?.state){
                OnboardingStates.UNKNOWN -> {
                    if(auth.currentUser != null){
                        binding.onbFrame.setGone()
                        mOnboardingSharedViewModel.setOnboardingState(OnboardingStates.COMPLETED)
                    }else {
                        mOnboardingSharedViewModel.setOnboardingState(OnboardingStates.LOGIN)
                    }
                }
                OnboardingStates.LOGIN -> {
                    binding.onbFrame.removeAllViews()
                    binding.onbFrame.setGone()
                }
                OnboardingStates.CUSTOMER_DETAILS_SCREEN -> {
                    val email = it.data?.optString("email")?:""
                    val password = it.data?.optString("password")?:""
                    if(handleFieldValidation(email,password)){
                        openSignUpFormFragment(email,password,false)
                    }else {
                        mOnboardingSharedViewModel.setOnboardingState(OnboardingStates.UNKNOWN)
                    }
                }

                OnboardingStates.COMPLETED -> {
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    onboardingStateListener?.onLoginSuccessful(OnboardingStates.COMPLETED,auth.currentUser)
                }
                else ->{}
            }

        }

        mOnboardingSharedViewModel.userLoginLiveData.observe(viewLifecycleOwner) {
            when(it){
                is Result.Success -> {
                    AccountManager.saveAccountInfo(it.data)
                    mOnboardingSharedViewModel.setOnboardingState(OnboardingStates.COMPLETED,GsonUtils.toJson(it.data))
                }
                is Result.InProgress -> {
                    //todo show loading
                }
                is Result.Error<*> -> {
                    ViewUtils.showGenericErrorToast(context)
                }
            }
        }

        mOnboardingSharedViewModel.verifyUserLiveData.observe(viewLifecycleOwner) {
            when(it){
                is Result.Success -> {
                    AccountManager.saveAccountInfo(it.data)
                    handleState(it.data)
                }
                is Result.InProgress -> {}
                is Result.Error<*> -> {
                    Toast.makeText(context, "User verification failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleState(data: User) {
        if(AccountManager.user?.phoneNumber.isNullOrBlank() || AccountManager.user?.email.isNullOrBlank() || AccountManager.user?.name.isNullOrBlank() || AccountManager.user?.dateOfBirth.isNull()){
            mOnboardingSharedViewModel.setOnboardingState(OnboardingStates.CUSTOMER_DETAILS_SCREEN)
        } else {
            mOnboardingSharedViewModel.setOnboardingState(OnboardingStates.COMPLETED,GsonUtils.toJson(data))
        }
    }

    private fun openSignUpFormFragment(email: String,password: String, isDefaultScreen: Boolean = true) {
        mBinding?.onbFrame?.id?.let { frame ->
            binding.onbFrame.setVisible()
            val fragment = activity?.supportFragmentManager?.beginTransaction()?.replace(frame,
                SignupFormFragment.getInstance(email,password,onboardingStateListener)
            )
            if(isDefaultScreen.not()){
                fragment?.addToBackStack(SignupFormFragment.TAG)
            }
            fragment?.commitAllowingStateLoss()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GoogleSignInManager.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
//                hideLoading()
                Log.w(SignupFormFragment.TAG, "Google sign in failed", e)
                // Handle failure
                Toast.makeText(context, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {

                    SharedPrefUtil.getAuthInstance().saveString(
                        SharedPrefConstants.LOGIN_METHOD,
                        SharedPrefConstants.GOOGLE_METHOD)
                    val user = auth.currentUser
                    handleSuccessfulLogin(user)
                } else {
//                    hideLoading()
                    // Sign in failed
                    Log.w(SignupFormFragment.TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun handleSuccessfulLogin(user: FirebaseUser?) {
        user?.let {
            FirebaseManager.getUserToken(WeakReference(context), user, object :
                FirebaseEventsListener {
                override fun onTokenReceived(idToken: String) {
                    mOnboardingSharedViewModel.verifyUser()
                }

                override fun onTokenError(error: String?) {
//                    hideLoading()
                    Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show()
                    Log.e(SignupFormFragment.TAG, "Token Error: $error")
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}