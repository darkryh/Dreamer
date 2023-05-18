package com.ead.project.dreamer.app.data.preference

import androidx.datastore.core.DataStore
import com.ead.project.dreamer.app.AppInfo
import com.ead.project.dreamer.app.data.files.Files
import com.ead.project.dreamer.app.model.AppBuild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject

class AppBuildPreferences @Inject constructor(
    private val store : DataStore<AppBuild>
) {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val appBuild get() = store.data

    val isDarkMode get() = appBuild.map { it.isDarkTheme }

    fun isUnlockedVersion() : Boolean = runBlocking { store.data.first().isUnlockedVersion }

    fun isLockedVersion () : Boolean = !isUnlockedVersion()

    fun isDarkTheme() : Boolean = runBlocking { store.data.first().isDarkTheme }

    fun getLastVersionFile() : File = runBlocking {
        File(Files.DirectoryDownloadsFile,"${store.data.first().lastVersion}.apk")
    }

    fun setDarkMode(value : Boolean) {
        runBlocking {
            store.updateData { appBuild ->
                appBuild.copy(
                    isDarkTheme = value
                )
            }
        }
    }

    fun update(build: AppBuild) {
        scope.launch {
            store.updateData {
                build.copy(
                    currentVersionDeprecated = AppInfo.versionValue < build.minVersion
                )
            }
        }
    }

    fun setUnlockedVersion(value: Boolean) {
        scope.launch {
            store.updateData { appBuild ->
                appBuild.copy(
                    isUnlockedVersion = value
                )
            }
        }
    }

    fun updateUnlockedVersion() {
        scope.launch {
            store.updateData { appBuild: AppBuild ->
                appBuild.copy(
                    isUnlockedVersion =  !appBuild.isUnlockedVersion
                )
            }
        }
    }
}