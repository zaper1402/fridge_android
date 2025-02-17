package com.ashir.fridge.ui.onboarding.interfaces

import com.ashir.fridge.ui.onboarding.enums.OnboardingStates
import com.google.firebase.auth.FirebaseUser

interface OnboardingStateListener {
    fun onLoginSuccessful(state: OnboardingStates, user : FirebaseUser?)

    fun onRegistrationSuccessful(state: OnboardingStates, user : FirebaseUser?)
}