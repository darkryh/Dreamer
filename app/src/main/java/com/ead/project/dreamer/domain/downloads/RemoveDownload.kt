package com.ead.project.dreamer.domain.downloads

import javax.inject.Inject
import android.app.DownloadManager

class RemoveDownload @Inject constructor(
    private val downloadManager: DownloadManager,
    private val removeEnqueueDownload: RemoveEnqueueDownload
) {

    operator fun invoke(downloadId: Long) {
        downloadManager.remove(downloadId)
        removeEnqueueDownload(downloadId)
    }
}