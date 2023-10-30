package com.ead.project.dreamer.presentation.settings.options

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.AppInfo
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.data.utils.Thread
import com.ead.project.dreamer.presentation.settings.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsContentRatingFragment : PreferenceFragmentCompat() {

    private val viewModel : SettingsViewModel by viewModels()

    private lateinit var swFullContent : SwitchPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.content_rating_preferences, rootKey)
        initLayouts()
        settingLayouts()
    }

    private fun initLayouts() {
        swFullContent = findPreference(PREFERENCE_FULL_CONTENT)?:return
    }

    private fun settingLayouts() {
        lifecycleScope.launch {
            viewModel.isUnlockedVersion.collectLatest { isUnlockedVersion  ->
                swFullContent.isChecked = isUnlockedVersion
            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (val key = preference.key) {
            PREFERENCE_FULL_CONTENT -> {
                return if (AppInfo.isGoogleAppVersion) {
                    Thread.onClickEffect {
                        swFullContent.isChecked = false
                        toast(getString(R.string.google_policies_mode))
                    }
                    viewModel.getBooleanKeyPreference(key)
                }
                else {
                    viewModel.updateUnlockedMode()
                }
            }
            else -> { true }
        }
    }

    companion object {
        const val PREFERENCE_FULL_CONTENT = "PREFERENCE_FULL_CONTENT"
    }
}