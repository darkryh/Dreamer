package com.ead.project.dreamer.domain.configurations

import androidx.work.*
import com.ead.project.dreamer.data.worker.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LaunchPeriodicTimeRequest @Inject constructor(
    private val workManager: WorkManager,
    private val constraints: Constraints,
) {

    companion object {
        const val ChaptersCachingWorkerCode = 0
        const val DirectoryWorkerCode = -1
        const val FixerChaptersCachingWorkerCode = -2
        const val FixerProfileCachingWorkerCode = -3
        const val HomeWorkerCode = -4
        const val NewContentWorkerCode = -5
        const val NewsWorkerCode = -6
        const val ProfileCachingWorkerCode = -7
        const val ProfileRepositoryWorkerCode = -8
        const val ScrapperWorkerCode = -9
        const val UpdateReleasesWorkerCode = -10
        const val DownloadWorker = -11
    }

    operator fun invoke(
        op: Int,
        interval: Long,
        timeUnit: TimeUnit,
        stringCode: String,
        existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy,
        data: Data? = null
    ) {
        val request = getRequest(op, interval, timeUnit).setConstraints(constraints)
        if (data != null) request.setInputData(data)
        workManager.enqueueUniquePeriodicWork(
            stringCode,
            existingPeriodicWorkPolicy,
            request.build()
        )
    }

    private fun getRequest(op: Int,interval : Long,timeUnit: TimeUnit): PeriodicWorkRequest.Builder {
        return when (op) {
            ChaptersCachingWorkerCode -> PeriodicWorkRequestBuilder<ChaptersCachingWorker>(interval, timeUnit)
            DirectoryWorkerCode -> PeriodicWorkRequestBuilder<DirectoryWorker>(interval, timeUnit)
            FixerChaptersCachingWorkerCode -> PeriodicWorkRequestBuilder<FixerChaptersCachingWorker>(interval, timeUnit)
            FixerProfileCachingWorkerCode -> PeriodicWorkRequestBuilder<FixerProfileCachingWorker>(interval, timeUnit)
            HomeWorkerCode -> PeriodicWorkRequestBuilder<HomeWorker>(interval, timeUnit)
            NewContentWorkerCode -> PeriodicWorkRequestBuilder<NewContentWorker>(interval, timeUnit)
            NewsWorkerCode -> PeriodicWorkRequestBuilder<NewsWorker>(interval, timeUnit)
            ProfileCachingWorkerCode -> PeriodicWorkRequestBuilder<ProfileCachingWorker>(interval, timeUnit)
            ProfileRepositoryWorkerCode -> PeriodicWorkRequestBuilder<ProfileRepositoryWorker>(interval, timeUnit)
            ScrapperWorkerCode -> PeriodicWorkRequestBuilder<ScrapperWorker>(interval, timeUnit)
            UpdateReleasesWorkerCode -> PeriodicWorkRequestBuilder<UpdateReleasesWorker>(interval, timeUnit)
            DownloadWorker -> PeriodicWorkRequestBuilder<DownloadWorker>(interval,timeUnit)
            else -> PeriodicWorkRequestBuilder<DirectoryWorker>(interval, timeUnit)
        }
    }
}