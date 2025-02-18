package com.ashir.fridge.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ashir.fridge.account.AccountRepository
import com.ashir.fridge.account.pojo.User
import com.ashir.fridge.http.Result
import kotlinx.coroutines.launch

class MainActivityViewModel : BaseViewModel() {

    val getSelfUserLiveData : LiveData<Result<User>> get() = _getSelfUserLiveData
    private val _getSelfUserLiveData = MutableLiveData<Result<User>>()

    fun getSelfUser() {
        _getSelfUserLiveData.postValue(Result.InProgress())
        viewModelScope.launch {
            _getSelfUserLiveData.postValue(AccountRepository.getSelfUser())
        }
    }
}