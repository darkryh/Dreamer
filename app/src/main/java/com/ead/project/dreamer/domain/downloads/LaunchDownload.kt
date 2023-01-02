package com.ead.project.dreamer.domain.downloads

import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class LaunchDownload @Inject constructor(
    private val createDownloadRequest: CreateDownloadRequest,
    private val configureDownloadRequest: ConfigureDownloadRequest
) {

    operator fun invoke(chapter : Chapter?,url : String) =
        chapter?.let { createDownloadRequest(configureDownloadRequest(it,url),it.id)  }

    operator fun invoke(title: String, url: String) {
        createDownloadRequest(configureDownloadRequest(title, url))
    }
}