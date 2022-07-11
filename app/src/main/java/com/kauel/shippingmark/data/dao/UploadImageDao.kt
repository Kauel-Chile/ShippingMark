package com.kauel.shippingmark.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kauel.shippingmark.api.uploadImage.ResponseUpload
import kotlinx.coroutines.flow.Flow

@Dao
interface UploadImageDao {
    @Query("SELECT * FROM response_upload_image")
    fun getAllResponseUploadImage(): Flow<ResponseUpload>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataResponseUploadImage(sendData: ResponseUpload)

    @Query("DELETE FROM response_upload_image")
    suspend fun deleteAllDataResponseUploadImage()
}