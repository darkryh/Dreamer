package com.ead.project.dreamer.presentation.settings.options

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.AppInfo
import com.ead.project.dreamer.app.data.notifications.NotificationManager.Companion.ALL
import com.ead.project.dreamer.presentation.settings.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsNotificationsFragment : PreferenceFragmentCompat() {

    private val viewModel : SettingsViewModel by viewModels()

    private lateinit var swMarketingNotifications : SwitchPreference
    private lateinit var lpNotifications : androidx.preference.ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.notifications_preferences, rootKey)
        initLayouts()
        settingLayouts()
        functionsLayout()
    }

    private fun initLayouts() {
        swMarketingNotifications = findPreference(AppInfo.TOPIC)?:return
        lpNotifications = findPreference(PREFERENCE_NOTIFICATIONS)?:return
    }

    private fun settingLayouts() {
        lifecycleScope.launch {
            viewModel.preferences.getBooleanFlow(AppInfo.TOPIC,true).collectLatest { isSubscribed ->
                swMarketingNotifications.isChecked = isSubscribed
                viewModel.subscribeToAppTopicIf(isSubscribed)
            }
        }
        lifecycleScope.launch {
            viewModel.preferences.getIntFlow(PREFERENCE_NOTIFICATIONS_OPTION,ALL).collectLatest {
                lpNotifications.setValueIndex(it)
            }
        }
    }

    private fun functionsLayout() {
        lpNotifications.setOnPreferenceChangeListener { _, newValue ->
            lifecycleScope.launch {
                val notificationOption = newValue.toString().toInt()
                viewModel.preferences.set(PREFERENCE_NOTIFICATIONS_OPTION,notificationOption)
            }
            true
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (val key = preference.key) {
            AppInfo.TOPIC -> viewModel.updateBooleanKeyPreference(key)
            else -> false
        }
    }

    companion object {
        const val PREFERENCE_NOTIFICATIONS = "PREFERENCE_NOTIFICATIONS"
        const val PREFERENCE_NOTIFICATIONS_OPTION = "PREFERENCE_NOTIFICATIONS_OPTION"
    }
}