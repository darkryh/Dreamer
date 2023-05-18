package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.app.data.home.HomePreferences
import com.ead.project.dreamer.app.data.worker.Worker
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.domain.DirectoryUseCase
import com.ead.project.dreamer.domain.ObjectUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class DirectoryWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val directoryUseCase: DirectoryUseCase,
    private val objectUseCase: ObjectUseCase,
    private val webProvider: WebProvider
) : CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val directory = directoryUseCase.getDirectoryList()
                val workerPosition = inputData.getInt(Worker.DIRECTORY_KEY, -1)

                if (directory.size <= HomePreferences.HOME_ITEM_LIMIT) {
                    val directoryData = async { webProvider.requestingData(workerPosition) }
                    directoryData.await().apply {
                        if (isEmpty()) Result.failure()
                        objectUseCase.insertObject(this)
                        updateDirectoryCompleted(workerPosition)
                        Result.success()
                    }
                }
                Result.success()
            } catch (ex: IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }

    private suspend fun updateDirectoryCompleted(workerPosition: Int) {
        if (workerPosition == 1) {
            directoryUseCase.setDirectoryState(true)
        }
    }
}