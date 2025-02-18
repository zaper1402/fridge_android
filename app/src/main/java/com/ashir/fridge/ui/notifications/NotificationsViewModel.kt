package com.ashir.fridge.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.notifications.pojo.NotifData
import com.ashir.fridge.ui.notifications.repository.NotificationRepository
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {

    val notifLiveData: LiveData<Result<NotifData>>
        get() = _notifLiveData
    private val _notifLiveData = MutableLiveData<Result<NotifData>>()


    fun getNotifData() {
        _notifLiveData.postValue(Result.InProgress())
        viewModelScope.launch {
            _notifLiveData.postValue(NotificationRepository.getNotifData())
        }
    }
}