package com.ead.project.dreamer.domain.downloads

import android.app.DownloadManager
import android.app.DownloadManager.Request
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class CreateDownloadRequest @Inject constructor(
    private val downloadManager: DownloadManager
) {

    operator fun invoke(request : Request,id : Int) {
        val idDownload = downloadManager.enqueue(request)
        Chapter.addToDownloadList(Pair(idDownload,id))
    }

    operator fun invoke(request: Request) = downloadManager.enqueue(request)
}