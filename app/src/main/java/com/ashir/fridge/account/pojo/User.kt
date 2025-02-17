package com.ashir.fridge.account.pojo

import com.google.gson.annotations.SerializedName
import java.time.LocalDate


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
    val name: String
    // Add additional fields as needed
)

data class Entry(
    val id: Int,
    val userInventoryId: Int, // Foreign key to UserProduct
    val quantity: Float,
    val expiryDate: LocalDate,
    val creationDate: LocalDate
)

data class UserProduct(
    val id: Int,
    val product: Product,
    val userId: Int,
    val entries: List<Entry> = emptyList()
) {
    val totalQuantity: Float
        get() = entries.sumOf { it.quantity.toDouble() }.toFloat()
}