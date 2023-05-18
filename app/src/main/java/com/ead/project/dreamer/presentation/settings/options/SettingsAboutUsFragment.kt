package com.ead.project.dreamer.presentation.settings.options

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.AppInfo
import com.ead.project.dreamer.app.data.preference.Settings
import com.ead.project.dreamer.presentation.settings.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsAboutUsFragment : PreferenceFragmentCompat() {

    companion object {

        const val PREFERENCE_TIKTOK = "PREFERENCE_TIKTOK"
        const val PREFERENCE_INSTAGRAM = "PREFERENCE_INSTAGRAM"
        const val PREFERENCE_TWITTER = "PREFERENCE_TWITTER"
        const val PREFERENCE_DISCORD = "PREFERENCE_DISCORD"

        const val TIKTOK = "https://www.tiktok.com/@darkryh"
        const val INSTAGRAM = "https://www.instagram.com/darkryh"
        const val TWITTER = "https://twitter.com/Darkryh"
        const val DISCORD = "https://discord.gg/mvMfenSazJ"
    }

    private val viewModel : SettingsViewModel by viewModels()

    private lateinit var prefVersion : Preference
    private lateinit var prefState : Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.about_us_preferences, rootKey)
        initLayouts()
        settingLayouts()
    }

    private fun initLayouts() {
        prefVersion = findPreference(Settings.APP_VERSION)?:return
        prefState = findPreference(Settings.SYNC_DIRECTORY_PROFILE)?:return
    }

    private fun settingLayouts() {
        lifecycleScope.launch {
            viewModel.preferences.getBooleanFlow(Settings.SYNC_DIRECTORY_PROFILE).collectLatest { isSynchronized ->
                if (isSynchronized) {
                    prefState.summary = requireActivity().getString(R.string.sync_complete)
                }
                else {
                    prefState.summary = requireActivity().getString(R.string.sync_in_progress)
                }
            }
        }
        prefVersion.summary = getString(R.string.version_description,"Release",AppInfo.version, AppInfo.versionCode.toString())
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            PREFERENCE_TIKTOK -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(TIKTOK))
                startActivity(intent)
            }
            PREFERENCE_INSTAGRAM -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(INSTAGRAM))
                startActivity(intent)
            }
            PREFERENCE_TWITTER -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(TWITTER))
                startActivity(intent)
            }
            PREFERENCE_DISCORD -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DISCORD))
                startActivity(intent)
            }
        }
        return false
    }
}