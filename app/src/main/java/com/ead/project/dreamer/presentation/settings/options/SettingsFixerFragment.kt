package com.ead.project.dreamer.presentation.settings.options

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.AppInfo
import com.ead.project.dreamer.app.data.monos_chinos.MonosChinos
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.presentation.settings.layouts.DiagnosticViewPreference
import com.ead.project.dreamer.presentation.settings.viewmodels.SettingsFixerViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFixerFragment : PreferenceFragmentCompat() {

    private val settingsFixerViewModel : SettingsFixerViewModel by viewModels()
    private lateinit var preferenceFixer: Preference
    private lateinit var dvpPreference: DiagnosticViewPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fixer_preferences, rootKey)
        initLayouts()
    }
    private fun initLayouts() {
        preferenceFixer = findPreference(PREFERENCE_CLICK_FIXER)?:return
        dvpPreference = findPreference(PREFERENCE_CUSTOMIZED_DIAGNOSTIC_VIEW)?:return
        preferenceFixer.isEnabled = false
        dvpPreference.setOnTestClickListener {
            dvpPreference.clearStringBuilder()
            it.isEnabled = false
            connecting(
                AppInfo.API_APP,
                getString(R.string.status_try_reconnecting, "la API."),
                getString(R.string.status_api,"exitosa"),
                getString(R.string.status_api,"fallida"))

            connecting(
                MonosChinos.URL,
                getString(R.string.status_try_reconnecting, "el Proveedor."),
                getString(R.string.status_provider,"exitosa"),
                getString(R.string.status_provider,"fallida"))

            if(!settingsFixerViewModel.isDataFromDatabaseOK()) {
                dvpPreference.addLog(getString(R.string.status_database_items_incorrect))
                preferenceFixer.isEnabled = true
            }
            else dvpPreference.addLog(getString(R.string.status_database_items_correct))

        }

    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            PREFERENCE_CLICK_FIXER -> settingsFixerViewModel.synchronizeScrapper()
        }
        return false
    }

    private fun connecting(url : String, connectingMessage :String, positiveMessage : String, negativeMessage : String) {
        settingsFixerViewModel.getConnectionState(url).observe(viewLifecycleOwner) {
            when(it) {
                0 -> dvpPreference.addLog(connectingMessage)
                1 -> dvpPreference.addLog(positiveMessage)
                -1 -> {
                    dvpPreference.addLog(negativeMessage)
                    preferenceFixer.isEnabled = true
                }
            }
        }
    }

    private val testChapter : Chapter = Chapter(0, 0, "null", "null", -1, "https://monoschinos2.com/ver/cowboy-bebop-latino-episodio-1")

    companion object {
        const val PREFERENCE_CUSTOMIZED_DIAGNOSTIC_VIEW = "PREFERENCE_CUSTOMIZED_DIAGNOSTIC_VIEW"
        const val PREFERENCE_CLICK_FIXER = "PREFERENCE_CLICK_FIXER"
    }
}