package com.ead.project.dreamer.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.discord.Discord
import com.ead.project.dreamer.data.utils.Thread
import com.ead.project.dreamer.databinding.FragmentDashboardSettingsBinding
import com.ead.project.dreamer.presentation.settings.options.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsDashboardFragment : Fragment() {

    private val discordUser = Discord.getUser()

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

            if (discordUser != null){
                textUserName.text = discordUser.username
                textRank.text = discordUser.all_ranks

                imageAccount.load(discordUser.cdn_avatar?:return){
                    transformations(CircleCropTransformation())
                }
            }
        }
    }

    private fun settingFunctionality() {
        binding.apply {
            containerAccount.setOnClickListener { launchPreferencesCategory(SettingsAccountFragment()) }
            imageAccount.setOnClickListener { launchPreferencesCategory(SettingsAccountFragment()) }
            optionRanks.setOnClickListener {  }
            optionUser.setOnClickListener { launchPreferencesCategory(SettingsAccountFragment()) }
            optionDesign.setOnClickListener { launchPreferencesCategory(SettingsDesignFragment()) }
            optionPlayer.setOnClickListener { launchPreferencesCategory(SettingsPlayerFragment()) }
            optionNotifications.setOnClickListener { launchPreferencesCategory(SettingsNotificationsFragment()) }
            optionContentRating.setOnClickListener { launchPreferencesCategory(SettingsContentRatingFragment()) }
            optionAboutUs.setOnClickListener { launchPreferencesCategory(SettingsAboutUsFragment()) }
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
}