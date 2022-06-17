package com.kauel.shippingmark.api.sendData

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*

data class Data(
    @SerializedName("date")
    val date: Date,
    @SerializedName("id_phone")
    val id_phone: String,
    @SerializedName("rut_operador")
    val rut_operador: String,
    @SerializedName("bar_code")
    val bar_code: String,
    @SerializedName("id_transporte")
    val id_transporte: String,
    @SerializedName("pallet_type")
    val pallet_type: String,
    @SerializedName("image1")
    val image1: MultipartBody.Part,
    @SerializedName("numero_etiquetas1")
    val number_labels1: Int,
    @SerializedName("numero_cajas1")
    val number_box1: Int,
    @SerializedName("manual1")
    val manual1: Boolean,
    @SerializedName("image2")
    val image2: MultipartBody.Part,
    @SerializedName("numero_etiquetas2")
    val number_labels2: Int,
    @SerializedName("numero_cajas2")
    val number_box2: Int,
    @SerializedName("manual2")
    val manual2: Boolean,
    @SerializedName("image3")
    val image3: MultipartBody.Part,
    @SerializedName("numero_etiquetas3")
    val number_labels3: Int,
    @SerializedName("numero_cajas3")
    val number_box3: Int,
    @SerializedName("manual3")
    val manual3: Boolean,
    @SerializedName("image4")
    val image4: MultipartBody.Part,
    @SerializedName("numero_etiquetas4")
    val number_labels4: Int,
    @SerializedName("numero_cajas4")
    val number_box4: Int,
    @SerializedName("manual4")
    val manual4: Boolean,
)
