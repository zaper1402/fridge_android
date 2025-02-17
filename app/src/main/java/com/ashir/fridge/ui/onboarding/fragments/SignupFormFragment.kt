package com.ashir.fridge.ui.onboarding.fragments

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ashir.fridge.R
import com.ashir.fridge.account.AccountManager
import com.ashir.fridge.databinding.SignupFormLayoutBinding
import com.ashir.fridge.firebase.FirebaseEventsListener
import com.ashir.fridge.firebase.FirebaseManager
import com.ashir.fridge.firebase.GoogleSignInManager
import com.ashir.fridge.firebase.GoogleSignInManager.Companion.RC_SIGN_IN
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.onboarding.enums.OnboardingStates
import com.ashir.fridge.ui.onboarding.interfaces.OnboardingStateListener
import com.ashir.fridge.ui.onboarding.viemodel.OnboardingSharedViewModel
import com.ashir.fridge.utils.PhoneNumberUtils
import com.ashir.fridge.utils.Utils
import com.ashir.fridge.utils.sharedprefs.SharedPrefConstants
import com.ashir.fridge.utils.sharedprefs.SharedPrefUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.threemusketeers.dliverCustomer.main.utils.extensions.debouncedClickListener
import com.threemusketeers.dliverCustomer.main.utils.extensions.isNonNull
import java.lang.ref.WeakReference


class SignupFormFragment : Fragment() {

    private var mBinding: SignupFormLayoutBinding? = null
    private val auth = Firebase.auth
    private var onboardingStateListener : OnboardingStateListener? = null
    private val mOnboardingSharedViewModel : OnboardingSharedViewModel by activityViewModels()
    private val formFields = HashMap<String, String>(
        mapOf(
            "email" to "",
            "password" to "",
            "confirmPassword" to "",
            "phone_number" to "",
            "name" to "",
        )
    )

