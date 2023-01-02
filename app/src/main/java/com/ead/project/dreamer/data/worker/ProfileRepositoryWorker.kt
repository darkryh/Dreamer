package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.data.utils.DataStore
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
class ProfileRepositoryWorker  @AssistedInject constructor(
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
                val repositoryData = directoryManager.getDirectoryList()
                val repositoryProfile = profileManager.getProfileList()

                if (repositoryData.size != repositoryProfile.size) {
                    val currentPos = DataStore.readInt(Constants.PROFILE_REPOSITORY)
                    for (pos in currentPos until repositoryData.size) {
                        val animeBase = repositoryData[pos]
                        val profile = async {
                            webProvider.getAnimeProfile(
                                animeBase.id,
                                animeBase.reference
                            )
                        }
                        profile.await().apply {
                            reference = repositoryData[pos].reference
                            objectManager.insertObject(this)
                            DataStore.writeIntAsync(Constants.PROFILE_REPOSITORY,pos)
                        }
                    }
                    DataStore.writeBooleanAsync(Constants.PREFERENCE_DIRECTORY_PROFILE,true)
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