package com.ead.project.dreamer.domain.downloads

import android.app.DownloadManager
import android.app.DownloadManager.Request
import com.ead.project.dreamer.app.data.downloads.DownloadStore
import javax.inject.Inject

class GenerateDownload @Inject constructor(
    private val downloadManager: DownloadManager,
    private val downloadStore: DownloadStore
) {

    operator fun invoke(request : Request, chapterId : Int) : Long {
        val downloadId = downloadManager.enqueue(request)
        downloadStore.addDownload(downloadId = downloadId, chapterId = chapterId)
        return downloadId
    }

    operator fun invoke(request: Request) : Long = downloadManager.enqueue(request)
}