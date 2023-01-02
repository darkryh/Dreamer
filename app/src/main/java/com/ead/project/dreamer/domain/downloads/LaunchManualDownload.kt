package com.ead.project.dreamer.domain.downloads

import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class LaunchManualDownload @Inject constructor(
    private val downloadEngine: DownloadEngine,
    private val launchDownload: LaunchDownload
) {

    operator fun invoke(chapter : Chapter, url : String) {
        downloadEngine.settingBroadcast()
        launchDownload(chapter, url)
    }

}