package com.ead.project.dreamer.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.data.utils.Thread
import com.ead.project.dreamer.databinding.FragmentDashboardSettingsBinding
import com.ead.project.dreamer.presentation.settings.options.SettingsAboutUsFragment
import com.ead.project.dreamer.presentation.settings.options.SettingsAccountFragment
import com.ead.project.dreamer.presentation.settings.options.SettingsContentRatingFragment
import com.ead.project.dreamer.presentation.settings.options.SettingsDesignFragment
import com.ead.project.dreamer.presentation.settings.options.SettingsNotificationsFragment
import com.ead.project.dreamer.presentation.settings.options.SettingsPlayerFragment
import com.ead.project.dreamer.presentation.settings.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsDashboardFragment : Fragment() {

    private val viewModel : SettingsViewModel by viewModels()
    private var _binding : FragmentDashboardSettingsBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardSettingsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingLayout()
        settingFunctionality()
        observeAccount()
    }

    private fun settingLayout() {
        binding.apply {

            containerAccount.addSelectableItemEffect()
            optionUser.addSelectableItemEffect()
            optionRanks.addSelectableItemEffect()
            optionDesign.addSelectableItemEffect()
            optionPlayer.addSelectableItemEffect()
            optionNotifications.addSelectableItemEffect()
            optionContentRating.addSelectableItemEffect()
            optionAboutUs.addSelectableItemEffect()
        }
    }

    private fun settingFunctionality() {
        binding.apply {
            imageLinkdin.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(LINKDIN))
                startActivity(intent)
            }

            containerAccount.setOnClickListener { launchPreferencesCategory(SettingsAccountFragment()) }
            imageAccount.setOnClickListener { launchPreferencesCategory(SettingsAccountFragment()) }
            optionRanks.setOnClickListener { toast("disponible en proximas versiones.") }
            optionUser.setOnClickListener { launchPreferencesCategory(SettingsAccountFragment()) }
            optionDesign.setOnClickListener { launchPreferencesCategory(SettingsDesignFragment()) }
            optionPlayer.setOnClickListener { launchPreferencesCategory(SettingsPlayerFragment()) }
            optionNotifications.setOnClickListener { launchPreferencesCategory(SettingsNotificationsFragment()) }
            optionContentRating.setOnClickListener { launchPreferencesCategory(SettingsContentRatingFragment()) }
            optionAboutUs.setOnClickListener { launchPreferencesCategory(SettingsAboutUsFragment()) }
        }
    }

    private fun observeAccount() {
        lifecycleScope.launch {
            viewModel.getAccount().collectLatest { eadAccount ->
                if (eadAccount == null) return@collectLatest

                binding.apply {
                    textUserName.text = eadAccount.displayName
                    textRank.text = eadAccount.ranksNames.toString()

                    imageAccount.load(eadAccount.profileImage?:return@collectLatest){
                        transformations(CircleCropTransformation())
                    }
                }
            }
        }
    }

    private fun launchPreferencesCategory(requestedFragment : Fragment) {
        Thread.onClickEffect {
            val transaction = requireActivity().supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)

            transaction.replace(R.id.frame_content_settings, requestedFragment).commit()
        }
    }

    companion object {
        const val LINKDIN = "https://www.linkedin.com/in/xavier-alexander-torres-calder%C3%B3n-632798212/"
    }
}