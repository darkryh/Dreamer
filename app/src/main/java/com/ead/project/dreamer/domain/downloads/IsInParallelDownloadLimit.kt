package com.ead.project.dreamer.domain.downloads

import com.ead.project.dreamer.app.data.downloads.DownloadStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class IsInParallelDownloadLimit @Inject constructor(
    private val downloadStore: DownloadStore
) {

    suspend operator fun invoke() : Boolean = downloadStore.runningDownloads.first().count() <= 3

}