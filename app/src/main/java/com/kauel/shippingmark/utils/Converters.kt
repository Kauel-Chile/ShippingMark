package com.kauel.shippingmark.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kauel.shippingmark.api.login.Data
import com.kauel.shippingmark.api.sendData.ResponseData
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
}