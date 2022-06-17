package com.kauel.shippingmark.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kauel.shippingmark.api.sendData.ResponseSendData
import kotlinx.coroutines.flow.Flow

@Dao
interface SendDataDao {
    @Query("SELECT * FROM response_send_data")
    fun getAllResponseSendData(): Flow<ResponseSendData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataResponseSendData(sendData: ResponseSendData)

    @Query("DELETE FROM response_send_data")
    suspend fun deleteAllDataResponseSendData()
}