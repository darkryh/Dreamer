package com.ead.project.dreamer.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.utils.ThreadUtil
import com.ead.project.dreamer.ui.settings.options.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsDashboardFragment : PreferenceFragmentCompat() {

    companion object {
        const val PREFERENCE_ACCOUNT = "PREFERENCE_ACCOUNT"
        const val PREFERENCE_PLAYER = "PREFERENCE_PLAYER"
        const val PREFERENCE_DESIGN = "PREFERENCE_DESIGN"
        const val PREFERENCE_FIXER = "PREFERENCE_FIXER"
        const val PREFERENCE_CONTENT_RATING = "PREFERENCE_CONTENT_RATING"
        const val PREFERENCE_NOTIFICATIONS = "PREFERENCE_NOTIFICATIONS"
        const val PREFERENCE_ABOUT_US = "PREFERENCE_ABOUT_US"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.dashboard_preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            PREFERENCE_ACCOUNT -> launchPreferencesCategory(SettingsAccountFragment())
            PREFERENCE_PLAYER -> launchPreferencesCategory(SettingsPlayerFragment())
            PREFERENCE_DESIGN -> launchPreferencesCategory(SettingsDesignFragment())
            PREFERENCE_FIXER -> launchPreferencesCategory(SettingsFixerFragment())
            PREFERENCE_CONTENT_RATING -> launchPreferencesCategory(SettingsContentRatingFragment())
            PREFERENCE_NOTIFICATIONS -> launchPreferencesCategory(SettingsNotificationsFragment())
            PREFERENCE_ABOUT_US -> launchPreferencesCategory(SettingsAboutUsFragment())
        }
        return false
    }

    private fun launchPreferencesCategory(requestedFragment : Fragment) {
        ThreadUtil.runInMs({
            val transaction = requireActivity().supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)

            transaction.replace(R.id.Frame_Content_Settings, requestedFragment).commit()
        },160)
    }
}