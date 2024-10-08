package com.ead.project.dreamer.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import com.ead.project.dreamer.app.data.player.casting.CastManager
import com.ead.project.dreamer.app.data.util.TimeUtil
import com.ead.project.dreamer.app.data.worker.Worker
import com.ead.project.dreamer.app.model.EadAccount
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.domain.ChapterUseCase
import com.ead.project.dreamer.domain.ObjectUseCase
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.domain.ProfileUseCase
import com.ead.project.dreamer.domain.configurations.ConfigureChapters
import com.ead.project.dreamer.domain.configurations.ConfigureProfile
import com.ead.project.dreamer.domain.configurations.LaunchOneTimeRequest
import com.ead.project.dreamer.domain.databasequeries.GetChapter
import com.ead.project.dreamer.domain.servers.HandleChapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
    val handleChapter: HandleChapter,
    val getChapter: GetChapter,
    val castManager: CastManager,
    preferenceUseCase: PreferenceUseCase
): ViewModel() {

    val appBuildPreferences = preferenceUseCase.appBuildPreferences
    private val userPreferences = preferenceUseCase.userPreferences

    val getProfile = profileUseCase.getProfile

    fun getAccount() : Flow<EadAccount?> = userPreferences.user

    fun getAnimeProfile(id : Int) : LiveData<AnimeProfile?>  = profileUseCase.getProfile.livedata(id)

    fun getIsLikedProfile(id: Int) : LiveData<Boolean?> = getAnimeProfile(id).map { it?.isFavorite }

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

    fun updateChapterIfIsConsumed(chapter: Chapter) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!chapter.isContentConsumed) return@launch

            val nextChapter = getChapter.fromTitleAndNumber(chapter.title, chapter.number + 1)?:return@launch

            objectUseCase.updateObject(
                nextChapter.copy(
                    currentProgress = 1,
                    lastDateSeen = TimeUtil.getNow()
                )
            )
        }
    }

    fun downloadAllChapters(id: Int) =
        viewModelScope.launch (Dispatchers.IO) { /*downloadUseCase.startDownload(chapterUseCase.getChaptersToDownload(id))*/ }

    fun downloadFromChapters(chapters: List<Chapter>) { /*downloadUseCase.startDownload(chapters)*/ }

    fun downloadFromChapter(chapter: Chapter) { /*downloadUseCase.startDownload(chapter)*/ }

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