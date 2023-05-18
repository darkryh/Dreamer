package com.ead.project.dreamer.app

import android.app.Application
import android.app.DownloadManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.ead.project.dreamer.app.data.network.Network
import com.ead.project.dreamer.app.data.notifications.NotificationChannels
import com.ead.project.dreamer.app.data.preference.Settings
import com.ead.project.dreamer.app.data.util.system.resetDownloads
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject lateinit var hiltWorkerFactory: HiltWorkerFactory
    @Inject lateinit var preferenceUseCase: PreferenceUseCase

    @Inject lateinit var scope : CoroutineScope
    @Inject lateinit var downloadManager: DownloadManager

    override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
        .setWorkerFactory(hiltWorkerFactory)
        .build()

    override fun onCreate() {
        super.onCreate()
        Instance = this
        initAdsPreferences()
        initApplicationTheme()
        initApplicationPreferences()
        createNotificationChannels()
        subscribeToNetworkConnection()
    }

    override fun onTerminate() {
        super.onTerminate()
        Network.unregisterCallback()
    }

    private fun initApplicationPreferences() {


        scope.launch {

            preferenceUseCase.appBuildPreferences.apply {

                if (AppInfo.isGoogleAppVersion) {
                    setUnlockedVersion(false)
                }

            }

            preferenceUseCase.preferences.apply {

                if (getBoolean(Settings.RESET_DOWNLOADS,true)){
                    set(Settings.RESET_DOWNLOADS,false)
                    downloadManager.resetDownloads()
                }

            }
        }
    }

    private fun createNotificationChannels() {
        NotificationChannels.initialize(Instance)
    }

    private fun subscribeToNetworkConnection() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        Network.registerCallBack(request)
    }

    private fun initApplicationTheme () {
        if (preferenceUseCase.appBuildPreferences.isDarkTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


    companion object {

        lateinit var Instance : App
        var InitializationStatus: InitializationStatus? = null

        fun initAdsPreferences() {
            if (InitializationStatus != null) {
                MobileAds.initialize(Instance) { InitializationStatus = it }
            }
        }

    }

}