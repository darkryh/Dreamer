package com.ead.project.dreamer.domain.downloads

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.presentation.chapter.settings.ChapterSettingsFragment
import javax.inject.Inject

class LaunchDownload @Inject constructor() {

    private val bundle = Bundle()

    operator fun invoke(context: Context, chapter: Chapter,isChapter : Boolean) {
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        val chapterSettings = ChapterSettingsFragment()
        chapterSettings.apply {
            arguments = bundle.apply {
                clear()
                putParcelable(Chapter.REQUESTED, chapter)
                putBoolean(ChapterSettingsFragment.IS_INSTANCE_A_CHAPTER,isChapter)
            }
            show(fragmentManager, ChapterSettingsFragment.FRAGMENT)
        }
    }
}