package com.kauel.shippingmark.api.sendData

data class PalletData(
    val img_name: String,
    val number_labels: Int,
    val number_box: Int,
    val manual: Boolean
)