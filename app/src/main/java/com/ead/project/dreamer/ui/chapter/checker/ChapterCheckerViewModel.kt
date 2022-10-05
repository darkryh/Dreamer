package com.ead.project.dreamer.ui.chapter.checker

import androidx.lifecycle.*
import androidx.work.*
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.worker.ChaptersCachingWorker
import com.ead.project.dreamer.data.worker.ProfileCachingWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChapterCheckerViewModel @Inject constructor(
    private val repository: AnimeRepository,
    private val workManager: WorkManager,
    private val constraints: Constraints
): ViewModel() {

    fun getChapter(chapter : Chapter) = repository
        .getFlowChapterFromTitleAndNumber(chapter.title,chapter.chapterNumber).asLiveData()

    fun getAnimeBase(title : String) = repository.getFlowAnimeBaseFromTitle(title).asLiveData()

    fun getAnimeProfile(id : Int) = repository.getFlowAnimeProfile(id).asLiveData()

    fun getChaptersFromProfile (id : Int) : LiveData<List<Chapter>> =
        repository.getFlowChaptersFromProfile(id).asLiveData()

    fun cachingProfile(id : Int, reference : String) {

        val array = arrayOf(id.toString(), reference)

        val data: Data = Data.Builder()
            .putStringArray(Constants.ANIME_PROFILE_KEY, array)
            .build()

        val cachingProfile =  OneTimeWorkRequestBuilder<ProfileCachingWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            Constants.SYNC_PROFILE_CHECKER,
            ExistingWorkPolicy.KEEP,
            cachingProfile)
    }

    fun cachingChapters(id : Int, reference : String, size : Int,lastChapterId : Int) {

        val array = arrayOf(id.toString(),size.toString(),reference,lastChapterId.toString())

        val data = Data.Builder()
            .putStringArray(Constants.CHAPTER_PROFILE_KEY,array)
            .build()


        val syncingChaptersRequest = OneTimeWorkRequestBuilder<ChaptersCachingWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            Constants.SYNC_CHAPTER_SIZE_CHECKER,
            ExistingWorkPolicy.KEEP,
            syncingChaptersRequest)
    }

    fun updateAnimeProfile(animeProfile: AnimeProfile) {
        viewModelScope.launch (Dispatchers.IO) {
            repository.updateAnimeProfile(animeProfile)
        }
    }
}