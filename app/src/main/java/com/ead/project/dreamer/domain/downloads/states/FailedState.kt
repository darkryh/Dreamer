package com.ead.project.dreamer.domain.downloads.states

import android.content.Context
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.system.extensions.error
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.domain.downloads.EnqueueDownload
import com.ead.project.dreamer.domain.downloads.LaunchDownload
import com.ead.project.dreamer.domain.downloads.RemoveDownload
import javax.inject.Inject

class FailedState @Inject constructor(
    private val removeDownload: RemoveDownload,
    private val launchDownload: LaunchDownload,
    private val enqueueDownload: EnqueueDownload
) {

    operator fun invoke(context: Context, chapter: Chapter){
        removeDownload(chapter.id)
        context.error(context.getString(R.string.warning_chapter_status_failed))
        launchDownload(chapter)
    }

    operator fun invoke(context: Context, chapter: Chapter,url : String){
        removeDownload(chapter.id)
        context.toast(context.getString(R.string.warning_chapter_status_failed))
        enqueueDownload(chapter, url)
    }
}