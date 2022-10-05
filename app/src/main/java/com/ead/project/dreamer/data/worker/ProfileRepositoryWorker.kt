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

class ProfileRepositoryWorker  @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context,workerParameters) {

    lateinit var repository: AnimeRepository
    lateinit var webProvider: WebProvider

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val animeProfileScrap = AnimeProfileScrap.get()?:AnimeProfileScrap.getDataFromApi(repository)
                val repositoryData = repository.getDirectory()
                val repositoryProfile = repository.getProfileList()

                if (repositoryData.size != repositoryProfile.size) {
                    val currentPos = DataStore.readInt(Constants.PROFILE_REPOSITORY)
                    for (pos in currentPos until repositoryData.size) {
                        val animeBase = repositoryData[pos]
                        val profile = async {
                            webProvider.getAnimeProfile(
                                animeBase.id,
                                animeBase.reference,
                                animeProfileScrap
                            )
                        }
                        profile.await().apply {
                            reference = repositoryData[pos].reference
                            repository.insertProfile(this)
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