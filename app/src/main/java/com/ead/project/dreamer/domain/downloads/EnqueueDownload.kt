package com.ead.project.dreamer.domain.downloads

import com.ead.project.dreamer.app.data.downloads.DownloadStore
import javax.inject.Inject

class EnqueueDownload @Inject constructor(
    private val downloadStore: DownloadStore
) {

    operator fun invoke(downloadId : Long, chapterId : Int) {
        downloadStore.addDownload(downloadId, chapterId)
    }
}
