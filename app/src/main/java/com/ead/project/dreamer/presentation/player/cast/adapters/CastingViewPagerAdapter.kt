package com.ead.project.dreamer.presentation.player.cast.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.presentation.player.cast.CastingChaptersFragment
import javax.annotation.Nonnull

class CastingViewPagerAdapter (@Nonnull fragmentActivity: FragmentActivity,
                               private val chapterSender: Chapter) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 1

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CastingChaptersFragment().apply {
                this.chapter = chapterSender
            }
            else -> CastingChaptersFragment().apply {
                this.chapter = chapterSender
            }
        }
    }
}