package com.ead.project.dreamer.app.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val API_SETTINGS_FILE = "API_SETTINGS_FILE"
private val Context.store : DataStore<Preferences> by preferencesDataStore(name = API_SETTINGS_FILE)
@Singleton
class Preferences @Inject constructor(
    private val context: Context
) {

    suspend fun set(preference: String, value : Boolean = false) {
        context.store.edit { settings ->
            settings[getPreferenceBoolean(preference)] = value
        }
    }

    suspend fun set(preference: String, value : String?) {
        context.store.edit { settings ->
            settings[getPreferenceString(preference)] = value?:"null"
        }
    }

    suspend fun set(preference: String, value : Int?) {
        context.store.edit { settings ->
            settings[getPreferenceInt(preference)] = value?:-1
        }
    }

    suspend fun set(preference: String, value : Long?) {
        context.store.edit { settings ->
            settings[getPreferenceLong(preference)] = value?:-1
        }
    }

    suspend fun set(preference: String, value : Float?) {
        context.store.edit { settings ->
            settings[getPreferenceFloat(preference)] = value?:-1f
        }
    }

    suspend fun set(preference: String, value : Double?) {
        context.store.edit { settings ->
            settings[getPreferenceDouble(preference)] = value?:-1.0
        }
    }

    suspend fun getString(preference: String,value: String = "null") : String {
        val setting = context.store.data.first()
        return setting[getPreferenceString(preference)]?:value
    }

    suspend fun getInt(preference: String,value: Int = 0) : Int {
        val setting = context.store.data.first()
        return setting[getPreferenceInt(preference)]?: value
    }

    suspend fun getDouble(preference: String,value: Double = 0.0) : Double {
        val setting = context.store.data.first()
        return setting[getPreferenceDouble(preference)]?: value
    }

    suspend fun getBoolean(preference :String, value : Boolean = false) : Boolean {
        val setting = context.store.data.first()
        return setting[getPreferenceBoolean(preference)]?: value
    }

    fun getStringFlow(stringKey: String) : Flow<String?> {
        return context.store
            .data
            .mappingNullableFlow(
                preferenceKey =  getPreferenceString(stringKey),
                defaultValue = null
            )
    }

    fun getIntFlow(preference: String,defaultValue : Int = -1) : Flow<Int> {
        return context.store
            .data
            .mappingFlow(
                preferenceKey =  getPreferenceInt(preference),
                defaultValue = defaultValue
            )
    }

    fun getLongFlow(preference: String,defaultValue: Long = -1) : Flow<Long> {
        return context.store
            .data
            .mappingFlow(
                preferenceKey =  getPreferenceLong(preference),
                defaultValue = defaultValue
            )
    }

    fun getFloatFlow(preference: String, defaultValue: Float = -1f) : Flow<Float> {
        return context.store
            .data
            .mappingFlow(
                preferenceKey =  getPreferenceFloat(preference),
                defaultValue = defaultValue
            )
    }

    fun getDoubleFlow(preference: String, defaultValue: Double = -1.0) : Flow<Double> {
        return context.store
            .data
            .mappingFlow(
                preferenceKey =  getPreferenceDouble(preference),
                defaultValue = defaultValue
            )
    }

    fun getBooleanFlow(preference: String,defaultValue : Boolean = false): Flow<Boolean> {
        return context.store
            .data
            .mappingFlow(
                preferenceKey =  getPreferenceBoolean(preference),
                defaultValue = defaultValue
            )
    }

    private fun<T> Flow<Preferences>.mappingFlow(preferenceKey: Preferences.Key<T>, defaultValue : T) : Flow<T> {
        return map { preferences ->
            preferences[preferenceKey]?:defaultValue
        }.catch { exception ->
            if (exception is IOException) {
                emit(defaultValue)
            }
            else {
                throw exception
            }
        }
    }

    private fun<T> Flow<Preferences>.mappingNullableFlow(preferenceKey: Preferences.Key<T>, defaultValue : T?) : Flow<T?> {
        return map { preferences ->
            preferences[preferenceKey]?:defaultValue
        }.catch { exception ->
            if (exception is IOException) {
                emit(defaultValue)
            }
            else {
                throw exception
            }
        }
    }

    private fun getPreferenceBoolean(preference: String) : Preferences.Key<Boolean> {
        return booleanPreferencesKey(preference)
    }

    private fun getPreferenceString(preference: String) : Preferences.Key<String> {
        return stringPreferencesKey(preference)
    }

    private fun getPreferenceInt(preference: String) : Preferences.Key<Int> {
        return intPreferencesKey(preference)
    }

    private fun getPreferenceLong(preference: String) : Preferences.Key<Long> {
        return longPreferencesKey(preference)
    }

    private fun getPreferenceFloat(preference: String) : Preferences.Key<Float> {
        return getPreferenceFloat(preference)
    }

    private fun getPreferenceDouble(preference: String) : Preferences.Key<Double> {
        return doublePreferencesKey(preference)
    }
}