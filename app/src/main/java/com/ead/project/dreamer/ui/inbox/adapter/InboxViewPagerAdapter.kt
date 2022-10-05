package com.ead.project.dreamer.ui.inbox.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ead.project.dreamer.ui.favorites.FavoriteFragment
import com.ead.project.dreamer.ui.suggestions.SuggestionsFragment
import javax.annotation.Nonnull

class InboxViewPagerAdapter(@Nonnull fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SuggestionsFragment()
            1 -> FavoriteFragment()
            else -> SuggestionsFragment()
        }
    }
}