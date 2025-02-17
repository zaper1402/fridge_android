package com.ashir.fridge.ui.onboarding.enums

import org.json.JSONObject
import java.io.Serializable

enum class OnboardingStates {
    UNKNOWN,
    LOGIN,
    CUSTOMER_DETAILS_SCREEN,
    COMPLETED;

    companion object {
        fun getValueOrDefault(name : String?): OnboardingStates {
            name?: return UNKNOWN
            return try {
                OnboardingStates.valueOf(name)
            }catch (e : Exception) {
                UNKNOWN
            }
        }

    }
}

data class OnboardingStatesData(val state : OnboardingStates, val data : JSONObject?) : Serializable