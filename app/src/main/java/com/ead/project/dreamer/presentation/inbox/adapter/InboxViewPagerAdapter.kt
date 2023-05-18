package com.ead.project.dreamer.presentation.inbox.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ead.project.dreamer.presentation.download.DownloadsFragment
import com.ead.project.dreamer.presentation.favorites.FavoriteFragment
import com.ead.project.dreamer.presentation.suggestions.SuggestionsFragment
import javax.annotation.Nonnull

class InboxViewPagerAdapter(@Nonnull fragmentActivity: FragmentActivity, private val viewPager2: ViewPager2) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SuggestionsFragment().apply {
                this.viewPager2 = this@InboxViewPagerAdapter.viewPager2
            }
            1 -> FavoriteFragment()
            2 -> DownloadsFragment()
            else -> SuggestionsFragment()
        }
    }
}