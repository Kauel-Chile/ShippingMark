package com.kauel.shippingmark.data.repository

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.withTransaction
import com.kauel.shippingmark.api.ApiService
import com.kauel.shippingmark.api.login.Login
import com.kauel.shippingmark.api.sendData.Data
import com.kauel.shippingmark.api.uploadImage.FileName
import com.kauel.shippingmark.api.uploadImage.ResponseFile
import com.kauel.shippingmark.api.uploadImage.ResponseUpload
import com.kauel.shippingmark.data.AppDatabase
import com.kauel.shippingmark.utils.Resource
import com.kauel.shippingmark.utils.fileToMultipart
import com.kauel.shippingmark.utils.networkBoundResource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDateTime
import javax.inject.Inject

class Repository @Inject constructor(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase,
    @ApplicationContext val appContext: Context,
) {

    private val sendDataDao = appDatabase.sendDataDao()
    private val loginDao = appDatabase.loginDao()
    private val uploadImageDao = appDatabase.uploadImage()

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

    fun sendData(
        data: Data,
    ) = networkBoundResource(
        databaseQuery = {
            sendDataDao.getAllResponseSendData()
        },
        networkCall = {
            apiService.sendData(data)
        },
        saveCallResult = {
            appDatabase.withTransaction {
                sendDataDao.deleteAllDataResponseSendData()
                sendDataDao.insertDataResponseSendData(it)
            }
        }
    )

    fun uploadImage(
        name_image: RequestBody,
        userName: RequestBody,
        infoDate: RequestBody,
        image: MultipartBody.Part,
    ) = networkBoundResource(
        databaseQuery = {
            uploadImageDao.getAllResponseUploadImage()
        },
        networkCall = {
            apiService.sendImage(name_image, userName, infoDate, image)
        },
        saveCallResult = {
            appDatabase.withTransaction {
                uploadImageDao.deleteAllDataResponseUploadImage()
                uploadImageDao.insertDataResponseUploadImage(it)
            }
        }
    )

    private var flagStop = false
    private var flagPause = false

    @SuppressLint("NewApi")
    fun uploadImageCustom(
        listFile: List<FileName>,
        userName: RequestBody,
    ): Flow<Resource<List<ResponseFile>>> {
        return flow {
            emit(Resource.Loading<List<ResponseFile>>())
            val listMutable = mutableListOf<ResponseFile>()
            try {
                var response: ResponseUpload

                val sizeList = listFile.size
                var flagListFile = listFile
                var position = 1

                while (position <= sizeList) {

                    if (flagStop) break

                    if (flagListFile.isNotEmpty()) {
                        val image = fileToMultipart(flagListFile[0].file)
                        val current = LocalDateTime.now()
                        val infoDate =
                            current.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                        response =
                            apiService.sendImage(flagListFile[0].name, userName, infoDate, image)

                        listMutable.add(ResponseFile(flagListFile[0].uri,
                            flagListFile[0].file.name,
                            response.status))
                        flagListFile = flagListFile.drop(1)

                        emit(Resource.Loading<List<ResponseFile>>(listMutable))
                        position++
                    } else {
                        break
                    }
                }

                emit(Resource.Success<List<ResponseFile>>(listMutable))
            } catch (throwable: Throwable) {
                emit(Resource.Error<List<ResponseFile>>(throwable, listMutable))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun stopUpload() {
        flagStop = true
    }

    fun pauseUpload() {
        flagPause = true
    }
}