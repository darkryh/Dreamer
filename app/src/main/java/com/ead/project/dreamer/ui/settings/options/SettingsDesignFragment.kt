package com.ead.project.dreamer.ui.settings.options

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore

class SettingsDesignFragment : PreferenceFragmentCompat() {

    private lateinit var pCommunicator : SwitchPreference
    private var communicators = DataStore
        .readBoolean(Constants.PREFERENCE_OFFICIAL_ADVERTISER,true)


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.design_preferences, rootKey)
        initLayouts()
        settingsLayout()
    }

    private fun initLayouts() {
        pCommunicator = findPreference(Constants.PREFERENCE_OFFICIAL_ADVERTISER)!!
    }

    private fun settingsLayout() {
        pCommunicator.isChecked = communicators
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        val key = preference.key!!
        var data = DataStore.readBoolean(key)

        when (key) {
            Constants.PREFERENCE_THEME_MODE -> {
                DataStore.writeBooleanAsync(key,!data)
                if (!data)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                parentFragmentManager.popBackStack()
                return data
            }
            Constants.PREFERENCE_OFFICIAL_ADVERTISER -> {
                data = DataStore.readBoolean(key,true)
                DataStore.writeBooleanAsync(key, !data)
                return data
            }
        }
        return false
    }
}