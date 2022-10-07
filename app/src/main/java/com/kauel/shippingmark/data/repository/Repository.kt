package com.kauel.shippingmark.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.room.withTransaction
import com.kauel.shippingmark.api.ApiService
import com.kauel.shippingmark.api.login.Login
import com.kauel.shippingmark.api.sendData.Data
import com.kauel.shippingmark.api.uploadImage.DataImagesUploaded
import com.kauel.shippingmark.api.uploadImage.FileName
import com.kauel.shippingmark.api.uploadImage.ResponseFile
import com.kauel.shippingmark.api.uploadImage.ResponseUpload
import com.kauel.shippingmark.data.AppDatabase
import com.kauel.shippingmark.utils.Resource
import com.kauel.shippingmark.utils.fileToMultipart
import com.kauel.shippingmark.utils.networkBoundResource
import dagger.hilt.android.qualifiers.ApplicationContext
import io.sentry.Sentry
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
    private val dataImagesUploadedDao = appDatabase.dataImagesUploaded()

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

    private var flagStop = true

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
                var position = 1
                flagStop = true

                while (position <= sizeList) {

                    val numFile = position - 1

                    if (!flagStop) break

                    if (listFile.isNotEmpty()) {

                        try {
                            val image = fileToMultipart(listFile[numFile].file)
                            val current = LocalDateTime.now()
                            val infoDate =
                                current.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                            response = apiService.sendImage(listFile[numFile].name,
                                userName,
                                infoDate,
                                image)

                            listMutable.add(ResponseFile(listFile[numFile].uri,
                                listFile[numFile].file.name,
                                response.status))

                            insertDataImagesUploaded(DataImagesUploaded(listFile[numFile].file.name, response.status))

                            emit(Resource.Loading<List<ResponseFile>>(listMutable))
                        } catch (throwable: Throwable) {
                            Sentry.captureException(throwable)
                        }

                        position++
                    } else {
                        break
                    }
                }

                emit(Resource.Success<List<ResponseFile>>(listMutable))
            } catch (throwable: Throwable) {
                Sentry.captureException(throwable)
                emit(Resource.Error<List<ResponseFile>>(throwable, listMutable))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun stopUpload() {
        flagStop = false
    }

    suspend fun insertDataImagesUploaded(data: DataImagesUploaded) {
        appDatabase.withTransaction {
            dataImagesUploadedDao.insert(data)
        }
    }
}