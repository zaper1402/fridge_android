package com.ashir.fridge.utils.managers

import android.annotation.SuppressLint
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.WorkerThread
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.threemusketeers.dliverCustomer.main.utils.extensions.getAppContext
import com.ashir.fridge.utils.sharedprefs.SharedPrefUtil
import java.util.UUID

object AppDeviceManager {
    private const val TAG = "HikeDeviceManager"
    const val DEVICE_ID_KEY = "DEVICE_ID_KEY_INFO"
    const val SECURE_ID_KEY = "SECURE_ID_KEY_INFO"
    const val DEVICE_ID_GENERATED_SOURCE = "DEVICE_ID_GENERATED_SOURCE"
    @Volatile private var googleAdvertiserId: String? = null
    @Volatile private var secureId: String? = null

    @WorkerThread
    fun init() {
        val deviceIdKey = SharedPrefUtil.getDefaultInstance().getString(DEVICE_ID_KEY, "")

        if(deviceIdKey?.isNotBlank() == true) {
            googleAdvertiserId = deviceIdKey
            return
        }
        if (deviceIdKey?.isBlank() == true) {
            fetchDeviceIdFromSystem()
        }
    }

    @JvmStatic
    fun getDeviceId(isFromUpdateCall: Boolean = false): String {  // returns Google Advertiser Id
        if (isFromUpdateCall) {
            fetchDeviceIdFromSystem(isFromUpdateCall)
        } else if(googleAdvertiserId.isNullOrEmpty()){
            googleAdvertiserId = SharedPrefUtil.getDefaultInstance().getString(DEVICE_ID_KEY, "")
        }
        Log.i(TAG,"returning deviceId as $googleAdvertiserId")
        return googleAdvertiserId!!
    }

    fun onLaunchInitSecureIdIfRequired(){
        val secureIdKey = SharedPrefUtil.getDefaultInstance().getString(SECURE_ID_KEY, "")
        if(secureIdKey?.isNotEmpty() == true) {
            secureId = secureIdKey
        } else {
            fetchSecureIdFromSystem()
        }
    }

    @JvmStatic
    fun getSecureId(isFromUpdateCall: Boolean = false): String {  // returns Android Secure Id
        if (isFromUpdateCall) {
            fetchSecureIdFromSystem(isFromUpdateCall)
        } else if(secureId.isNullOrEmpty()){
            secureId = SharedPrefUtil.getDefaultInstance().getString(SECURE_ID_KEY, "")
        }
        Log.i(TAG,"returning secureId as $secureId")
        return secureId!!
    }

    private fun fetchDeviceIdFromSystem(isFromUpdateCall: Boolean = false) {
        try {
            googleAdvertiserId = AdvertisingIdClient.getAdvertisingIdInfo(getAppContext()).id
            Log.i(TAG, "got $googleAdvertiserId value from google api call.")
            SharedPrefUtil.getDefaultInstance().saveString(DEVICE_ID_GENERATED_SOURCE, "GOOGLE_ADVERTISER_ID") // Used just for Debugging
        } catch (e: Throwable) {  // when googleplayservices doesn't exist this can throw exception
            if (isFromUpdateCall) {
                googleAdvertiserId = SharedPrefUtil.getDefaultInstance().getString(DEVICE_ID_KEY, "")
                Log.i(TAG, "got exception ${e.message}, so keeping previously generated id: $googleAdvertiserId for Google Advertiser Id.")
            } else {
                googleAdvertiserId = UUID.randomUUID().toString()
                Log.i(TAG, "got exception ${e.message}, so keeping uuid value as $googleAdvertiserId for Google Advertiser Id.")
                SharedPrefUtil.getDefaultInstance().saveString(DEVICE_ID_GENERATED_SOURCE, "UUID")
            }
        }
        SharedPrefUtil.getDefaultInstance().saveString(DEVICE_ID_KEY, googleAdvertiserId)
    }

    @SuppressLint("HardwareIds")
    private fun fetchSecureIdFromSystem(isFromUpdateCall: Boolean = false) {
        try {
            secureId = Settings.Secure.getString(getAppContext().contentResolver, Settings.Secure.ANDROID_ID)
            Log.i(TAG, "got $secureId value for Android Secure Id")
        } catch (e: Throwable) {  // when googleplayservices doesn't exist this can throw exception
            if (isFromUpdateCall) {
                secureId = SharedPrefUtil.getDefaultInstance().getString(SECURE_ID_KEY, "")
                Log.i(TAG, "got exception ${e.message}, so keeping previously generated id: $secureId for Android Secure Id.")
            } else {
                secureId = UUID.randomUUID().toString()
                Log.i(TAG, "got exception ${e.message}, so keeping uuid value as $secureId for Android Secure Id.")
            }
        }
        SharedPrefUtil.getDefaultInstance().saveString(SECURE_ID_KEY, secureId)
    }

    fun isUsingParallelSpace(): Boolean { //sample parallel space path - /data/user/0/com.excean.parallelspace/gameplugins/com.hike.mazuma.debug
        try {
            val dataDir = getAppContext().applicationInfo?.dataDir
            val packageStartIndex = dataDir?.indexOf(getAppContext().packageName)
            if (packageStartIndex != null && packageStartIndex >= 0) {
                val remainingPath = dataDir.substring(0, packageStartIndex)
                if (remainingPath.contains(".")) {
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun isUsingGuestMode(): Boolean { //sample guest mode path - /data/user/10/com.hike.mazuma.debug
        try {
            val dataDir = getAppContext().applicationInfo?.dataDir
            val packageStartIndex = dataDir?.indexOf(getAppContext().packageName)
            if (packageStartIndex != null && packageStartIndex >= 0) {
                val remainingPath = dataDir.substring(0, packageStartIndex)
                if (remainingPath.contains("/0/").not()) {
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun isEmulator() = (
            (Build.MANUFACTURER == "Google" && Build.BRAND == "google"
                    && ((Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                            && Build.FINGERPRINT.endsWith(":user/release-keys")
                            && Build.PRODUCT.startsWith("sdk_gphone_")
                            && Build.MODEL.startsWith("sdk_gphone_")
                        )
                    || (Build.FINGERPRINT.startsWith("google/sdk_gphone64_")
                            && (Build.FINGERPRINT.endsWith(":userdebug/dev-keys") || Build.FINGERPRINT.endsWith(":user/release-keys"))
                            && Build.PRODUCT.startsWith("sdk_gphone64_")
                            && Build.MODEL.startsWith("sdk_gphone64_")
                        )
                    )
            )
            || Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            //bluestacks
            || "QC_Reference_Phone" == Build.BOARD && !"Xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.HOST.startsWith("Build")
            //MSI App Player
            || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
            || Build.PRODUCT == "google_sdk"
            )
}