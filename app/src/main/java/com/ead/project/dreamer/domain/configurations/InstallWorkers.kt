package com.ead.project.dreamer.domain.configurations

import androidx.work.*
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.worker.DirectoryWorker
import com.ead.project.dreamer.data.worker.ProfileRepositoryWorker
import javax.inject.Inject

class InstallWorkers @Inject constructor(
    private val workManager: WorkManager,
    private val constraints: Constraints,
) {

    operator fun invoke(workersQuantity : Int = 3) {
        val directoryRequest : MutableList<OneTimeWorkRequest> = ArrayList()
        for (i in 1 until workersQuantity + 1) {

            val data : Data = Data.Builder()
                .putInt(Constants.DIRECTORY_KEY,i)
                .build()

            val syncingChaptersRequest =
                OneTimeWorkRequestBuilder<DirectoryWorker>()
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build()

            directoryRequest.add(syncingChaptersRequest)
        }

        var continuation = workManager.beginUniqueWork(
            Constants.SYNC_DIRECTORY,
            ExistingWorkPolicy.REPLACE,
            directoryRequest)

        val syncingProfilingRequest =
            OneTimeWorkRequestBuilder<ProfileRepositoryWorker>()
                .setConstraints(constraints)
                .build()

        continuation = continuation.then(syncingProfilingRequest)
        continuation.enqueue()
    }
}
