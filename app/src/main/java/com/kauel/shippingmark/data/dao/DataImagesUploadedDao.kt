package com.kauel.shippingmark.data.dao

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.room.*
import com.kauel.shippingmark.api.uploadImage.DataImagesUploaded
import kotlinx.coroutines.flow.Flow

@Dao
interface DataImagesUploadedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: DataImagesUploaded)

    @Update
    suspend fun update(data: DataImagesUploaded)

    @Query("SELECT * FROM data_images_uploaded WHERE status = 1")
    fun getAllDataImagesUploaded(): Flow<List<DataImagesUploaded>>

    @Query("SELECT * FROM data_images_uploaded WHERE name like :name LIMIT 1")
    fun getDataImagesUploaded(name: String): LiveData<DataImagesUploaded>

    @Query("SELECT * FROM data_images_uploaded WHERE status = :status")
    fun getAllDataImagesUploadedStatus(status: Boolean): Flow<List<DataImagesUploaded>>

    @Query("DELETE FROM data_images_uploaded")
    suspend fun deleteAllDataImagesUploaded()

    @Query("DELETE FROM data_images_uploaded WHERE id = :id")
    suspend fun deleteDataImagesUploaded(id: Int)
}