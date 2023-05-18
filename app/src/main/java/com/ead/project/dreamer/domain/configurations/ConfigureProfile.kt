package com.ead.project.dreamer.domain.configurations

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import com.ead.project.dreamer.app.data.worker.Worker
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.AnimeProfile
import javax.inject.Inject

class ConfigureProfile @Inject constructor(
    private val repository: AnimeRepository,
    private val launchOneTimeRequest: LaunchOneTimeRequest,
) {

    suspend operator fun invoke (id : Int,reference: String) {
        val animeProfile : AnimeProfile? = repository.getAnimeProfile(id)
        if (animeProfile == null) cachingProfile(id,reference)
    }

    operator fun invoke(animeProfile: AnimeProfile?,id : Int,reference: String) {
        if (animeProfile == null) cachingProfile(id,reference)
    }

    private fun cachingProfile(id : Int, reference : String) {
        val array = arrayOf(id.toString(), reference)

        val data: Data = Data.Builder()
            .putStringArray(Worker.ANIME_PROFILE_KEY, array)
            .build()

        launchOneTimeRequest(
            LaunchOneTimeRequest.ProfileCachingWorkerCode,
            Worker.SYNC_PROFILE_CHECKER,
            ExistingWorkPolicy.KEEP,
            data
        )
    }
}