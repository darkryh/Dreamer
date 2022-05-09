package com.ead.project.dreamer.ui.settings.options


import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.google.firebase.messaging.FirebaseMessaging

class SettingsNotificationsFragment : PreferenceFragmentCompat() {

    private lateinit var pCommunicator : SwitchPreference
    private lateinit var pNotificatorOfficial : SwitchPreference
    private lateinit var pNotificator : SwitchPreference

    private var communicators = DataStore
        .readBoolean(Constants.PREFERENCE_CUSTOMIZE_COMMUNICATORS,true)
    private var notificator = DataStore
        .readBoolean(Constants.PREFERENCE_NOTIFICATIONS,true)
    private var dreamerTopic = DataStore
        .readBoolean(Constants.DREAMER_TOPIC,true)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.notifications_preferences, rootKey)
        initLayouts()
        settingLayouts()
    }

    private fun initLayouts() {
        pCommunicator = findPreference(Constants.PREFERENCE_CUSTOMIZE_COMMUNICATORS)!!
        pNotificatorOfficial = findPreference(Constants.DREAMER_TOPIC)!!
        pNotificator = findPreference(Constants.PREFERENCE_NOTIFICATIONS)!!
    }

    private fun settingLayouts() {
        pNotificator.isChecked = notificator
        pNotificatorOfficial.isChecked = dreamerTopic
        pCommunicator.isChecked = communicators
    }


    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        val key = preference.key!!
        val data: Boolean

        when (key) {
            Constants.PREFERENCE_CUSTOMIZE_COMMUNICATORS -> {
                data = DataStore.readBoolean(key,true)
                DataStore.writeBooleanAsync(key, !data)
                return data
            }
            Constants.PREFERENCE_NOTIFICATIONS -> {
                data = DataStore.readBoolean(key,true)
                DataStore.writeBooleanAsync(key, !data)
                return data
            }
            Constants.DREAMER_TOPIC -> {
                data = DataStore.readBoolean(key,true)
                if (!data)
                    FirebaseMessaging.getInstance().subscribeToTopic(key)
                else
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(key)
                DataStore.writeBooleanAsync(key, !data)
                return data
            }
        }
        return false
    }
}