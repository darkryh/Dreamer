package com.ead.project.dreamer.data.utils

import androidx.room.TypeConverter
import com.ead.project.dreamer.app.data.util.system.deserialize
import com.ead.project.dreamer.data.database.model.Chapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Converters {

    private val gson : Gson = Gson()
    @TypeConverter
    fun listStringToJson(value: List<String>): String = gson.toJson(value)

    @TypeConverter
    fun jsonStringToList(listOfString: String?): MutableList<String?>? = gson
        .fromJson(listOfString, object : TypeToken<List<String?>?>() {}.type)

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun chapterToJson(value: Chapter?): String = gson.toJson(value)

    @TypeConverter
    fun jsonStringToChapter(value: String?): Chapter? = gson.deserialize(value)
}