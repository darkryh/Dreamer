package com.ead.project.dreamer.presentation.settings.options

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.ead.project.dreamer.R
import com.ead.project.dreamer.presentation.settings.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsDesignFragment : PreferenceFragmentCompat() {

    private val viewModel : SettingsViewModel by viewModels()
    private lateinit var swBannerOfficialAds : SwitchPreference
    private lateinit var swDarkThemeMode : SwitchPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.design_preferences, rootKey)
        initLayouts()
        settingsLayout()
    }

    private fun initLayouts() {
        swBannerOfficialAds = findPreference(PREFERENCE_OFFICIAL_ADVERTISER)?:return
        swDarkThemeMode = findPreference(PREFERENCE_THEME_MODE)?:return
    }

    private fun settingsLayout() {
        lifecycleScope.launch {
            viewModel.isDarkMode.collect { isDarkMode ->
                swDarkThemeMode.isChecked = isDarkMode
            }
        }

        lifecycleScope.launch {
            viewModel.preferences.getBooleanFlow(PREFERENCE_OFFICIAL_ADVERTISER).collectLatest { isSubscribedToBannerOficialAds ->
                swBannerOfficialAds.isChecked = isSubscribedToBannerOficialAds
            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when(val key = preference.key) {
            PREFERENCE_THEME_MODE -> {
                if(viewModel.isDarkThemeMode()) {
                    viewModel.setDarkThemeMode(false)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                else {
                    viewModel.setDarkThemeMode(true)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                parentFragmentManager.popBackStack()
                return true
            }
            else -> viewModel.updateBooleanKeyPreference(key)
        }
    }

    companion object {
        const val PREFERENCE_THEME_MODE = "PREFERENCE_THEME_MODE"
        const val PREFERENCE_OFFICIAL_ADVERTISER = "PREFERENCE_OFFICIAL_ADVERTISER"
    }
}