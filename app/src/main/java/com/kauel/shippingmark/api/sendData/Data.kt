package com.kauel.shippingmark.api.sendData

import com.google.gson.annotations.SerializedName
import com.kauel.shippingmark.ui.main.Result

data class Data(
    @SerializedName("date")
    val date: String,
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
    @SerializedName("nombre_imagen1")
    val name_image1: String,
    @SerializedName("numero_etiquetas1")
    val number_labels1: Int,
    @SerializedName("numero_cajas1")
    val number_box1: Int,
    @SerializedName("manual1")
    val manual1: Boolean,
    @SerializedName("lista_marcas1")
    val list_mark1: ArrayList<Result>,
    @SerializedName("nombre_imagen2")
    val name_image2: String,
    @SerializedName("numero_etiquetas2")
    val number_labels2: Int,
    @SerializedName("numero_cajas2")
    val number_box2: Int,
    @SerializedName("manual2")
    val manual2: Boolean,
    @SerializedName("lista_marcas2")
    val list_mark2: ArrayList<Result>,
    @SerializedName("nombre_imagen3")
    val name_image3: String,
    @SerializedName("numero_etiquetas3")
    val number_labels3: Int,
    @SerializedName("numero_cajas3")
    val number_box3: Int,
    @SerializedName("manual3")
    val manual3: Boolean,
    @SerializedName("lista_marcas3")
    val list_mark3: ArrayList<Result>,
    @SerializedName("nombre_imagen4")
    val name_image4: String,
    @SerializedName("numero_etiquetas4")
    val number_labels4: Int,
    @SerializedName("numero_cajas4")
    val number_box4: Int,
    @SerializedName("manual4")
    val manual4: Boolean,
    @SerializedName("lista_marcas4")
    val list_mark4: ArrayList<Result>,
)