    private var isValid:Boolean = false
    private var googleSignInManager: GoogleSignInManager? = null
    private val binding get() = mBinding!!
    companion object {
        const val TAG = "PhoneNumberFragment"
        fun getInstance(email:String?, password: String?,onboardingStateListener: OnboardingStateListener?) = SignupFormFragment().apply {
            this.onboardingStateListener = onboardingStateListener
            val bundle = Bundle()
            bundle.putString("email", email)
            bundle.putString("password", password)
            arguments = bundle
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = SignupFormLayoutBinding.inflate(inflater, container, false)
        googleSignInManager = GoogleSignInManager(WeakReference(context), WeakReference(this))
        googleSignInManager?.setupGoogleSignIn()
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        formFields["email"] = arguments?.getString("email") ?: ""
        formFields["password"] = arguments?.getString("password") ?: ""
        setupUi()
        setupObserver()
        setupClickListeners()
    }

    private fun setupUi() {
        disableEnableLoginButton(false)
        if(formFields["name"]?.isNotBlank() == true){
            mBinding?.editFullName?.setText(formFields["name"])
        }
        if(formFields["email"]?.isNotBlank() == true){
            mBinding?.editEmail?.setText(formFields["email"])
        }
        if(formFields["phone_number"]?.isNotBlank() == true){
            mBinding?.editMobileNumber?.setText(formFields["phone_number"])
        }
        if(formFields["date_of_birth"].isNonNull()){
            mBinding?.editDob?.text = formFields["date_of_birth"]
        }
        if(formFields["password"]?.isNotBlank() == true){
            mBinding?.editPassword?.setText(formFields["password"])
        }

       addTextWatchers()
    }

    private fun addTextWatchers() {
        binding.editFullName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                formFields["name"] = s.toString()
                validateAndEnableButton()
            }
        })
        mBinding?.editMobileNumber?.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(isUpdating) return
                val phoneNumber = s.toString()
                val regex = Regex("[^\\d+]")
                if (phoneNumber.isNotEmpty() && formFields["phone_number"] != phoneNumber) {
                    val cleanNumber = phoneNumber.replace(regex, "")
                    isUpdating = true
                    if(cleanNumber[0] != '+'){
                        mBinding?.editMobileNumber?.setText("+${cleanNumber}")
                        mBinding?.editMobileNumber?.setSelection(cleanNumber.length + 1)
                    }else {
                        mBinding?.editMobileNumber?.setText(cleanNumber)
                        mBinding?.editMobileNumber?.setSelection(cleanNumber.length)
                    }
                    formFields["phone_number"] = cleanNumber
                    validateAndEnableButton()
                    isUpdating = false
                    return
                }
            }
        })
        binding.editEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                formFields["email"] = s.toString()
                validateAndEnableButton()
            }
        })
        binding.editDob.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                formFields["date_of_birth"] = s.toString()
                validateAndEnableButton()
            }
        })
        binding.editPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                formFields["password"] = s.toString()
                validateAndEnableButton()
            }
        })
        binding.editConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                formFields["confirmPassword"] = s.toString()
                validateAndEnableButton()
            }
        })
    }

    private fun validateAndEnableButton() {
        val isValid = formFields["name"]?.isNotBlank() == true &&
            formFields["email"]?.isNotBlank() == true &&
            formFields["password"]?.isNotEmpty() == true &&
            formFields["confirmPassword"].equals(formFields["password"]) &&
            PhoneNumberUtils.isValidPhoneNumber(formFields["phone_number"]) &&
            (formFields["date_of_birth"].isNullOrBlank() || Utils.isValidDate(formFields["date_of_birth"] ?: ""))
        disableEnableLoginButton(isValid)
    }


    private fun disableEnableLoginButton(isEnabled: Boolean) {
        mBinding?.layoutSignUpButton?.isEnabled = isEnabled
        mBinding?.layoutSignUpButton?.alpha = if (isEnabled) 1f else 0.5f
    }




    private fun setupClickListeners() {
        mBinding?.layoutSignUpButton?.debouncedClickListener {
            mOnboardingSharedViewModel.signupUser(formFields)
        }

        mBinding?.textLogInLink?.debouncedClickListener {
            mOnboardingSharedViewModel.setOnboardingState(OnboardingStates.LOGIN)
        }
        mBinding?.layoutGoogleSignUp?.setOnClickListener {
//            showLoading()
            googleSignInManager?.signInWithGoogle()
        }

        mBinding?.editDob?.setOnClickListener {
            Utils.showDatePickerDialog(requireContext(), mBinding?.editDob)
        }

        binding.imagePasswordVisibility.debouncedClickListener {
            if (binding.editPassword.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                binding.editPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.imagePasswordVisibility.setColorFilter(R.color.button_primary, PorterDuff.Mode.SRC_IN)
            } else {
                binding.editPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.imagePasswordVisibility.colorFilter = null
            }
            // Move the cursor to the end of the text
            binding.editPassword.setSelection(binding.editPassword.text.length)
        }

        binding.imageConfirmPasswordVisibility.debouncedClickListener {
            if (binding.editConfirmPassword.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                binding.editConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.imageConfirmPasswordVisibility.setColorFilter(R.color.button_primary, PorterDuff.Mode.SRC_IN)
            } else {
                binding.editConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.imageConfirmPasswordVisibility.colorFilter = null
            }
            // Move the cursor to the end of the text
            binding.editConfirmPassword.setSelection(binding.editConfirmPassword.text.length)
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

                    SharedPrefUtil.getAuthInstance().saveString(
                        SharedPrefConstants.LOGIN_METHOD,
                        SharedPrefConstants.GOOGLE_METHOD)
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
                    mOnboardingSharedViewModel.verifyUser()
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
        mOnboardingSharedViewModel.signupUserLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Error<*> -> {
                    showToast(it.responseBody.toString())
                }
                is Result.InProgress -> {

                }
                is Result.Success -> {
                    AccountManager.saveAccountInfo(it.data)
                    mOnboardingSharedViewModel.setOnboardingState(OnboardingStates.COMPLETED)

                }
            }
        }
    }



    private fun showToast(text: String){
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}