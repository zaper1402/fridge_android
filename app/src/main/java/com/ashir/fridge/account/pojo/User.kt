package com.ashir.fridge.account.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class User(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("date_of_birth")
    val dateOfBirth: String?,
    val gender: String?,
    val photo: String?, // path or URL
    val preferences: List<String>?
)

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val tags: List<String>,
    val allergyTags: AllergyTags,
    @SerializedName("standard_expiry_days")
    val standardExpiryDays: Int,
    @SerializedName("photo")
    val totalQuantity: Long
    // Add additional fields as needed
) : Serializable
