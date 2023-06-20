package com.ead.project.dreamer.domain.configurations

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import com.ead.project.dreamer.app.data.worker.Worker
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.AnimeProfile
import javax.inject.Inject

class ConfigureChapters @Inject constructor(
    private val repository: AnimeRepository,
    private val launchOneTimeRequest: LaunchOneTimeRequest,
) {

    suspend operator fun invoke(id : Int,reference: String,byPassFinalState : Boolean = false) {
        val dataList : List<Int> = repository.getPreparationProfile(id)
        var animeProfile : AnimeProfile = repository.getAnimeProfile(id)?:return
        if (dataList.size >= 2) {
            animeProfile = animeProfile.copy(
                size = dataList[0],
                lastChapterId = dataList[1],
                reference = reference
            )
        }
        if (cachingChaptersTrigger(animeProfile,byPassFinalState)) cachingChapters(
            animeProfile.id,
            animeProfile.reference?:return,
            animeProfile.size - dataList[0],
            animeProfile.lastChapterId
        )
        repository.updateAnimeProfile(animeProfile)
    }

    private fun cachingChaptersTrigger (animeProfile: AnimeProfile,byPassFinalState: Boolean) =
        animeProfile.lastChapterId == 0 ||
        animeProfile.state != AnimeProfile.PROFILE_FINAL_STATE ||
        byPassFinalState

    private fun cachingChapters(id : Int, reference : String, size : Int, lastChapterId : Int) {
        val array = arrayOf(id.toString(),size.toString(),reference,lastChapterId.toString())

        val data = Data.Builder()
            .putStringArray(Worker.CHAPTER_PROFILE_KEY,array)
            .build()

        launchOneTimeRequest(
            LaunchOneTimeRequest.ChaptersCachingWorkerCode,
            Worker.SYNC_CHAPTER_SIZE,
            ExistingWorkPolicy.KEEP,
            data
        )
    }
}