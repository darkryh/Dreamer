package com.ead.project.dreamer.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.domain.*
import com.ead.project.dreamer.domain.configurations.ConfigureChapters
import com.ead.project.dreamer.domain.configurations.ConfigureProfile
import com.ead.project.dreamer.domain.configurations.LaunchOneTimeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeProfileViewModel @Inject constructor(
    private val profileManager: ProfileManager,
    private val chapterManager: ChapterManager,
    private val objectManager: ObjectManager,
    private val configureChapters: ConfigureChapters,
    private val configureProfile: ConfigureProfile,
    private val launchOneTimeRequest: LaunchOneTimeRequest,
    private val downloadManager: DownloadManager
): ViewModel() {

    fun getAnimeProfile(id : Int) : LiveData<AnimeProfile?>  = profileManager.getProfile.livedata(id)

    fun configureProfileData(id : Int,reference: String) =
        viewModelScope.launch (Dispatchers.IO) { configureProfile(id,reference) }

    fun configureChaptersData(id : Int,reference: String) =
        viewModelScope.launch (Dispatchers.IO) { configureChapters(id,reference) }

    fun getChaptersFromProfile (id : Int) : LiveData<List<Chapter>> = chapterManager.getChapters.livedata(id)

    fun getChaptersFromProfile (id : Int,start: Int,end: Int) : LiveData<List<Chapter>> =
        chapterManager.getChapters.livedata(id,start,end)

    fun updateChapter(chapter: Chapter) =
        viewModelScope.launch (Dispatchers.IO) { objectManager.updateObject(chapter) }

    fun updateChapters(chapters : List<Chapter>) =
        viewModelScope.launch (Dispatchers.IO) { objectManager.updateObject(chapters) }

    fun updateAnimeProfile(animeProfile: AnimeProfile) =
        viewModelScope.launch (Dispatchers.IO) { objectManager.updateObject(animeProfile) }

    fun downloadAllChapters(id: Int) =
        viewModelScope.launch (Dispatchers.IO) { downloadManager.startDownload(chapterManager.getChaptersToDownload(id)) }

    fun downloadFromChapters(chapters: List<Chapter>) { downloadManager.startDownload(chapters) }

    fun downloadFromChapter(chapter: Chapter) { downloadManager.startDownload(chapter) }

    fun repairingProfiles() {
        launchOneTimeRequest(
            LaunchOneTimeRequest.FixerProfileCachingWorkerCode,
            Constants.SYNC_PROFILE_FIXER_CHECKER,
            ExistingWorkPolicy.KEEP
        )
    }

    fun repairingChapters() {
        launchOneTimeRequest(
            LaunchOneTimeRequest.FixerChaptersCachingWorkerCode,
            Constants.SYNC_CHAPTER_FIXER_SIZE,
            ExistingWorkPolicy.KEEP,
        )
    }
}