package com.ead.project.dreamer.domain.servers

import android.content.Context
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class HandleChapter @Inject constructor(
    private val launchServer: LaunchServer,
    private val launchVideo: LaunchVideo
) {

    operator fun invoke(context: Context, chapter: Chapter) {
        if (chapter.isDownloaded()) {
            launchVideo(context, chapter,true)
        }
        else {
            launchServer(context, chapter,false)
        }
    }
}