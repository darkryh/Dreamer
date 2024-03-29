package com.ead.project.dreamer.presentation.inbox.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ead.project.dreamer.presentation.download.DownloadsFragment
import com.ead.project.dreamer.presentation.favorites.FavoriteFragment
import com.ead.project.dreamer.presentation.suggestions.SuggestionsFragment
import javax.annotation.Nonnull

class InboxViewPagerAdapter(@Nonnull fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SuggestionsFragment()
            1 -> FavoriteFragment()
            2 -> DownloadsFragment()
            else -> SuggestionsFragment()
        }
    }
}