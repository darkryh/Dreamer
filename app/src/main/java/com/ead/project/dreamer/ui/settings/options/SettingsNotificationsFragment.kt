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
    private lateinit var lNotificator : androidx.preference.ListPreference

    private var communicators = DataStore
        .readBoolean(Constants.PREFERENCE_CUSTOMIZE_COMMUNICATORS,true)
    private lateinit var notificator :String
    private var dreamerTopic = DataStore
        .readBoolean(Constants.DREAMER_TOPIC,true)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.notifications_preferences, rootKey)
        initLayouts()
        settingLayouts()
        functionsLayout()
    }

    private fun initLayouts() {
        pCommunicator = findPreference(Constants.PREFERENCE_CUSTOMIZE_COMMUNICATORS)!!
        pNotificatorOfficial = findPreference(Constants.DREAMER_TOPIC)!!
        lNotificator = findPreference(Constants.PREFERENCE_NOTIFICATIONS)!!
    }

    private fun settingLayouts() {
        notificator = DataStore.readInt(Constants.PREFERENCE_NOTIFICATIONS, lNotificator.value.toInt()).toString()
        pNotificatorOfficial.isChecked = dreamerTopic
        pCommunicator.isChecked = communicators
        lNotificator.value = notificator
    }

    private fun functionsLayout() {
        lNotificator.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                DataStore.writeIntAsync(preference.key,newValue.toString().toInt())
                true
            }
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
            Constants.PREFERENCE_NOTIFICATIONS -> { return true }
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