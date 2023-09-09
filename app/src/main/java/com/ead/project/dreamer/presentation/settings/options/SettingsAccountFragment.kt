package com.ead.project.dreamer.presentation.settings.options

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.discord.Discord
import com.ead.project.dreamer.app.data.util.system.launchActivityAndFinish
import com.ead.project.dreamer.presentation.login.LoginActivity
import com.ead.project.dreamer.presentation.settings.layouts.AccountViewPreference
import com.ead.project.dreamer.presentation.settings.viewmodels.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsAccountFragment : PreferenceFragmentCompat() {

    private val viewModel : SettingsViewModel by viewModels()

    private lateinit var prefAdviser : Preference
    private lateinit var prefSession : Preference
    private var avAccountPreference: AccountViewPreference? = null
    private val discordUser = Discord.getUser()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.account_preferences, rootKey)
        initLayouts()
        userConfiguration()
    }

    private fun initLayouts() {
        avAccountPreference = findPreference(PREFERENCE_CUSTOMIZED_IMV_PROFILE) as AccountViewPreference?
        prefAdviser = findPreference(PREFERENCE_ADVISER)?:return
        prefSession = findPreference(PREFERENCE_SESSION)?:return
    }

    private fun userConfiguration() {
        if (discordUser != null) {
            prefAdviser.isVisible = false
        }
        else {
            prefSession.summary = requireActivity().getString(R.string.log_in)
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (val key = preference.key) {
            PREFERENCE_SESSION -> {
                if (discordUser != null) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.session))
                        .setMessage(getString(R.string.warning_logout,null))
                        .setPositiveButton(getString(R.string.confirm)) { _: DialogInterface?, _: Int ->
                            Discord.logout()
                            val intent: Intent? = requireActivity().baseContext.packageManager.getLaunchIntentForPackage(
                                requireActivity().baseContext.packageName
                            )
                            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent?:return@setPositiveButton)
                        }
                        .setNegativeButton(getString(R.string.cancel),null)
                        .show()
                    true
                }
                else {
                    requireActivity().launchActivityAndFinish(LoginActivity::class.java)
                    viewModel.updateBooleanKeyPreferenceTo(PREFERENCE_SKIP_LOGIN,false)
                }
            }
            else -> {
                viewModel.updateBooleanKeyPreference(key)
            }
        }
    }

    companion object {
        const val PREFERENCE_ADVISER = "PREFERENCE_ADVISER"
        const val PREFERENCE_SESSION = "PREFERENCE_SESSION"
        const val PREFERENCE_CUSTOMIZED_IMV_PROFILE = "PREFERENCE_CUSTOMIZED_IMV_PROFILE"

        const val PREFERENCE_SKIP_LOGIN = "PREFERENCE_SKIP_LOGIN"
    }
}