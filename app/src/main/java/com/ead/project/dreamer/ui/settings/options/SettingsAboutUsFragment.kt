package com.ead.project.dreamer.ui.settings.options

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ead.project.dreamer.BuildConfig
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants

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

    private lateinit var pVersion : Preference
    private lateinit var pState : Preference
    private var versionName = BuildConfig.VERSION_NAME

    private val isDirectoryProfileStateCompleted = Constants.isDirectorySynchronized()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.about_us_preferences, rootKey)
        initLayouts()
        settingLayouts()
    }

    private fun initLayouts() {
        pVersion = findPreference(Constants.PREFERENCE_APP_VERSION)!!
        pState = findPreference(Constants.PREFERENCE_DIRECTORY_PROFILE)!!
    }

    private fun settingLayouts() {
        if (isDirectoryProfileStateCompleted)
            pState.summary = requireActivity().getString(R.string.sync_complete)
        else
            pState.summary = requireActivity().getString(R.string.sync_in_progress)

        pVersion.summary = getString(R.string.version_description,"Release",versionName)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key!!) {
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