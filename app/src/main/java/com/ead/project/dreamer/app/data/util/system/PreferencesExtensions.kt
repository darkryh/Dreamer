package com.ead.project.dreamer.app.data.util.system

import com.ead.project.dreamer.app.data.preference.Preferences


suspend fun <T>Preferences.setGeneric(key : String,value : T) {
    when(value) {
        is Boolean -> set(key,value)
        is String -> set(key,value)
        is Int -> set(key,value)
        is Float -> set(key,value)
        is Double -> set(key,value)
    }
}