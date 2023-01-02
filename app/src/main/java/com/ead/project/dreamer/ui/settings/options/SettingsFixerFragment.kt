package com.ead.project.dreamer.ui.settings.options

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ead.commons.lib.lifecycle.observeOnce
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.ui.settings.layouts.DiagnosticViewPreference
import com.ead.project.dreamer.ui.settings.viewmodels.SettingsFixerViewModel
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
        preferenceFixer = findPreference(Constants.PREFERENCE_CLICK_FIXER)!!
        dvpPreference = findPreference(Constants.PREFERENCE_CUSTOMIZED_DIAGNOSTIC_VIEW)!!
        preferenceFixer.isEnabled = false
        dvpPreference.setOnTestClickListener {
            dvpPreference.clearStringBuilder()
            connecting(
                Constants.API_APP,
                getString(R.string.status_try_reconnecting, "la API."),
                getString(R.string.status_api,"exitosa"),
                getString(R.string.status_api,"fallida"))

            connecting(
                Constants.PROVIDER_URL,
                getString(R.string.status_try_reconnecting, "el Proveedor."),
                getString(R.string.status_provider,"exitosa"),
                getString(R.string.status_provider,"fallida"))

            if(!settingsFixerViewModel.isDataFromDatabaseOK()) {
                dvpPreference.addLog(getString(R.string.status_database_items_incorrect))
                preferenceFixer.isEnabled = true
            }
            else dvpPreference.addLog(getString(R.string.status_database_items_correct))

            settingsFixerViewModel.getEmbedServers({},testChapter).observeOnce(this) {
                if (it.contains(getString(R.string.null_word))) {
                    dvpPreference.addLog(getString(R.string.status_server_script_incorrect))
                    preferenceFixer.isEnabled = true
                }
                else dvpPreference.addLog(getString(R.string.status_server_script_correct))
            }
        }

    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            Constants.PREFERENCE_CLICK_FIXER -> settingsFixerViewModel.synchronizeScrapper()
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
}