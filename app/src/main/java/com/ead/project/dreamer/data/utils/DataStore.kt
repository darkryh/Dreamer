package com.ead.project.dreamer.data.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.ead.project.dreamer.app.DreamerApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException

const val API_SETTINGS_FILE = "API_SETTINGS_FILE"
val Context.datastore : DataStore<Preferences> by preferencesDataStore(API_SETTINGS_FILE)
class DataStore {

    companion object {

        fun writeStringAsync(stringKey :String, value : String?) {
            CoroutineScope(Dispatchers.IO).launch {
                val dataStoreKey = stringPreferencesKey(stringKey)
                DreamerApp.INSTANCE.datastore.edit { settings ->
                    settings[dataStoreKey] = value?:"null"
                }
            }
        }

        fun writeIntAsync(stringKey :String, value : Int) {
            CoroutineScope(Dispatchers.IO).launch {
                val dataStoreKey = intPreferencesKey(stringKey)
                DreamerApp.INSTANCE.datastore.edit { settings ->
                    settings[dataStoreKey] = value
                }
            }
        }

        fun writeDoubleAsync(stringKey :String, value : Double) {
            CoroutineScope(Dispatchers.IO).launch {
                val dataStoreKey = doublePreferencesKey(stringKey)
                DreamerApp.INSTANCE.datastore.edit { settings ->
                    settings[dataStoreKey] = value
                }
            }
        }

        fun writeBooleanAsync(stringKey :String, value : Boolean) {
            CoroutineScope(Dispatchers.IO).launch {
                val dataStoreKey = booleanPreferencesKey(stringKey)
                DreamerApp.INSTANCE.datastore.edit { settings ->
                    settings[dataStoreKey] = value
                }
            }
        }

        private suspend fun writeSuspendString(stringKey :String, value : String?) {
            val dataStoreKey = stringPreferencesKey(stringKey)
            DreamerApp.INSTANCE.datastore.edit { settings ->
                settings[dataStoreKey] = value?:"null"
            }
        }

        private suspend fun writeSuspendInt(stringKey :String, value : Int) {
            val dataStoreKey = intPreferencesKey(stringKey)
            DreamerApp.INSTANCE.datastore.edit { settings ->
                settings[dataStoreKey] = value
            }
        }

        private suspend fun writeSuspendDouble(stringKey :String, value : Double) {
            val dataStoreKey = doublePreferencesKey(stringKey)
            DreamerApp.INSTANCE.datastore.edit { settings ->
                settings[dataStoreKey] = value
            }
        }

        private suspend fun writeSuspendBoolean(stringKey :String, value : Boolean) {
            val dataStoreKey = booleanPreferencesKey(stringKey)
            DreamerApp.INSTANCE.datastore.edit { settings ->
                settings[dataStoreKey] = value
            }
        }

        fun writeString(stringKey :String, value : String?) = runBlocking {
            writeSuspendString(stringKey, value)
        }

        fun writeInt(stringKey :String, value : Int) = runBlocking {
            writeSuspendInt(stringKey, value)
        }

        fun writeDouble(stringKey :String, value : Double) = runBlocking {
            writeSuspendDouble(stringKey, value)
        }

        fun writeBoolean(stringKey :String, value : Boolean) = runBlocking {
            writeSuspendBoolean(stringKey, value)
        }

        suspend fun readStringAsync(stringKey: String,string: String? = null) : String? {
            val dataStoreKey = stringPreferencesKey(stringKey)
            val preference = DreamerApp.INSTANCE.datastore.data.first()
            return preference[dataStoreKey]
        }

        suspend fun readIntAsync(stringKey: String,value: Int = 0) : Int {
            val dataStoreKey = intPreferencesKey(stringKey)
            val preference = DreamerApp.INSTANCE.datastore.data.first()
            return preference[dataStoreKey]?: value
        }

        suspend fun readDoubleAsync(stringKey: String,value: Double = 0.0) : Double {
            val dataStoreKey = doublePreferencesKey(stringKey)
            val preference = DreamerApp.INSTANCE.datastore.data.first()
            return preference[dataStoreKey]?: value
        }

        suspend fun readBooleanAsync(stringKey :String, value : Boolean = false) : Boolean {
            val dataStoreKey = booleanPreferencesKey(stringKey)
            val preference = DreamerApp.INSTANCE.datastore.data.first()
            return preference[dataStoreKey]?: value
        }

        fun readString(stringKey :String, value : String? = null) : String = runBlocking {
            readStringAsync(stringKey, value)?:"null"
        }

        fun readInt(stringKey :String, value : Int = 0) : Int = runBlocking {
            readIntAsync(stringKey, value)
        }

        fun readDouble(stringKey :String, value : Double = 0.0) : Double = runBlocking {
            readDoubleAsync(stringKey, value)
        }

        fun readBoolean(stringKey :String, value : Boolean = false) : Boolean = runBlocking {
            readBooleanAsync(stringKey, value)
        }

        fun flowString(stringKey: String) : Flow<String?> {
            val dataStoreKey = stringPreferencesKey(stringKey)
            val preference = DreamerApp.INSTANCE.datastore.data.map {
                it[dataStoreKey] }.catch { exception ->
                if (exception is IOException) {
                    emit(null)
                } else {
                    throw exception
                }
            }
            return preference
        }

        fun flowInt(stringKey: String) : Flow<Int> {
            val dataStoreKey = intPreferencesKey(stringKey)
            val preference = DreamerApp.INSTANCE.datastore.data.map {
                it[dataStoreKey]?:0 }.catch { exception ->
                if (exception is IOException) {
                    emit(0)
                } else {
                    throw exception
                }
            }
            return preference
        }

        fun flowDouble(stringKey: String) : Flow<Double> {
            val dataStoreKey = doublePreferencesKey(stringKey)
            val preference = DreamerApp.INSTANCE.datastore.data.map {
                it[dataStoreKey]?:0.0 }.catch { exception ->
                if (exception is IOException) {
                    emit(0.0)
                } else {
                    throw exception
                }
            }
            return preference
        }

        fun flowBoolean(stringKey: String) : Flow<Boolean> {
            val dataStoreKey = booleanPreferencesKey(stringKey)
            val preference = DreamerApp.INSTANCE.datastore.data.map {
                it[dataStoreKey]?:false }.catch { exception ->
                if (exception is IOException) {
                    emit(false)
                } else {
                    throw exception
                }
            }
            return preference
        }
    }
}