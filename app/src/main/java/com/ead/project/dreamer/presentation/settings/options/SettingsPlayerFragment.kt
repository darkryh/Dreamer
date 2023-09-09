package com.ead.project.dreamer.presentation.settings.options

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.launchActivity
import com.ead.project.dreamer.app.model.PlayerPreference
import com.ead.project.dreamer.app.model.ServerPreference
import com.ead.project.dreamer.presentation.server.order.ServerOrderActivity
import com.ead.project.dreamer.presentation.settings.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsPlayerFragment : PreferenceFragmentCompat() {

    private val viewModel : SettingsViewModel by viewModels()

    private lateinit var pAutomaticPlayerModeOrder : Preference

    private lateinit var swAutomaticPlayerMode : SwitchPreference
    private lateinit var swPictureInPictureMode : SwitchPreference
    private lateinit var swExternalPlayerMode : SwitchPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.player_preferences, rootKey)
        initLayouts()
        configLayouts()
    }

    private fun initLayouts() {
        pAutomaticPlayerModeOrder = findPreference(PREFERENCE_RANK_AUTOMATIC_PLAYER_ORDER)?:return
        swAutomaticPlayerMode = findPreference(PREFERENCE_RANK_AUTOMATIC_PLAYER)?:return
        swPictureInPictureMode = findPreference(PREFERENCE_PIP_MODE_PLAYER)?:return
        swExternalPlayerMode = findPreference(PREFERENCE_EXTERNAL_PLAYER)?:return
    }

    private fun configLayouts() {
        lifecycleScope.launch {
            viewModel.playerPreferencesFlow.collectLatest { playerPreference: PlayerPreference ->
                swExternalPlayerMode.isChecked = playerPreference.isInExternalMode
                swPictureInPictureMode.isChecked = playerPreference.isInPictureInPictureMode
            }
        }
        lifecycleScope.launch {
            viewModel.serverPreferenceFlow.collectLatest { serverPreference: ServerPreference ->
                swAutomaticPlayerMode.isChecked = serverPreference.isAutomatic
                pAutomaticPlayerModeOrder.isEnabled = serverPreference.isAutomatic
            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when(preference.key) {
            PREFERENCE_RANK_AUTOMATIC_PLAYER_ORDER -> {
                requireActivity().launchActivity(ServerOrderActivity::class.java)
                return true
            }
            PREFERENCE_RANK_AUTOMATIC_PLAYER -> {
                viewModel.updateAutomaticPlayerMode()
            }
            PREFERENCE_EXTERNAL_PLAYER -> {
                viewModel.updateExternalPlayerMode()
            }
            PREFERENCE_PIP_MODE_PLAYER -> {
                viewModel.updatePictureInPictureMode()
            }
            else -> {
                viewModel.updateBooleanKeyPreference(preference.key)
            }
        }
    }

    companion object {
        const val PREFERENCE_RANK_AUTOMATIC_PLAYER_ORDER = "PREFERENCE_RANK_AUTOMATIC_PLAYER_ORDER"
        const val PREFERENCE_RANK_AUTOMATIC_PLAYER = "PREFERENCE_RANK_AUTOMATIC_PLAYER"
        const val PREFERENCE_EXTERNAL_PLAYER = "PREFERENCE_EXTERNAL_PLAYER"
        const val PREFERENCE_PIP_MODE_PLAYER = "PREFERENCE_PIP_MODE_PLAYER"
    }
}