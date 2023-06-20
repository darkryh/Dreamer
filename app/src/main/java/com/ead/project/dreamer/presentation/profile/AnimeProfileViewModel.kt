package com.ead.project.dreamer.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.ead.project.dreamer.app.data.worker.Worker
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.app.data.player.casting.CastManager
import com.ead.project.dreamer.app.model.Requester
import com.ead.project.dreamer.data.utils.AdManager
import com.ead.project.dreamer.domain.*
import com.ead.project.dreamer.domain.configurations.ConfigureChapters
import com.ead.project.dreamer.domain.configurations.ConfigureProfile
import com.ead.project.dreamer.domain.configurations.LaunchOneTimeRequest
import com.ead.project.dreamer.domain.servers.HandleChapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeProfileViewModel @Inject constructor(
    private val profileUseCase: ProfileUseCase,
    private val chapterUseCase: ChapterUseCase,
    private val objectUseCase: ObjectUseCase,
    private val configureChapters: ConfigureChapters,
    private val configureProfile: ConfigureProfile,
    private val launchOneTimeRequest: LaunchOneTimeRequest,
    private val downloadUseCase: DownloadUseCase,
    val handleChapter: HandleChapter,
    val adManager : AdManager,
    val castManager: CastManager,
    preferenceUseCase: PreferenceUseCase
): ViewModel() {

    private val playerPreferences = preferenceUseCase.playerPreferences
    val playerPreference = playerPreferences.preference

    fun getAnimeProfile(id : Int) : LiveData<AnimeProfile?>  = profileUseCase.getProfile.livedata(id)

    fun resetRequestingProfile() {
        viewModelScope.launch {
            playerPreferences.setRequestingProfile(Requester.Deactivate)
        }
    }

    fun configureProfileData(id : Int,reference: String) =
        viewModelScope.launch (Dispatchers.IO) { configureProfile(id,reference) }

    fun configureChaptersData(id : Int,reference: String) =
        viewModelScope.launch (Dispatchers.IO) { configureChapters(id,reference) }

    fun getFirstChapterFromProfile(id: Int) : LiveData<Chapter?> = chapterUseCase.getChapter.firstChapterLiveData(id)

    fun getChaptersFromProfile(id : Int) : LiveData<List<Chapter>> = chapterUseCase.getChapters.livedata(id)

    fun getChaptersFromNumber(id: Int,number : Int) : LiveData<List<Chapter>> = chapterUseCase.getChapters.fromNumber(id,number)

    fun updateChapter(chapter: Chapter) =
        viewModelScope.launch (Dispatchers.IO) { objectUseCase.updateObject(chapter) }

    fun updateChapters(chapters : List<Chapter>) =
        viewModelScope.launch (Dispatchers.IO) { objectUseCase.updateObject(chapters) }

    fun updateAnimeProfile(animeProfile: AnimeProfile) =
        viewModelScope.launch (Dispatchers.IO) { objectUseCase.updateObject(animeProfile) }

    fun downloadAllChapters(id: Int) =
        viewModelScope.launch (Dispatchers.IO) { downloadUseCase.startDownload(chapterUseCase.getChaptersToDownload(id)) }

    fun downloadFromChapters(chapters: List<Chapter>) { downloadUseCase.startDownload(chapters) }

    fun downloadFromChapter(chapter: Chapter) { downloadUseCase.startDownload(chapter) }

    fun repairingProfiles() {
        launchOneTimeRequest(
            LaunchOneTimeRequest.FixerProfileCachingWorkerCode,
            Worker.SYNC_PROFILE_FIXER_CHECKER,
            ExistingWorkPolicy.KEEP
        )
    }

    fun repairingChapters() {
        launchOneTimeRequest(
            LaunchOneTimeRequest.FixerChaptersCachingWorkerCode,
            Worker.SYNC_CHAPTER_FIXER_SIZE,
            ExistingWorkPolicy.KEEP,
        )
    }
}