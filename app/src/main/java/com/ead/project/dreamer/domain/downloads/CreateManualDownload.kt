package com.ead.project.dreamer.domain.downloads

import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class CreateManualDownload @Inject constructor(
    private val downloadEngine: DownloadEngine,
    private val createDownload: CreateDownload
) {

    operator fun invoke(chapter : Chapter, url : String) {
        downloadEngine.settingBroadcast()
        createDownload(chapter, url)
    }

}