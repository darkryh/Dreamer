package com.ead.project.dreamer.presentation.settings.options

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.AppInfo
import com.ead.project.dreamer.presentation.settings.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsContentRatingFragment : PreferenceFragmentCompat() {

    private val viewModel : SettingsViewModel by viewModels()

    private lateinit var swPrivacyPolicy : SwitchPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.content_rating_preferences, rootKey)
        initLayouts()
        settingLayouts()
    }

    private fun initLayouts() {
        swPrivacyPolicy = findPreference(PREFERENCE_GOOGLE_POLICY)?:return
    }

    private fun settingLayouts() {
        lifecycleScope.launch {
            viewModel.preferences.getBooleanFlow(PREFERENCE_GOOGLE_POLICY).collectLatest { isGooglePolicyActivated ->
                if (AppInfo.isGoogleAppVersion) {
                    swPrivacyPolicy.isChecked = true
                }
                else {
                    swPrivacyPolicy.isChecked = isGooglePolicyActivated
                }
            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return if (!AppInfo.isGoogleAppVersion) {
            viewModel.getBooleanKeyPreference(preference.key)
        }
        else {
            true
        }
    }

    companion object {
        const val PREFERENCE_GOOGLE_POLICY = "PREFERENCE_GOOGLE_POLICY"
    }
}