package com.ead.project.dreamer.domain.configurations

import androidx.work.*
import com.ead.project.dreamer.data.worker.*
import javax.inject.Inject

class LaunchOneTimeRequest @Inject constructor(
    private val workManager: WorkManager,
    private val constraints: Constraints,
) {

    companion object {
        const val ChaptersCachingWorkerCode = 0
        const val DirectoryWorkerCode = 1
        const val FixerChaptersCachingWorkerCode = 2
        const val FixerProfileCachingWorkerCode = 3
        const val HomeWorkerCode = 4
        const val NewContentWorkerCode = 5
        const val NewsWorkerCode = 6
        const val ProfileCachingWorkerCode = 7
        const val ProfileRepositoryWorkerCode = 8
        const val ScrapperWorkerCode = 9
        const val UpdateReleasesWorkerCode = 10
    }

    operator fun invoke(
        op: Int,
        stringCode: String,
        existingWorkPolicy: ExistingWorkPolicy,
        data: Data? = null
    ) {
        val request = getRequest(op).setConstraints(constraints)
        if (data != null) request.setInputData(data)
        workManager.enqueueUniqueWork(
            stringCode,
            existingWorkPolicy,
            request.build()
        )
    }

    private fun getRequest(op: Int): OneTimeWorkRequest.Builder {
        return when (op) {
            ChaptersCachingWorkerCode -> OneTimeWorkRequestBuilder<ChaptersCachingWorker>()
            DirectoryWorkerCode -> OneTimeWorkRequestBuilder<DirectoryWorker>()
            FixerChaptersCachingWorkerCode -> OneTimeWorkRequestBuilder<FixerChaptersCachingWorker>()
            FixerProfileCachingWorkerCode -> OneTimeWorkRequestBuilder<FixerProfileCachingWorker>()
            HomeWorkerCode -> OneTimeWorkRequestBuilder<HomeWorker>()
            NewContentWorkerCode -> OneTimeWorkRequestBuilder<NewContentWorker>()
            NewsWorkerCode -> OneTimeWorkRequestBuilder<NewsWorker>()
            ProfileCachingWorkerCode -> OneTimeWorkRequestBuilder<ProfileCachingWorker>()
            ProfileRepositoryWorkerCode -> OneTimeWorkRequestBuilder<ProfileRepositoryWorker>()
            ScrapperWorkerCode -> OneTimeWorkRequestBuilder<ScrapperWorker>()
            UpdateReleasesWorkerCode -> OneTimeWorkRequestBuilder<UpdateReleasesWorker>()
            else -> OneTimeWorkRequestBuilder<DirectoryWorker>()
        }
    }
}