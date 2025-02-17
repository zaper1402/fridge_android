package com.ashir.fridge.ui.onboarding.interfaces

interface OTPListener {
    fun onOTPEntered(otp:String)
    fun onOTPChanged(otp: String) // will be called only when otp length < 6
}
