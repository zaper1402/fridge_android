package com.threemusketeers.dliverCustomer.main.utils.sharedprefs

import android.content.Context
import android.content.SharedPreferences

class SharedPrefImpl(sharedPrefFile: String,context: Context) : ISharedPrefManager{
    private val mSharedPreferences: SharedPreferences? = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
    override fun saveData(key: String, value: Boolean?) {
        value ?: return
        mSharedPreferences?.edit()?.putBoolean(key, value)?.apply()
    }

    override fun saveData(key: String, value: Int?) {
        value ?: return
        mSharedPreferences?.edit()?.putInt(key, value)?.apply()
    }

    override fun saveData(key: String, value: String?) {
        value ?: return
        mSharedPreferences?.edit()?.putString(key, value)?.apply()
    }

    override fun saveData(key: String, value: Long?) {
        value ?: return
        mSharedPreferences?.edit()?.putLong(key, value)?.apply()
    }

    override fun saveData(key: String, value: MutableSet<String>?) {
        value ?: return
        mSharedPreferences?.edit()?.putStringSet(key, value)?.apply()
    }

    override fun getData(key: String, defaultValue: Boolean): Boolean {
        return mSharedPreferences?.getBoolean(key, defaultValue) ?: defaultValue
    }

    override fun getData(key: String, defaultValue: Int): Int {
        return mSharedPreferences?.getInt(key, defaultValue) ?: defaultValue
    }

    override fun getData(key: String, defaultValue: String?): String? {
        return mSharedPreferences?.getString(key, defaultValue) ?: defaultValue
    }

    override fun getData(key: String, defaultValue: Long): Long {
        return mSharedPreferences?.getLong(key, defaultValue) ?: defaultValue
    }

    override fun getData(key: String, defaultValue: MutableSet<String>?): MutableSet<String>? {
        return mSharedPreferences?.getStringSet(key, defaultValue) ?: defaultValue
    }

    override fun removeKey(key: String) {
        mSharedPreferences?.edit()?.remove(key)?.apply()
    }

    override fun clearAll() {
        mSharedPreferences?.edit()?.clear()?.apply()
    }

    override fun contains(key: String): Boolean {
        return mSharedPreferences?.contains(key) ?: false
    }
}