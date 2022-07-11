package com.kauel.shippingmark.api.uploadImage

import android.net.Uri

data class ResponseFile(
    val file: Uri,
    val name: String,
    val fileStatus: Boolean
)
