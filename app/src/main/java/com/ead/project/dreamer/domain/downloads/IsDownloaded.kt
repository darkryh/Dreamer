package com.ead.project.dreamer.domain.downloads

import android.app.DownloadManager
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class IsDownloaded @Inject constructor(
    private val isInDownloadManagerProgress: IsInDownloadManagerProgress
) {

    operator fun invoke(chapter: Chapter) =
        chapter.isDownloaded() && isInDownloadManagerProgress(chapter,DownloadManager.STATUS_SUCCESSFUL.toLong())
}