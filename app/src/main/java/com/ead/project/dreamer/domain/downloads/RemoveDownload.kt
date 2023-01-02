package com.ead.project.dreamer.domain.downloads

import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class RemoveDownload @Inject constructor(
    private val downloadManager: android.app.DownloadManager
) {

    operator fun invoke(data : Pair<Long,Int>?) {
        data?.let {
            downloadManager.remove(it.first)
            Chapter.removeFromDownloadList(it)
        }
    }
}