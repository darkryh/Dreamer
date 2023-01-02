package com.ead.project.dreamer.ui.settings.options

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.models.discord.User
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.ui.login.LoginActivity
import com.ead.project.dreamer.ui.settings.layouts.AccountViewPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsAccountFragment : PreferenceFragmentCompat() {

    private var user : User?= User.get()

    private lateinit var pAdviser : Preference
    private lateinit var pSession : Preference

    private var imvpProfile: AccountViewPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.account_preferences, rootKey)
        initLayouts()
        settingLayouts()
        userConfiguration()
    }

    private fun initLayouts() {
        imvpProfile = findPreference(Constants.PREFERENCE_CUSTOMIZED_IMV_PROFILE) as AccountViewPreference?
        pAdviser = findPreference(Constants.PREFERENCE_ADVISER)!!
        pSession = findPreference(Constants.PREFERENCE_SESSION)!!
    }

    private fun settingLayouts() {
        //imvpProfile?.setAccountClickListener{ /*TO DO*/}
    }

    private fun userConfiguration() {
        if (user != null) {
            pAdviser.isVisible = false
        }
        else
            pSession.summary = requireActivity().getString(R.string.log_in)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        val key = preference.key!!
        val data = DataStore.readBoolean(key)
        when (key) {
            Constants.PREFERENCE_SESSION -> {
                if (user != null) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.session))
                        .setMessage(getString(R.string.warning_logout,User.get()?.username))
                        .setPositiveButton(getString(R.string.confirm)) { _: DialogInterface?, _: Int ->
                            User.logout()
                            val intent: Intent? = requireActivity().baseContext.packageManager.getLaunchIntentForPackage(
                                requireActivity().baseContext.packageName
                            )
                            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }
                        .setNegativeButton(getString(R.string.cancel),null)
                        .show()
                }
                else {
                    DataStore.writeBooleanAsync(Constants.PREFERENCE_SKIP_LOGIN,false)
                    requireActivity().startActivity(Intent(requireActivity(), LoginActivity::class.java))
                    requireActivity().finish()
                }
            }
            Constants.PREFERENCE_SKIP_LOGIN -> {
                DataStore.writeBooleanAsync(key, !data)
                return data
            }
        }
        return false
    }

}