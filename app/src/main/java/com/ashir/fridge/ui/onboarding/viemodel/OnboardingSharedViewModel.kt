package com.ashir.fridge.ui.onboarding.viemodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ashir.fridge.account.AccountRepository
import com.ashir.fridge.account.pojo.User
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.BaseViewModel
import com.ashir.fridge.ui.onboarding.enums.OnboardingStates
import com.ashir.fridge.ui.onboarding.enums.OnboardingStatesData
import com.ashir.fridge.ui.onboarding.repository.OnboardingRepository
import com.ashir.fridge.utils.sharedprefs.SharedPrefConstants
import com.ashir.fridge.utils.sharedprefs.SharedPrefUtil
import kotlinx.coroutines.launch
import org.json.JSONObject

class OnboardingSharedViewModel : BaseViewModel() {

    val onboardingStateLiveData : LiveData<OnboardingStatesData?> get() = _onboardingStateLiveData
    private val _onboardingStateLiveData = MutableLiveData(
        SharedPrefUtil.getDefaultInstance().getDataObject(SharedPrefConstants.ONBOARDING_STATE, OnboardingStatesData::class.java)?: OnboardingStatesData(OnboardingStates.UNKNOWN,null)
    )

    val userLoginLiveData : LiveData<Result<User>> get() = _userLoginLiveData
    private val _userLoginLiveData = MutableLiveData<Result<User>>()

    val verifyUserLiveData : LiveData<Result<User>> get() = _verifyUserLiveData
    private val _verifyUserLiveData = MutableLiveData<Result<User>>()

    val signupUserLiveData : LiveData<Result<User>> get() = _signupUserLiveData
    private val _signupUserLiveData = MutableLiveData<Result<User>>()

    fun setOnboardingState(state: OnboardingStates, data: JSONObject? = null){
        val onbData = OnboardingStatesData(state,data)
        SharedPrefUtil.getDefaultInstance().saveDataObject(SharedPrefConstants.ONBOARDING_STATE,onbData)
        _onboardingStateLiveData.postValue(onbData)
    }

    fun loginUser(email: String, password: String) {
        _userLoginLiveData.postValue(Result.InProgress())
        viewModelScope.launch {
           _userLoginLiveData.postValue(OnboardingRepository.loginUser(email,password))
        }
    }

    fun verifyUser() {
        _verifyUserLiveData.postValue(Result.InProgress())
        viewModelScope.launch {
            _verifyUserLiveData.postValue(AccountRepository.verifyUser())
        }
    }


    fun signupUser(form: HashMap<String,String>) {
        _signupUserLiveData.postValue(Result.InProgress())
        viewModelScope.launch {
            _signupUserLiveData.postValue(OnboardingRepository.signUpUser(form))
        }
    }
}