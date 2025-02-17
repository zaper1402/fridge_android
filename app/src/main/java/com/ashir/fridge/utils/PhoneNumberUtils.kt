package com.ashir.fridge.utils

object PhoneNumberUtils {

    val phoneNumberRegex = Regex("^\\+\\d+$")

    enum class Countries(val country: String, val extension:String){
        INDIA("India","+91"),
        FINLAND("Finland","+358"),
    }

    /**
    * This function checks if the phone number is valid or not for number inclusive of country code
     */
    fun isValidPhoneNumber(phoneNumber: String?): Boolean {
        phoneNumber?: return false
        return isValidIndianPhoneNumber(phoneNumber) ||
                isValidFinnishPhoneNumber(phoneNumber)
    }

    private fun isValidIndianPhoneNumber(phoneNumber: String): Boolean {
        val regex = Regex("^[6-9]\\d{9}$")
        return phoneNumber.length == 13 && regex.matches(phoneNumber)
    }

    private fun isValidFinnishPhoneNumber(phoneNumber: String): Boolean {
        val regex = Regex("^\\+358\\d{9}$")
        return phoneNumber.length == 13 && regex.matches(phoneNumber)
    }
}