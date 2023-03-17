package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.data.utils.DataStore
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
                val sectionPos = inputData.getInt(Constants.DIRECTORY_KEY, -1)

                if (directory.size <= Constants.HOME_ITEMS_LIMIT) {
                    val directoryData = async { webProvider.requestingData(sectionPos) }
                    directoryData.await().apply {
                        if (isEmpty()) Result.failure()
                        objectUseCase.insertObject(this)
                        final(sectionPos)
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

    private fun final(pos: Int) { if (pos == 1) DataStore.writeBooleanAsync(Constants.FINAL_DIRECTORY,true) }
}