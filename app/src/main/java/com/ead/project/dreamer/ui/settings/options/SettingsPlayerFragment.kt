package com.ead.project.dreamer.ui.settings.options

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.models.discord.User
import com.ead.project.dreamer.data.utils.DataStore

class SettingsPlayerFragment : PreferenceFragmentCompat() {

    private var user : User? = User.get()
    private lateinit var playerAutomatic : SwitchPreference
    private lateinit var playerPipMode : SwitchPreference


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.player_preferences, rootKey)
        initLayouts()
        userConfiguration()
    }

    private fun initLayouts() {
        playerAutomatic = findPreference(Constants.PREFERENCE_RANK_AUTOMATIC_PLAYER)!!
        playerPipMode = findPreference(Constants.PREFERENCE_PIP_MODE_PLAYER)!!
    }


    private fun userConfiguration() {
        playerAutomatic.isChecked = Constants.isAutomaticPlayerMode()
        playerAutomatic.isEnabled =  user!= null
        playerPipMode.isChecked = Constants.getPlayerPipMode()
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        val key = preference.key!!
        var data = DataStore.readBoolean(key)

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
                data = DataStore.readBoolean(key,true)
                DataStore.writeBooleanAsync(key, !data)
                return data
            }
        }
        return false
    }
}