package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
class ProfileCachingWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val directoryUseCase: DirectoryUseCase,
    private val objectUseCase: ObjectUseCase,
    private val webProvider: WebProvider
) : CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val array = inputData.getStringArray(Worker.ANIME_PROFILE_KEY)!!
                val id = array[0].toInt()
                val reference = array[1]

                val requestedProfile = async { webProvider.getAnimeProfile(id ,reference, context) }
                val animeBase = directoryUseCase.getDirectory.byId(id)
                requestedProfile.await().apply {
                    objectUseCase.insertObject(copy(
                        reference = animeBase.reference
                    ))
                    Result.success()
                }
                Result.failure()
            }
            catch (ex : IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }
}