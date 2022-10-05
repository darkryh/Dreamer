package com.ead.project.dreamer.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.app.model.scrapping.AnimeBaseScrap
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
    //private lateinit var output : Data

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val animeBaseScrap = AnimeBaseScrap.get()?:AnimeBaseScrap.getDataFromApi(repository)
                val directory = repository.getDirectory()
                val sectionPos = inputData.getInt(Constants.DIRECTORY_KEY, -1)
                if (directory.size <= Constants.HOME_ITEMS_LIMIT) {
                    val directoryData = async { webProvider.requestingData(sectionPos,animeBaseScrap) }
                    directoryData.await().apply {
                        if (isEmpty()) Result.failure()
                        repository.insertAllAnimeBase(this)
                        final(sectionPos)
                        Result.success()
                    }
                }
                Result.success()
            } catch (ex: IOException) {
                ex.printStackTrace()
                Log.d("testing", "doWork: ${ex.cause}")
                Result.failure()
            }
        }
    }

    //private fun outputValue(value : Boolean) = workDataOf(Pair(Constants.RESULT_DIRECTORY_WORKER,value))

    private fun final(pos: Int) { if (pos == 1) DataStore.writeBooleanAsync(Constants.FINAL_DIRECTORY,true) }
}