package com.ead.project.dreamer.domain.downloads

import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import com.ead.project.dreamer.app.data.worker.Worker
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.domain.configurations.LaunchPeriodicTimeRequest
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LaunchDownload @Inject constructor(val launchPeriodicTimeRequest: LaunchPeriodicTimeRequest) {
    operator fun invoke(chapter: Chapter) {

        val data: Data = Data.Builder()
            .putInt(Worker.DOWNLOAD_CHAPTER_KEY,chapter.id)
            .build()


        launchPeriodicTimeRequest(
            LaunchPeriodicTimeRequest.DownloadWorker,
            5,
            TimeUnit.MINUTES,
            Worker.SYNC_DOWNLOADS,
            ExistingPeriodicWorkPolicy.UPDATE,
            data
        )
    }
}