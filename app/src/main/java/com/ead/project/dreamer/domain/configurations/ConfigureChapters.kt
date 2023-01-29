package com.ead.project.dreamer.domain.configurations

import androidx.work.*
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.AnimeProfile
import javax.inject.Inject

class ConfigureChapters @Inject constructor(
    private val repository: AnimeRepository,
    private val launchOneTimeRequest: LaunchOneTimeRequest,
) {

    suspend operator fun invoke(id : Int,reference: String,byPassFinalState : Boolean = false) {
        val dataList : List<Int> = repository.getPreparationProfile(id)
        val animeProfile : AnimeProfile = repository.getAnimeProfile(id)!!
        if (dataList.size >= 2) {
            animeProfile.size = dataList[0]
            animeProfile.lastChapterId = dataList[1]
            animeProfile.reference = reference
        }
        if (cachingChaptersTrigger(animeProfile,byPassFinalState)) cachingChapters(
            animeProfile.id,
            animeProfile.reference?:"null",
            animeProfile.size - dataList[0],
            animeProfile.lastChapterId
        )
        repository.updateAnimeProfile(animeProfile)
    }

    private fun cachingChaptersTrigger (animeProfile: AnimeProfile,byPassFinalState: Boolean) =
        animeProfile.lastChapterId == 0 ||
        animeProfile.state != Constants.PROFILE_FINAL_STATE ||
        byPassFinalState

    private fun cachingChapters(id : Int, reference : String, size : Int, lastChapterId : Int) {
        val array = arrayOf(id.toString(),size.toString(),reference,lastChapterId.toString())

        val data = Data.Builder()
            .putStringArray(Constants.CHAPTER_PROFILE_KEY,array)
            .build()

        launchOneTimeRequest(
            LaunchOneTimeRequest.ChaptersCachingWorkerCode,
            Constants.SYNC_CHAPTER_SIZE,
            ExistingWorkPolicy.KEEP,
            data
        )
    }
}