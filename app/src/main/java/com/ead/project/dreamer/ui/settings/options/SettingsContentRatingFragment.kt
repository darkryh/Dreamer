package com.ead.project.dreamer.ui.settings.options

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.ead.commons.lib.lifecycle.fragment.showLongToast
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.ThreadUtil

class SettingsContentRatingFragment : PreferenceFragmentCompat() {

    private lateinit var pPrivacyPolicy : SwitchPreference
    private val isTheAppFromGoogle = DataStore
        .readBoolean(Constants.IS_THE_APP_FROM_GOOGLE)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.content_rating_preferences, rootKey)
        initLayouts()
        settingLayouts()
    }

    private fun initLayouts() {
        pPrivacyPolicy = findPreference(Constants.PREFERENCE_GOOGLE_POLICY)!!
    }

    private fun settingLayouts() {
        if (isTheAppFromGoogle)
            pPrivacyPolicy.isChecked = true
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        val key = preference.key!!
        val data = DataStore.readBoolean(key)

        when (key) {
            Constants.PREFERENCE_GOOGLE_POLICY -> {
                if (!isTheAppFromGoogle)
                    DataStore.writeBooleanAsync(key, !data)
                else run {
                    pPrivacyPolicy.isChecked = true
                    showLongToast(requireActivity().getString(R.string.google_policies_mode))
                }
                return data
            }
        }
        return false
    }

    private fun run(task: () -> Unit) = ThreadUtil.runInMs(task,160)
}