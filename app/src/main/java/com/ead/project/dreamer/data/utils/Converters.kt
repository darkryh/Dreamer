package com.ead.project.dreamer.data.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Converters {

    @TypeConverter
    fun listStringToJson(value: List<String>): String = Gson().toJson(value)

    @TypeConverter
    fun jsonStringToList(listOfString: String?): MutableList<String?>? = Gson()
        .fromJson(listOfString, object : TypeToken<List<String?>?>() {}.type)

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}