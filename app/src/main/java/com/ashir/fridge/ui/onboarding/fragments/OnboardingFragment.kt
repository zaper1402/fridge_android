package com.ashir.fridge.ui.onboarding.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ashir.fridge.databinding.LoginFormLayoutBinding
import com.ashir.fridge.ui.onboarding.enums.OnboardingStates
import com.ashir.fridge.ui.onboarding.interfaces.OnboardingStateListener
import com.ashir.fridge.ui.onboarding.viemodel.OnboardingSharedViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.threemusketeers.dliverCustomer.main.utils.extensions.addPressFeedback

class OnboardingFragment : Fragment() {

    private var mBinding: LoginFormLayoutBinding? = null
    private val auth = Firebase.auth
    private var onboardingStateListener : OnboardingStateListener? = null
    private val mOnboardingSharedViewModel : OnboardingSharedViewModel by activityViewModels()

    companion object {
        const val TAG = "OnboardingFragment"
        fun getInstance(onboardingStateListener: OnboardingStateListener?) = OnboardingFragment().apply {
            this.onboardingStateListener = onboardingStateListener
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = LoginFormLayoutBinding.inflate(inflater, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.layoutGoogleSignUp?.addPressFeedback()
        setupObserver()
    }



    private fun setupObserver() {
        mOnboardingSharedViewModel.onboardingStateLiveData.observe(viewLifecycleOwner) {
            when(it?.state){
                OnboardingStates.UNKNOWN -> {
                    if(auth.currentUser != null){
//                        mOnboardingSharedViewModel.getCustomerAccountData(auth.uid)
                    }
                }
                OnboardingStates.CUSTOMER_DETAILS_SCREEN -> {
                    openLoginFragment()
                }

                OnboardingStates.COMPLETED -> {
                    onboardingStateListener?.onLoginSuccessful(OnboardingStates.COMPLETED,auth.currentUser)
                }
                else ->{}
            }

        }

//        mOnboardingSharedViewModel.accountStatusLiveData.observe(viewLifecycleOwner) {
//            when(it){
//                is Result.InProgress -> {
//                    //todo show loading
//                }
//                is Result.Success -> {
//                    AccountManager.saveAccountInfo(it.data)
//                    handleState()
//                }
//                is Result.Error<*> -> {
//                    //todo show error
//                }
//            }
//        }
//
//        mOnboardingSharedViewModel.verifyStatusLiveData.observe(viewLifecycleOwner) {
//            when(it){
//                is Result.Success -> {
//                    AccountManager.saveAccountInfo(it.data)
//                    handleState()
//                }
//                is Result.InProgress -> {}
//                is Result.Error<*> -> {}
//            }
//        }
    }
//    private fun handleState() {
//        if(AccountManager.customer?.phoneNo.isNullOrBlank() || AccountManager.customer?.email.isNullOrBlank()){
//            mOnboardingSharedViewModel.setOnboardingState(OnboardingStates.CUSTOMER_DETAILS_SCREEN)
//        }else if(AccountManager.customer?.locations?.isEmpty() == true){
//            mOnboardingSharedViewModel.setOnboardingState(OnboardingStates.GET_LOCATION)
//        }else {
//            mOnboardingSharedViewModel.setOnboardingState(OnboardingStates.COMPLETED)
//        }
//    }
//
    private fun openLoginFragment(isDefaultScreen: Boolean = true) {
        mBinding?.loginFrame?.id?.let { frame ->
            val fragment = activity?.supportFragmentManager?.beginTransaction()?.replace(frame,
                SignupFormFragment.getInstance(onboardingStateListener)
            )
            if(isDefaultScreen.not()){
                fragment?.addToBackStack(SignupFormFragment.TAG)
            }
            fragment?.commitAllowingStateLoss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}