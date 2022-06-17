package com.kauel.shippingmark.api.sendData

data class ResponseData(
    val __v: Int,
    val _id: String,
    val bar_code: String,
    val createdAt: String,
    val date: String,
    val id_phone: String,
    val id_transporte: String,
    val imagesUrl: List<Any>,
    val pallet_type: String,
    val rut_operador: String,
    val updatedAt: String
)