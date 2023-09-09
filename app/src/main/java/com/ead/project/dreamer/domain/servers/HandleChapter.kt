package com.ead.project.dreamer.domain.servers

import android.content.Context
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.presentation.server.menu.MenuServerFragment
import javax.inject.Inject

class HandleChapter @Inject constructor(
    private val launchVideo: LaunchVideo
) {

    operator fun invoke(context: Context, chapter: Chapter) {
        if (chapter.isDownloaded()) {
            launchVideo(context, chapter, null,true)
        }
        else {
            MenuServerFragment.launch(context,chapter,false)
        }
    }
}