package com.ead.project.dreamer.domain.downloads

import android.app.DownloadManager
import android.app.DownloadManager.Request
import javax.inject.Inject

class CreateDownloadRequest @Inject constructor(
    private val downloadManager: DownloadManager,
    private val enqueueDownload: EnqueueDownload
) {

    operator fun invoke(request : Request, chapterId : Int) {
        val downloadId = downloadManager.enqueue(request)
        enqueueDownload(downloadId = downloadId, chapterId = chapterId)
    }

    operator fun invoke(request: Request) = downloadManager.enqueue(request)
}