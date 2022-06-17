package com.kauel.shippingmark.api

import com.kauel.shippingmark.api.login.Login
import com.kauel.shippingmark.api.login.ResponseLogin
import com.kauel.shippingmark.api.sendData.Data
import com.kauel.shippingmark.api.sendData.ResponseSendData
import com.kauel.shippingmark.utils.URL_DATA_PALLET
import com.kauel.shippingmark.utils.URL_LOGIN
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import java.util.*

interface ApiService {
    @POST(URL_LOGIN)
    suspend fun getLogin(
        @Body user: Login,
    ): ResponseLogin

//    @POST(URL_DATA_PALLET)
//    suspend fun getSendData(
//        @Body data: Data,
//    ): ResponseSendData

    @Multipart
    @POST(URL_DATA_PALLET)
    suspend fun getSendData(
        @Part("date") date: RequestBody,
        @Part("id_phone") idPhone: RequestBody,
        @Part("rut_operador") rutOperator: RequestBody,
        @Part("bar_code") barCode: RequestBody,
        @Part("id_transporte") idTransport: RequestBody,
        @Part("pallet_type") palletType: RequestBody,
        @Part("numero_etiquetas1") numberLabels1: RequestBody,
        @Part("numero_cajas1") numberBox1: RequestBody,
        @Part("manual1") manual1: RequestBody,
        @Part("numero_etiquetas2") numberLabels2: RequestBody,
        @Part("numero_cajas2") numberBox2: RequestBody,
        @Part("manual2") manual2: RequestBody,
        @Part("numero_etiquetas3") numberLabels3: RequestBody,
        @Part("numero_cajas3") numberBox3: RequestBody,
        @Part("manual3") manual3: RequestBody,
        @Part("numero_etiquetas4") numberLabels4: RequestBody,
        @Part("numero_cajas4") numberBox4: RequestBody,
        @Part("manual4") manual4: RequestBody,
        @Part image1: MultipartBody.Part,
        @Part image2: MultipartBody.Part,
        @Part image3: MultipartBody.Part,
        @Part image4: MultipartBody.Part,
    ): ResponseSendData
}