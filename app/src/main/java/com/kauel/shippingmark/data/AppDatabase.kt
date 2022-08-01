package com.kauel.shippingmark.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kauel.shippingmark.api.login.ResponseLogin
import com.kauel.shippingmark.api.uploadImage.DataImagesUploaded
import com.kauel.shippingmark.api.sendData.ResponseSendData
import com.kauel.shippingmark.api.uploadImage.ResponseUpload
import com.kauel.shippingmark.data.dao.DataImagesUploadedDao
import com.kauel.shippingmark.data.dao.LoginDao
import com.kauel.shippingmark.data.dao.SendDataDao
import com.kauel.shippingmark.data.dao.UploadImageDao
import com.kauel.shippingmark.utils.Converters

@Database(
    entities = [ResponseLogin::class,
               ResponseSendData::class,
               ResponseUpload::class,
               DataImagesUploaded::class,],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun loginDao(): LoginDao

    abstract fun sendDataDao(): SendDataDao

    abstract fun uploadImage() : UploadImageDao

    abstract fun dataImagesUploaded() : DataImagesUploadedDao
}
