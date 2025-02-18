package com.threemusketeers.myapplication.main.utils.listeners

import android.location.Location
import com.google.android.gms.location.LocationSettingsStates

interface LocationCallbacks {

    fun onLocationSuccess(location : Location)

    fun onLocationSettingResult(locationSettingsStates : LocationSettingsStates?)
}