package com.ead.project.dreamer.ui.settings.options

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.ui.settings.viewmodels.SettingsFixerViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFixerFragment : PreferenceFragmentCompat() {

    private val settingsFixerViewModel : SettingsFixerViewModel by viewModels()
    private lateinit var preferenceStateApi: Preference
    private lateinit var preferenceProvider: Preference
    private lateinit var preferenceFixer: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fixer_preferences, rootKey)
        initLayouts()
    }
    private fun initLayouts() {
        preferenceStateApi = findPreference(Constants.PREFERENCE_CLICK_DREAMER_CONNECTION)!!
        preferenceProvider = findPreference(Constants.PREFERENCE_CLICK_MC2_CONNECTION)!!
        preferenceFixer = findPreference(Constants.PREFERENCE_CLICK_FIXER)!!
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key!!) {
            Constants.PREFERENCE_CLICK_DREAMER_CONNECTION -> {
                preferenceStateApi.isEnabled = false
                connecting(
                    Constants.API_APP,
                    preferenceStateApi,
                    getString(R.string.status_api,"exitosa"),
                    getString(R.string.status_api,"fallida"))
            }
            Constants.PREFERENCE_CLICK_MC2_CONNECTION -> {
                preferenceProvider.isEnabled = false
                connecting(
                    Constants.PROVIDER_URL,
                    preferenceProvider,
                    getString(R.string.status_provider,"exitosa"),
                    getString(R.string.status_provider,"fallida"))
            }
            Constants.PREFERENCE_CLICK_FIXER -> {
                preferenceFixer.isEnabled = false
                settingsFixerViewModel.synchronizeScrapper()
            }
        }
        return false
    }

    private fun connecting(url : String,preference: Preference,positiveMessage : String,negativeMessage : String) {

        settingsFixerViewModel.getConnectionState(url).observe(viewLifecycleOwner) {
            when(it) {
                0 -> {
                    val summary: Spannable = SpannableString(getString(R.string.try_reconnecting))
                    val color = Color.GRAY
                    summary.setSpan(ForegroundColorSpan(color), 0, summary.length, 0)
                    preference.summary = summary
                    val drawable = DreamerLayout.getDrawable(R.drawable.ic_assignment_24)
                    DreamerLayout.setColorFilter(drawable,color)
                    preference.icon = drawable
                }
                1 -> {
                    val summary: Spannable = SpannableString(positiveMessage)
                    val color = Color.GREEN
                    summary.setSpan(ForegroundColorSpan(color), 0, summary.length, 0)
                    preference.summary = summary
                    val drawable = DreamerLayout.getDrawable(R.drawable.ic_check_24)
                    DreamerLayout.setColorFilter(drawable,color)
                    preference.icon = drawable
                }
                -1 -> {
                    val summary: Spannable = SpannableString(negativeMessage)
                    val color = Color.RED
                    summary.setSpan(ForegroundColorSpan(color), 0, summary.length, 0)
                    preference.isEnabled = true
                    preference.summary = summary
                    val drawable = DreamerLayout.getDrawable(R.drawable.ic_clear)
                    DreamerLayout.setColorFilter(drawable,color)
                    preference.icon = drawable
                }
            }
        }
    }
}