package com.ead.project.dreamer.domain.downloads

import javax.inject.Inject
import android.app.DownloadManager
import com.ead.project.dreamer.app.data.downloads.DownloadStore

class RemoveDownload @Inject constructor(
    private val downloadManager: DownloadManager,
    private val downloadStore: DownloadStore
) {

    operator fun invoke(downloadId: Long) {
        downloadStore.removeDownload(downloadId)
        downloadManager.remove(downloadId)
    }

    operator fun invoke(chapterId : Int) {
        downloadManager.remove(downloadStore.removeDownload(chapterId))
    }
}