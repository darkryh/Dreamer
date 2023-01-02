package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.domain.DirectoryManager
import com.ead.project.dreamer.domain.ObjectManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class ProfileCachingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val directoryManager: DirectoryManager,
    private val objectManager: ObjectManager,
    private val webProvider: WebProvider
) : CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val array = inputData.getStringArray(Constants.ANIME_PROFILE_KEY)!!
                val id = array[0].toInt()
                val reference = array[1]

                val requestedProfile = async { webProvider.getAnimeProfile(id,reference) }
                val animeBase = directoryManager.getDirectory.byId(id)
                requestedProfile.await().apply {
                    this.reference = animeBase.reference
                    objectManager.insertObject(this)
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