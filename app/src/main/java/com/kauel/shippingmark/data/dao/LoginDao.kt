package com.kauel.shippingmark.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kauel.shippingmark.api.login.ResponseLogin
import kotlinx.coroutines.flow.Flow

@Dao
interface LoginDao {
    @Query("SELECT * FROM response_login")
    fun getAllResponseLogin(): Flow<ResponseLogin>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataResponseLogin(sendData: ResponseLogin)

    @Query("DELETE FROM response_login")
    suspend fun deleteAllDataResponseLogin()
}