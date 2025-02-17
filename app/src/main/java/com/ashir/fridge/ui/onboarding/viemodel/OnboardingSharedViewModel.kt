package com.ashir.fridge.ui.onboarding.viemodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ashir.fridge.ui.BaseViewModel
import com.ashir.fridge.ui.onboarding.enums.OnboardingStates
import com.ashir.fridge.ui.onboarding.enums.OnboardingStatesData
import com.ashir.fridge.utils.sharedprefs.SharedPrefUtil
import com.threemusketeers.dliverCustomer.main.utils.sharedprefs.SharedPrefConstants
import org.json.JSONObject

class OnboardingSharedViewModel : BaseViewModel() {

    val onboardingStateLiveData : LiveData<OnboardingStatesData?> get() = _onboardingStateLiveData
    private val _onboardingStateLiveData = MutableLiveData(
        SharedPrefUtil.getDefaultInstance().getDataObject(SharedPrefConstants.ONBOARDING_STATE, OnboardingStatesData::class.java)?: OnboardingStatesData(OnboardingStates.UNKNOWN,null)
    )

    fun setOnboardingState(state: OnboardingStates, data: JSONObject? = null){
        val onbData = OnboardingStatesData(state,data)
        SharedPrefUtil.getDefaultInstance().saveDataObject(SharedPrefConstants.ONBOARDING_STATE,onbData)
        _onboardingStateLiveData.postValue(onbData)
    }


}