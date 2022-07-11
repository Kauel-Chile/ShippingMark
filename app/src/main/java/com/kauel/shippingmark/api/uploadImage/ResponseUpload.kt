package com.kauel.shippingmark.api.uploadImage

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "response_upload_image")
data class ResponseUpload (
    @SerializedName("message")
    @PrimaryKey
    val message: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("data")
    val data: DataImage
)