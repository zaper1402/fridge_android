package com.ashir.fridge.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.profile.pojo.LogoutData
import com.ashir.fridge.ui.profile.repository.ProfileRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    val logoutLiveData : MutableLiveData<Result<LogoutData>> get() = _logoutLiveData
    private val _logoutLiveData = MutableLiveData<Result<LogoutData>>()

    fun logout() {
        _logoutLiveData.postValue(Result.InProgress())
        viewModelScope.launch {
            _logoutLiveData.postValue(ProfileRepository.logoutUser())
        }
    }
}