package com.ead.project.dreamer.app.data.action

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val ACTION_SETTINGS_FILE = "ACTION_SETTINGS_FILE"
private val Context.store : DataStore<Preferences> by preferencesDataStore(ACTION_SETTINGS_FILE)
class ActionStore(
    private val context: Context
) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun onPerform(preference : String,task : () -> Unit)  {
        scope.launch {
            context.store.edit { preferences ->
                if (!preferences.hasPerformed(preference)) {
                    preferences.performAction(preference)
                    task()
                }
            }
        }
    }

    fun resetAction(preference: String)  {
        scope.launch {
            context.store.edit { preferences ->
                preferences.resetAction(preference)
            }
        }
    }

    private fun MutablePreferences.performAction(preference: String) {
        this[getPreferenceBoolean(preference)] = true
    }

    private fun MutablePreferences.resetAction(preference: String) {
        this[getPreferenceBoolean(preference)] = false
    }

    private fun MutablePreferences.hasPerformed(preference: String) : Boolean {
        return this[getPreferenceBoolean(preference)]?:false
    }

    private fun getPreferenceBoolean(preference: String) : Preferences.Key<Boolean> {
        return booleanPreferencesKey(preference)
    }
}