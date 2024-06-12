package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.app.data.preference.Settings
import com.ead.project.dreamer.app.repository.Repository
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.domain.DirectoryUseCase
import com.ead.project.dreamer.domain.ObjectUseCase
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.domain.ProfileUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class ProfileRepositoryWorker  @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val directoryUseCase: DirectoryUseCase,
    private val objectUseCase: ObjectUseCase,
    private val profileUseCase: ProfileUseCase,
    private val webProvider: WebProvider,
    preferenceUseCase: PreferenceUseCase
) : CoroutineWorker(context,workerParameters) {

    private val preferences = preferenceUseCase.preferences

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val repositoryData = directoryUseCase.getDirectoryList()
                val repositoryProfile = profileUseCase.getProfileList()

                if (repositoryData.size != repositoryProfile.size) {
                    val currentPos = preferences.getInt(Repository.PROFILES)
                    for (pos in currentPos until repositoryData.size) {
                        val animeBase = repositoryData[pos]
                        val profile = async {
                            webProvider.getAnimeProfile(
                                animeBase.id,
                                animeBase.reference,
                                context
                            )
                        }
                        profile.await().apply {
                            objectUseCase.insertObject(copy(
                                reference = repositoryData[pos].reference
                            ))
                            preferences.set(Repository.PROFILES,pos)
                        }
                    }
                    preferences.set(Settings.SYNC_DIRECTORY_PROFILE,true)
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