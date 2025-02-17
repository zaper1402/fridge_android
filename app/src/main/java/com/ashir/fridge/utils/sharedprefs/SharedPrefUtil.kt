package com.ashir.fridge.utils.sharedprefs


import com.ashir.fridge.FridgeApplication
import com.ashir.fridge.utils.GsonUtils
import com.threemusketeers.dliverCustomer.main.utils.sharedprefs.ISharedPrefManager
import com.threemusketeers.dliverCustomer.main.utils.sharedprefs.SharedPrefImpl
import java.util.concurrent.ConcurrentHashMap

class SharedPrefUtil private constructor(private var mSharedPrefManager: ISharedPrefManager){

    companion object {
        private const val DEFAULT_SHARED_PREF = "default_prefs"
        private const val AUTH_SHARED_PREF = "auth_prefs"
        private val mSharedPrefMap = ConcurrentHashMap<String, SharedPrefUtil>()

        private fun getInstance(sharedPrefName: String): SharedPrefUtil {
            var sharedPrefUtil = mSharedPrefMap[sharedPrefName]
            if (sharedPrefUtil == null) {
                synchronized(this) {
                    if (sharedPrefUtil == null) {
                        sharedPrefUtil = SharedPrefUtil(SharedPrefImpl(sharedPrefName, FridgeApplication.instance))
                        mSharedPrefMap[sharedPrefName] = sharedPrefUtil!!
                    }
                }
            }
            return sharedPrefUtil!!
        }

        fun getDefaultInstance()  = getInstance(DEFAULT_SHARED_PREF)

        fun getAuthInstance()  = getInstance(AUTH_SHARED_PREF)
    }

    fun saveBoolean(key: String, value: Boolean) = mSharedPrefManager.saveData(key, value)

    fun getBoolean(key: String, defaultValue: Boolean): Boolean = mSharedPrefManager.getData(key, defaultValue)

    fun saveInt(key: String, value: Int) = mSharedPrefManager.saveData(key, value)

    fun getInt(key: String, defaultValue: Int): Int = mSharedPrefManager.getData(key, defaultValue)

    fun saveLong(key: String, value: Long) = mSharedPrefManager.saveData(key, value)

    fun getLong(key: String, defaultValue: Long): Long = mSharedPrefManager.getData(key, defaultValue)

    fun saveStringSet(key: String, value: MutableSet<String>) = mSharedPrefManager.saveData(key, value)

    fun getStringSet(key: String, defaultValue: MutableSet<String>?): MutableSet<String>? = mSharedPrefManager.getData(key, defaultValue)

    fun saveString(key: String, value: String?) = mSharedPrefManager.saveData(key, value)

    fun getString(key: String, defaultValue: String? = null): String? = mSharedPrefManager.getData(key, defaultValue)

    fun remove(key: String) = mSharedPrefManager.removeKey(key)

    fun contains(key: String) = mSharedPrefManager.contains(key)

    fun <T> getDataObject(key: String, classValue: Class<T>): T? {
        val dataNullable = getString(key, null)
        dataNullable ?: return null
        return GsonUtils.fromJson(dataNullable, classValue)
    }

    /**
     * Saves a [String] value with the given key.
     * @param key: name of the key
     * @param objectValue: [String] value to store any custom Object, internally its stores as json String
     */
    fun saveDataObject(key: String, objectValue: Any?) {
        objectValue ?: return
        val value = GsonUtils.toJsonString(objectValue)
        mSharedPrefManager.saveData(key, value)
    }
}