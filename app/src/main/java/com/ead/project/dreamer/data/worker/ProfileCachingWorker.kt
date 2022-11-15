package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.app.model.scrapping.AnimeProfileScrap
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.network.WebProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

class ProfileCachingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context,workerParameters) {

    lateinit var repository: AnimeRepository
    lateinit var webProvider: WebProvider

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val animeProfileScrap = AnimeProfileScrap.get()?:AnimeProfileScrap.getDataFromApi(repository)
                val array = inputData.getStringArray(Constants.ANIME_PROFILE_KEY)!!
                val id = array[0].toInt()
                val reference = array[1]
                val requestedProfile = async { webProvider.getAnimeProfile(id,reference,animeProfileScrap) }
                val animeBase = repository.getAnimeBaseById(id)
                requestedProfile.await().apply {
                    this.reference = animeBase.reference
                    repository.insertProfile(this)
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