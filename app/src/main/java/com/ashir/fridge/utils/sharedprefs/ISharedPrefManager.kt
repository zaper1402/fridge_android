package com.threemusketeers.dliverCustomer.main.utils.sharedprefs

interface ISharedPrefManager {

    /**
     * Saves a [Boolean] value with the given key.
     * @param key: name of the key
     * @param value: [Boolean] value to store with the passed key
     */
    fun saveData(key: String, value: Boolean?)

    /**Saves a [Int] value with the given key.
     * @param key: name of the key
     * @param value: [Int] value to store with the passed key
     */
    fun saveData(key: String, value: Int?)

    /**
     * Saves a [Long] value with the given key.
     * @param key: name of the key
     * @param value: [Long] value to store with the passed key
     */
    fun saveData(key: String, value: String?)

    /**
     * Saves a [Long] value with the given key.
     * @param key: name of the key
     * @param value: [Long] value to store with the passed key
     */
    fun saveData(key: String, value: Long?)

    /**
     * Saves a [MutableSet] value with the given key.
     * @param key: name of the key
     * @param value: [MutableSet] value to store with the passed key
     */
    fun saveData(key: String, value: MutableSet<String>?)

    /**Returns a [Boolean] value associated with the given key.
     * @param key: nome of the key
     * @param defaultValue: default (Long) value to be returned in case no key-value exists
     * @return [Boolean) value associated with the [key] or [defaultValue]
     */
    fun getData(key: String, defaultValue: Boolean): Boolean

    /**
     * Returns a [Int] value associated with the given key.
     *@param key: name of the key
     *@param defaultValue: default [Int] value to be returned in case no key-value exists
     *@return [Int] value associated with the [key] or [defaultValue]
     */
    fun getData(key: String, defaultValue: Int): Int

    /**Returns a [String] value associated with the given key.
     *@param key: name of the key
     * @param defaultValue: default [String] value to be returned in case no key-value exists
     * @return (String) value associated with the [key] or [defaultValue]
     */
    fun getData(key: String, defaultValue: String?): String?

    /**Returns a [Long] value associated with the given key.
     *@param key: nome of the key
     *@param defaultValue: default [Long] value to be returned in case no key-value exists
     *@return (Long) value associated with the [key]
     **/
    fun getData(key: String, defaultValue: Long): Long

    /**
    Returns a [MutableSet] value associated with the given key.
     * @param key: name of the key
     * @param defaultValue: default [MutableSet] value to be returned in case no key-val
     * @return (MutableSet] value associated with the [key] or [defaultValue]
     */
    fun getData(key: String, defaultValue: MutableSet<String>?): MutableSet<String>?


    /**
    Removes the value associated with the given key.
     * @param [key]: key for which the value needs to be removed
     */
    fun removeKey(key: String)

    /**Clears oll the keys stored.*/
    fun clearAll()
    fun contains(key: String): Boolean
}