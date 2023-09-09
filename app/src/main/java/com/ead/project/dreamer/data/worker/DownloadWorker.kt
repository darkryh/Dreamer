package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.data.utils.Thread
import com.ead.project.dreamer.domain.DownloadUseCase
import com.ead.project.dreamer.domain.downloads.DownloadEngine
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val downloadUseCase: DownloadUseCase,
    private val downloadEngine: DownloadEngine
) : CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                if (!downloadUseCase.isInParallelLimit()) Result.success()

                Thread.onUi {
                    if (!downloadEngine.isLoading()) downloadEngine.restart()
                }


                Result.success()
            } catch (ex: IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }
}