package com.ashir.fridge.ui.home.pojo

import com.ashir.fridge.account.pojo.Product
import com.ashir.fridge.utils.IModel
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.LocalDate

data class ProductListData(
    val products : List<Product>
)


data class Entry(
    val id: Int,
    val userInventoryId: Int, // Foreign key to UserProduct
    val quantity: Float,
    val expiryDate: LocalDate,
    val creationDate: LocalDate
)

data class UserProductData(
    val inventory: List<UserProduct>
)

data class UserProduct(
    val id: Int,
    val product: Product,
    val brand: String?,
    @SerializedName("quantity_type")
    var quantityType: String,
    var quantity: Float,
    val name: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("expiry_date")
    val expiryDate: String,

    @Transient
    var currentQuantity: Float?,
): IModel , Serializable
