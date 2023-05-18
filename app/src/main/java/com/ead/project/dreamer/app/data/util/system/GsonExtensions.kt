package com.ead.project.dreamer.app.data.util.system

import com.google.gson.Gson
import java.lang.Exception

inline fun <reified T>Gson.deserialize(json : String) : T? {
    return try {
        fromJson(json,T::class.java)
    } catch (e : Exception) {
        e.printStackTrace()
        null
    }
}