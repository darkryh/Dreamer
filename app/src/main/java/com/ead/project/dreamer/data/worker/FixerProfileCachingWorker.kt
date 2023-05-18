package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.domain.DirectoryUseCase
import com.ead.project.dreamer.domain.ObjectUseCase
import com.ead.project.dreamer.domain.ProfileUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class FixerProfileCachingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val directoryUseCase: DirectoryUseCase,
    private val objectUseCase: ObjectUseCase,
    private val profileUseCase: ProfileUseCase,
    private val webProvider: WebProvider
) : CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                /*val animeProfileScrap = profileUseCase.getProfileScrap.fromApi()
                if (AnimeProfileScrap.get() == animeProfileScrap) return@withContext Result.success()

                AnimeProfileScrap.set(animeProfileScrap)
                val profilesToFix = profileUseCase.getProfilesToFix()

                for (profile in profilesToFix) {
                    val animeBase = directoryUseCase.getDirectory.byId(profile.id)
                    val requestedAnimeProfile = async { webProvider.getAnimeProfile(profile.id,animeBase.reference) }
                    requestedAnimeProfile.await().apply {
                        this.reference = animeBase.reference
                        objectUseCase.updateObject(this)
                    }
                }*/
                /*Constants.setProfileFixer(true)*/
                return@withContext Result.success()
            }
            catch (ex : IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }
}