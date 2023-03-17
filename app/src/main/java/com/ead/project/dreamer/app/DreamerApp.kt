package com.ead.project.dreamer.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DreamerApp : Application(), Configuration.Provider {

    @Inject lateinit var hiltWorkerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
        .setWorkerFactory(hiltWorkerFactory)
        .build()

    companion object {

        lateinit var Instance : DreamerApp
        var InitializationStatus: InitializationStatus? = null

        fun initAdsPreferences() {
            if (InitializationStatus!= null) MobileAds.initialize(Instance) { InitializationStatus = it }
        }
    }

    override fun onCreate() {
        Instance = this
        initAdsPreferences()
        settingTheme()
        initPreferences()
        super.onCreate()
    }

    private fun initPreferences() {
        DataStore.apply {
            writeBooleanAsync(Constants.WORK_PREFERENCE_CLICKED_CHAPTER,false)
            writeBooleanAsync(Constants.WORK_PREFERENCE_CLICKED_PROFILE,false)
            writeBooleanAsync(Constants.PREFERENCE_DIRECTORY_CLICKED,false)
            writeBooleanAsync(Constants.PREFERENCE_SETTINGS_CLICKED,false)
            writeBooleanAsync(Constants.WORK_PREFERENCE_CLICKED_PROFILE_SUGGESTION,false)
            writeBooleanAsync(Constants.VERSION_DEPRECATED,false)
            writeDouble(Constants.MINIMUM_VERSION_REQUIRED,0.0)
        }
    }

    private fun settingTheme () {
        val isDarkTheme = Constants.isDarkThemeMode()
        if (isDarkTheme) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

}