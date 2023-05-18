package com.ead.project.dreamer.domain.servers

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.presentation.menuserver.MenuServerFragment
import javax.inject.Inject

class LaunchServer @Inject constructor() {

    private val bundle = Bundle()

    operator fun invoke(context: Context, chapter: Chapter,isDownloadMode : Boolean) {
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        val chapterMenu = MenuServerFragment()
        chapterMenu.apply {
            arguments = bundle.apply {
                clear()
                putParcelable(Chapter.REQUESTED, chapter)
                putBoolean(MenuServerFragment.IS_DATA_FOR_DOWNLOADING_MODE,isDownloadMode)
            }
            show(fragmentManager, MenuServerFragment.FRAGMENT)
        }
    }
}