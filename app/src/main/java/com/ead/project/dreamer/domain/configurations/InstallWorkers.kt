package com.ead.project.dreamer.domain.configurations

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ead.project.dreamer.app.data.worker.Worker
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
                .putInt(Worker.DIRECTORY_KEY,i)
                .build()

            val syncingChaptersRequest =
                OneTimeWorkRequestBuilder<DirectoryWorker>()
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build()

            directoryRequest.add(syncingChaptersRequest)
        }

        var continuation = workManager.beginUniqueWork(
            Worker.SYNC_DIRECTORY,
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
