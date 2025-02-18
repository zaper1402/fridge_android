package com.ashir.fridge.ui.notifications.pojo

import com.ashir.fridge.utils.IModel
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NotifData(
    @SerializedName("notifs")
    val notifItems: List<NotifItem>
): IModel, Serializable

data class NotifItem(
    val id: Int,
    val name: String,
    val message: String,
    @SerializedName("expiry_date")
    val expiryDate : String,
) : IModel, Serializable
