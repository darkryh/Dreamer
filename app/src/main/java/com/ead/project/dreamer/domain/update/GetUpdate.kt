package com.ead.project.dreamer.domain.update

import androidx.lifecycle.LiveData
import com.ead.project.dreamer.app.data.downloads.DownloadStore
import com.ead.project.dreamer.data.models.Download
import javax.inject.Inject

class GetUpdate @Inject constructor(
    private val downloadStore: DownloadStore
) {

    operator fun invoke(downloadId : Long) : LiveData<Download?> {
        return downloadStore.getDownload(downloadId)
    }
}