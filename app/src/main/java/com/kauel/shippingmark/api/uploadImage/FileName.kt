package com.kauel.shippingmark.api.uploadImage

import android.net.Uri
import okhttp3.RequestBody
import java.io.File

data class FileName(
    val file: File,
    val uri: Uri,
    val name: RequestBody,
)
