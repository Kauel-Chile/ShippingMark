package com.kauel.shippingmark.api

import com.kauel.shippingmark.api.login.Login
import com.kauel.shippingmark.api.login.ResponseLogin
import com.kauel.shippingmark.api.sendData.Data
import com.kauel.shippingmark.api.sendData.ResponseSendData
import com.kauel.shippingmark.api.uploadImage.ResponseUpload
import com.kauel.shippingmark.utils.URL_DATA_PALLET
import com.kauel.shippingmark.utils.URL_IMAGE
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

    @POST(URL_DATA_PALLET)
    suspend fun sendData(
        @Body data: Data,
    ): ResponseSendData

    @Multipart
    @POST(URL_IMAGE)
    suspend fun sendImage(
        @Part("nombre_imagen") nameImage: RequestBody,
        @Part("userName") userName: RequestBody,
        @Part("infoDate") infoDate: RequestBody,
        @Part image: MultipartBody.Part,
    ): ResponseUpload
}