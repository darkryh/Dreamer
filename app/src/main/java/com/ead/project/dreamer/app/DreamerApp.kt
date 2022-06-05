package com.ead.project.dreamer.app

import android.app.Application
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.Configuration
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.worker.factory.DaggerWorkerFactory
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DreamerApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: DaggerWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()

    companion object {
        lateinit var INSTANCE : DreamerApp
        lateinit var MOBILE_AD_INSTANCE: InitializationStatus

        fun showShortToast(message : String) {
            Toast.makeText(INSTANCE,message,Toast.LENGTH_SHORT).show()
        }

        fun showLongToast(message : String) {
            Toast.makeText(INSTANCE,message,Toast.LENGTH_LONG).show()
        }

    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        settingTheme()
        MobileAds.initialize(INSTANCE) { MOBILE_AD_INSTANCE = it }
        initPreferences()
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
        val theme = DataStore.readBoolean(Constants.PREFERENCE_THEME_MODE)
        if (theme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}