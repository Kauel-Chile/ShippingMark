package com.kauel.shippingmark.utils

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kauel.shippingmark.api.login.Data
import com.kauel.shippingmark.api.sendData.ResponseData
import com.kauel.shippingmark.api.uploadImage.DataImage
import java.util.*

class Converters {
    @TypeConverter
    fun restoreDate(objectToRestore: String?): Date? {
        return Gson().fromJson(objectToRestore, object : TypeToken<Date?>() {}.type)
    }

    @TypeConverter
    fun saveDate(objectToSave: Date?): String? {
        return Gson().toJson(objectToSave)
    }

    @TypeConverter
    fun restoreData(objectToRestore: String?): Data? {
        return Gson().fromJson(objectToRestore, object : TypeToken<Data?>() {}.type)
    }

    @TypeConverter
    fun saveData(objectToSave: Data?): String? {
        return Gson().toJson(objectToSave)
    }

    @TypeConverter
    fun restoreResponseData(objectToRestore: String?): ResponseData? {
        return Gson().fromJson(objectToRestore, object : TypeToken<ResponseData?>() {}.type)
    }

    @TypeConverter
    fun saveResponseData(objectToSave: ResponseData?): String? {
        return Gson().toJson(objectToSave)
    }

    @TypeConverter
    fun restoreResponseUpload(objectToRestore: String?): DataImage? {
        return Gson().fromJson(objectToRestore, object : TypeToken<DataImage?>() {}.type)
    }

    @TypeConverter
    fun saveResponseUpload(objectToSave: DataImage?): String? {
        return Gson().toJson(objectToSave)
    }

//    @TypeConverter
//    fun restoreUri(objectToRestore: String?): Uri? {
//        return Gson().fromJson(objectToRestore, object : TypeToken<Uri?>() {}.type)
//    }
//
//    @TypeConverter
//    fun saveUri(objectToSave: Uri?): String? {
//        return Gson().toJson(objectToSave)
//    }
}