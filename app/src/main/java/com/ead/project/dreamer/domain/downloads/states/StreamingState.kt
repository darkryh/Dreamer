package com.ead.project.dreamer.domain.downloads.states

import android.content.Context
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.downloads.DownloadStore
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.system.extensions.error
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.domain.downloads.EnqueueDownload
import com.ead.project.dreamer.domain.downloads.LaunchDownload
import javax.inject.Inject

class StreamingState @Inject constructor(
    private val downloadStore: DownloadStore,
    private val launchDownload: LaunchDownload,
    private val enqueueDownload: EnqueueDownload
) {

    operator fun invoke(context : Context,chapter: Chapter) {
        if (downloadStore.isDownloading(chapter.id)) {
            context.error(context.getString(R.string.warning_chapter_status_in_progress))
            return
        }
        launchDownload(chapter)
    }

    operator fun invoke(context: Context,chapter: Chapter,url: String) {
        if (downloadStore.isDownloading(chapter.id)) {
            context.toast(context.getString(R.string.warning_chapter_status_in_progress))
            return
        }
        enqueueDownload(chapter, url)
    }
}