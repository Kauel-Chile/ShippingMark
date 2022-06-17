package com.kauel.shippingmark.api.login

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "response_login")
data class ResponseLogin(
    @SerializedName("data")
    val data: Data,
    @SerializedName("message")
    val message: String,
    @PrimaryKey
    @SerializedName("token")
    val token: String
)