package com.ead.project.dreamer.presentation.settings.options

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.commons.lib.views.setResourceImageAndColor
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.launchActivityAndFinish
import com.ead.project.dreamer.app.model.EadAccount
import com.ead.project.dreamer.presentation.login.LoginActivity
import com.ead.project.dreamer.presentation.settings.layouts.AccountViewPreference
import com.ead.project.dreamer.presentation.settings.viewmodels.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsAccountFragment : PreferenceFragmentCompat() {

    private val viewModel : SettingsViewModel by viewModels()

    private lateinit var prefAdviser : Preference
    private lateinit var prefSession : Preference
    private lateinit var accountPreference: AccountViewPreference

    private var eadAccount : EadAccount?=null
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.account_preferences, rootKey)
        initLayouts()
        userConfiguration()
    }

    private fun initLayouts() {
        accountPreference = findPreference(PREFERENCE_CUSTOMIZED_IMV_PROFILE)?:return
        prefAdviser = findPreference(PREFERENCE_ADVISER)?:return
        prefSession = findPreference(PREFERENCE_SESSION)?:return
    }

    private fun userConfiguration() {
        lifecycleScope.launch {
            viewModel.getAccount().collectLatest { eadAccount ->
                this@SettingsAccountFragment.eadAccount = eadAccount
                if (eadAccount != null) {
                    prefAdviser.isVisible = false
                }
                else {
                    prefSession.summary = requireActivity().getString(R.string.log_in)
                }
            }
        }

        accountPreference.isBinded.observe(this) { isBinded ->
            val eadAccount = this.eadAccount
            if (!isBinded || eadAccount == null) return@observe

            accountPreference.state.setResourceImageAndColor(R.drawable.ic_check_24,R.color.green)
            accountPreference.userName.text = eadAccount.displayName
            accountPreference.rank.text = eadAccount.ranksNames.toString()
            accountPreference.profile.load(eadAccount.profileImage ?:return@observe) {
                transformations(CircleCropTransformation())
            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (val key = preference.key) {
            PREFERENCE_SESSION -> {
                if (eadAccount != null) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.session))
                        .setMessage(getString(R.string.warning_logout,eadAccount?.displayName))
                        .setPositiveButton(getString(R.string.confirm)) { _: DialogInterface?, _: Int ->
                            viewModel.logout()
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