package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.data.utils.DataStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

class DirectoryWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context,workerParameters) {

    lateinit var repository: AnimeRepository
    lateinit var webProvider: WebProvider

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val directory = repository.getDirectory()
                val sectionPos = inputData.getInt(Constants.DIRECTORY_KEY, -1)
                if (directory.size <= 25) {
                    val directoryData = async { webProvider.requestingData(sectionPos) }
                    directoryData.await().apply {
                        repository.insertAllAnimeBase(this)
                        final(sectionPos)
                        Result.success()
                    }
                }
                Result.success()
            } catch (ex: IOException) {
                ex.printStackTrace()
                Result.retry()
            }
        }
    }

    private fun final(pos: Int) {
        if (pos == 1) DataStore.writeBooleanAsync(Constants.FINAL_DIRECTORY,true)
    }
}