package com.ead.project.dreamer.domain.downloads

import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class AddDownload @Inject constructor(
    private val getTempDownloads: GetTempDownloads,
    private val downloadEngine: DownloadEngine
) {

    operator fun invoke(chapters : List<Chapter>)  {
        getTempDownloads().addAll(chapters)
        settingEngine()
    }

    operator fun invoke(chapter: Chapter) {
        getTempDownloads().add(chapter)
        settingEngine()
    }

    private fun settingEngine() { if (downloadEngine.isNotWorking()) downloadEngine() }
}