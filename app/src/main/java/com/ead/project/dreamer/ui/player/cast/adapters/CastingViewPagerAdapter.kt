package com.ead.project.dreamer.ui.player.cast.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.ui.player.cast.CastingChapterFragment
import com.ead.project.dreamer.ui.player.cast.CastingChapterListFragment
import javax.annotation.Nonnull

class CastingViewPagerAdapter (@Nonnull fragmentActivity: FragmentActivity,
                               private val chapterSender: Chapter) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CastingChapterFragment().apply {
                this.chapter = chapterSender
            }
            1 -> CastingChapterListFragment().apply {
                this.chapter = chapterSender
            }
            else -> CastingChapterFragment().apply{
                this.chapter = chapterSender
            }
        }
    }
}