package com.kauel.shippingmark.api.sendData

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "response_send_data")
data class ResponseSendData(
    @SerializedName("data")
    val data: ResponseData,
    @SerializedName("filesResponse")
    val filesResponse: String,
    @SerializedName("message")
    @PrimaryKey
    val message: String
)