package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.domain.ObjectManager
import com.ead.project.dreamer.domain.ProfileManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class UpdateReleasesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val objectManager: ObjectManager,
    private val profileManager: ProfileManager,
    private val webProvider: WebProvider
) : CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val repositoryData = profileManager.getProfilesReleases()

                if (DataStore.readBoolean(Constants.PREFERENCE_DIRECTORY_PROFILE)) {
                    for (pos in repositoryData.indices) {
                        val profile = repositoryData[pos]
                        val profileInProgress = async {
                            webProvider.getAnimeProfile(
                                profile.id,
                                profile.reference!!,
                            )
                        }
                        profileInProgress.await().apply {
                            reference = profile.reference
                            objectManager.updateObject(this)
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