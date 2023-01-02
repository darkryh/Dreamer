package com.ead.project.dreamer.domain.downloads

import android.app.DownloadManager
import android.database.Cursor
import javax.inject.Inject

class GetCursorFromDownloads @Inject constructor(
    private val downloadManager: DownloadManager
) {

    operator fun invoke() : Cursor = downloadManager.query(
        DownloadManager.Query().setFilterByStatus(
            DownloadManager.STATUS_SUCCESSFUL
                    or DownloadManager.STATUS_RUNNING
                    or DownloadManager.STATUS_PENDING
                    or DownloadManager.STATUS_PAUSED
    ))
}