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

class FixerProfileCachingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context,workerParameters) {

    lateinit var repository: AnimeRepository
    lateinit var webProvider: WebProvider

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val animeProfileScrap = repository.getAnimeProfileScrap()
                if (AnimeProfileScrap.get() == animeProfileScrap) return@withContext Result.success()
                else AnimeProfileScrap.set(animeProfileScrap)

                val profilesToFix = repository.getProfilesToFix()

                for (profile in profilesToFix) {
                    val animeBase = repository.getAnimeBaseById(profile.id)
                    val requestedAnimeProfile = async { webProvider.getAnimeProfile(profile.id,animeBase.reference,animeProfileScrap) }
                    requestedAnimeProfile.await().apply {
                        this.reference = animeBase.reference
                        repository.updateAnimeProfile(this)
                    }
                }
                Constants.setProfileFixer(true)
                return@withContext Result.success()
            }
            catch (ex : IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }
}