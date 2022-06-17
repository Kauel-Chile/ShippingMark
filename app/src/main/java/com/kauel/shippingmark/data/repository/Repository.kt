package com.kauel.shippingmark.data.repository

import android.content.Context
import androidx.room.withTransaction
import com.kauel.shippingmark.api.ApiService
import com.kauel.shippingmark.api.login.Login
import com.kauel.shippingmark.api.sendData.Data
import com.kauel.shippingmark.data.AppDatabase
import com.kauel.shippingmark.utils.networkBoundResource
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import javax.inject.Inject

class Repository @Inject constructor(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase,
    @ApplicationContext val appContext: Context,
) {

    private val sendDataDao = appDatabase.sendDataDao()
    private val loginDao = appDatabase.loginDao()

    fun login(user: Login) = networkBoundResource(
        databaseQuery = {
            loginDao.getAllResponseLogin()
        },
        networkCall = {
            apiService.getLogin(user)
        },
        saveCallResult = {
            appDatabase.withTransaction {
                loginDao.deleteAllDataResponseLogin()
                loginDao.insertDataResponseLogin(it)
            }
        }
    )

    //    fun sendData(
//        data: Data,
//    ) = networkBoundResource(
//        databaseQuery = {
//            sendDataDao.getAllResponseSendData()
//        },
//        networkCall = {
//            apiService.getSendData(data)
//        },
//        saveCallResult = {
//            appDatabase.withTransaction {
//                sendDataDao.deleteAllDataResponseSendData()
//                sendDataDao.insertDataResponseSendData(it)
//            }
//        }
//    )
    fun sendData(
        date: RequestBody,
        idPhone: RequestBody,
        rutOperator: RequestBody,
        barCode: RequestBody,
        idTransport: RequestBody,
        palletType: RequestBody,
        numberLabels1: RequestBody,
        numberBox1: RequestBody,
        manual1: RequestBody,
        numberLabels2: RequestBody,
        numberBox2: RequestBody,
        manual2: RequestBody,
        numberLabels3: RequestBody,
        numberBox3: RequestBody,
        manual3: RequestBody,
        numberLabels4: RequestBody,
        numberBox4: RequestBody,
        manual4: RequestBody,
        image1: MultipartBody.Part,
        image2: MultipartBody.Part,
        image3: MultipartBody.Part,
        image4: MultipartBody.Part,
    ) = networkBoundResource(
        databaseQuery = {
            sendDataDao.getAllResponseSendData()
        },
        networkCall = {
            apiService.getSendData(
                date,
                idPhone,
                rutOperator,
                barCode,
                idTransport,
                palletType,
                numberLabels1,
                numberBox1,
                manual1,
                numberLabels2,
                numberBox2,
                manual2,
                numberLabels3,
                numberBox3,
                manual3,
                numberLabels4,
                numberBox4,
                manual4,
                image1,
                image2,
                image3,
                image4)
        },
        saveCallResult = {
            appDatabase.withTransaction {
                sendDataDao.deleteAllDataResponseSendData()
                sendDataDao.insertDataResponseSendData(it)
            }
        }
    )
}