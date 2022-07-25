package com.kauel.shippingmark.api.uploadImage

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_images_uploaded")
data class DataImagesUploaded(
    val name: String,
    val status: Boolean,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)