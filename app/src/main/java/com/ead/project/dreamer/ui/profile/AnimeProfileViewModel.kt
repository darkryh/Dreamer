package com.ead.project.dreamer.ui.profile


import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.DownloadManager
import com.ead.project.dreamer.data.worker.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class AnimeProfileViewModel @Inject constructor(
    private val repository: AnimeRepository,
    private val workManager: WorkManager,
    private val constraints: Constraints,
    private val downloadManager: DownloadManager
): ViewModel() {

    fun getAnimeProfile(id : Int)  =
        repository.getFlowAnimeProfile(id).asLiveData()

    fun getPreparation(id : Int) = repository.getFlowPreparationProfile(id).asLiveData()

    fun getChaptersFromProfile (id : Int) = repository.getFlowChaptersFromProfile(id).asLiveData()

    fun getChaptersFromProfile (id : Int,start: Int,end: Int) = repository.getFlowChaptersFromProfile(id).map {
        it.filter { filter -> filter.chapterNumber >= start }
            .filter { filter2 -> filter2.chapterNumber <= end }
    }.asLiveData()

    fun updateChapter(chapter: Chapter) {
        viewModelScope.launch (Dispatchers.IO) {
            repository.updateChapter(chapter)
        }
    }

    fun updateAnimeProfile(animeProfile: AnimeProfile) {
        viewModelScope.launch (Dispatchers.IO) {
            repository.updateAnimeProfile(animeProfile)
        }
    }

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
            Constants.SYNC_CHAPTER_SIZE,
            ExistingWorkPolicy.KEEP,
            syncingChaptersRequest)
    }

    fun repairingProfiles() {

        val cachingProfile =  OneTimeWorkRequestBuilder<FixerProfileCachingWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            Constants.SYNC_PROFILE_FIXER_CHECKER,
            ExistingWorkPolicy.KEEP,
            cachingProfile)
    }

    fun downloadAllChapters(id: Int) {
        viewModelScope.launch (Dispatchers.IO) {
            downloadManager.init(repository.getNotDownloadedChaptersFromId(id))
        }
    }

    fun repairingChapters() {
        val syncingChaptersRequest = OneTimeWorkRequestBuilder<FixerChaptersCachingWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            Constants.SYNC_CHAPTER_FIXER_SIZE,
            ExistingWorkPolicy.KEEP,
            syncingChaptersRequest)
    }
}