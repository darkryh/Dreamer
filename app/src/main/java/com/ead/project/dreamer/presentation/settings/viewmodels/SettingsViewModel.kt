package com.ead.project.dreamer.presentation.settings.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ead.project.dreamer.app.AppInfo
import com.ead.project.dreamer.app.data.server.Server
import com.ead.project.dreamer.app.model.EadAccount
import com.ead.project.dreamer.app.repository.FirebaseClient
import com.ead.project.dreamer.domain.PreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val client: FirebaseClient,
    preferenceUseCase: PreferenceUseCase
) : ViewModel() {

    private val appBuildPreferences = preferenceUseCase.appBuildPreferences
    private val playerPreferences = preferenceUseCase.playerPreferences
    private val userPreferences = preferenceUseCase.userPreferences
    val preferences = preferenceUseCase.preferences


    val isUnlockedVersion = appBuildPreferences.isUnlockedVersion
    val isDarkMode = appBuildPreferences.isDarkMode

    val playerPreferencesFlow = playerPreferences.preference
    val serverPreferenceFlow = Server.serverPreferenceFlow

    fun getAccount() : Flow<EadAccount?> = userPreferences.user

    fun logout() = userPreferences.logout()

    fun subscribeToAppTopicIf(isSubscribed : Boolean) {
        if(isSubscribed) {
            client.inAppMessage.subscribeToTopic(AppInfo.TOPIC)
        }
        else {
            client.inAppMessage.unsubscribeFromTopic(AppInfo.TOPIC)
        }
    }


    fun getBooleanKeyPreference(key: String) : Boolean {
        return runBlocking {
            !preferences.getBoolean(key)
        }
    }

    fun updateBooleanKeyPreference(key : String) : Boolean {
        viewModelScope.launch {
            val booleanData = preferences.getBoolean(key)
            preferences.set(key,!booleanData)
        }
        return true
    }

    fun isDarkThemeMode() : Boolean {
        return appBuildPreferences.isDarkTheme()
    }

    fun setDarkThemeMode(value: Boolean) {
        appBuildPreferences.setDarkMode(value)
    }

    fun updateBooleanKeyPreferenceTo(key: String,value : Boolean) : Boolean {
        viewModelScope.launch {
            preferences.set(key,value)
        }
        return true
    }

    fun updateExternalPlayerMode() : Boolean {
        viewModelScope.launch {
            playerPreferences.updateExternalMode()
        }
        return true
    }

    fun updateAutomaticPlayerMode() : Boolean {
        viewModelScope.launch {
            Server.updateAutomaticResolver()
        }
        return true
    }

    fun updatePictureInPictureMode() : Boolean {
        viewModelScope.launch {
            playerPreferences.updatePictureInPictureMode()
        }
        return true
    }

    fun updateUnlockedMode() : Boolean {
        viewModelScope.launch {
            appBuildPreferences.updateUnlockedVersion()
        }
        return true
    }

}