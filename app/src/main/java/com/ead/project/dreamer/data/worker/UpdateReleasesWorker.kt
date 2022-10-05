package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.app.model.scrapping.AnimeProfileScrap
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

class UpdateReleasesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context,workerParameters) {

    lateinit var repository: AnimeRepository
    lateinit var webProvider: WebProvider

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val animeProfileScrap = AnimeProfileScrap.get()?:AnimeProfileScrap.getDataFromApi(repository)
                val repositoryData = repository.getProfileReleases()

                if (DataStore.readBoolean(Constants.PREFERENCE_DIRECTORY_PROFILE)) {
                    for (pos in repositoryData.indices) {
                        val profile = repositoryData[pos]
                        val profileInProgress = async {
                            webProvider.getAnimeProfile(
                                profile.id,
                                profile.reference!!,
                                animeProfileScrap
                            )
                        }
                        profileInProgress.await().apply {
                            reference = profile.reference
                            repository.updateAnimeProfile(this)
                        }
                    }
                }
                Result.success()
            }
            catch (ex : IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }
}