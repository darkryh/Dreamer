package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.app.model.scrapping.AnimeProfileScrap
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.domain.DirectoryManager
import com.ead.project.dreamer.domain.ObjectManager
import com.ead.project.dreamer.domain.ProfileManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class FixerProfileCachingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val directoryManager: DirectoryManager,
    private val objectManager: ObjectManager,
    private val profileManager: ProfileManager,
    private val webProvider: WebProvider
) : CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val animeProfileScrap = profileManager.getProfileScrap.fromApi()
                if (AnimeProfileScrap.get() == animeProfileScrap) return@withContext Result.success()

                AnimeProfileScrap.set(animeProfileScrap)
                val profilesToFix = profileManager.getProfilesToFix()

                for (profile in profilesToFix) {
                    val animeBase = directoryManager.getDirectory.byId(profile.id)
                    val requestedAnimeProfile = async { webProvider.getAnimeProfile(profile.id,animeBase.reference) }
                    requestedAnimeProfile.await().apply {
                        this.reference = animeBase.reference
                        objectManager.updateObject(this)
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