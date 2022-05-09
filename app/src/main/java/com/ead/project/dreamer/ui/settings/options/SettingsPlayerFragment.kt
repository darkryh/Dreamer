package com.ead.project.dreamer.ui.settings.options

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.retrofit.model.discord.User
import com.ead.project.dreamer.data.utils.DataStore

class SettingsPlayerFragment : PreferenceFragmentCompat() {

    private var user : User?= User.get()
    private lateinit var playerAutomatic : SwitchPreference


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.player_preferences, rootKey)
        initLayouts()
        userConfiguration()
    }

    private fun initLayouts() {
        playerAutomatic = findPreference(Constants.PREFERENCE_RANK_AUTOMATIC_PLAYER)!!
    }


    private fun userConfiguration() {
        if (user == null) {
            playerAutomatic.isChecked = false
            playerAutomatic.isEnabled = false
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        val key = preference.key!!
        val data = DataStore.readBoolean(key)

        when (key) {
            Constants.PREFERENCE_RANK_AUTOMATIC_PLAYER -> {
                DataStore.writeBooleanAsync(key, !data)
                return data
            }
            Constants.PREFERENCE_EXTERNAL_PLAYER -> {
                DataStore.writeBooleanAsync(key, !data)
                return data
            }
            Constants.PREFERENCE_PIP_MODE_PLAYER -> {
                DataStore.writeBooleanAsync(key, !data)
                return data
            }
        }
        return false
    }
}