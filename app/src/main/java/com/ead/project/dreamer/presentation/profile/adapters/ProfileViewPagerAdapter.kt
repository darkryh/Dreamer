package com.ead.project.dreamer.presentation.profile.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ead.project.dreamer.presentation.profile_chapters.ProfileChaptersFragment
import com.ead.project.dreamer.presentation.profile_description.ProfileDescriptionFragment
import javax.annotation.Nonnull

class ProfileViewPagerAdapter(@Nonnull fragmentActivity: FragmentActivity,val id : Int) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileDescriptionFragment().also { it.profileId = id }
            1 -> ProfileChaptersFragment().also { it.profileId = id }
            else -> ProfileDescriptionFragment().also { it.profileId = id }
        }
    }
}