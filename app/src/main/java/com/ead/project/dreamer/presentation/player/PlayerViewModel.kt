package com.ead.project.dreamer.presentation.player

import android.app.Application
import androidx.lifecycle.*
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.app.data.player.casting.CastManager
import com.ead.project.dreamer.data.utils.AdManager
import com.ead.project.dreamer.domain.*
import com.ead.project.dreamer.domain.servers.HandleChapter
import com.google.android.gms.cast.framework.CastContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val profileUseCase: ProfileUseCase,
    private val chapterUseCase: ChapterUseCase,
    private val objectUseCase: ObjectUseCase,
    val preferenceUseCase: PreferenceUseCase,
    val handleChapter: HandleChapter,
    val castContext: CastContext,
    val castManager: CastManager,
    val adManager: AdManager,
    application: Application
): AndroidViewModel(application) {

    val preferences = preferenceUseCase.preferences
    val playerPreferences = preferenceUseCase.playerPreferences
    private val adPreferences = preferenceUseCase.adPreferences

    fun addViewedTime() {
        adPreferences.addViewedTime()
    }

    fun updateChapter(chapter: Chapter) = viewModelScope.launch (Dispatchers.IO) {
        objectUseCase.updateObject(chapter)
    }
    fun getProfileData(id: Int) : LiveData<AnimeProfile?> = profileUseCase.getProfile.livedata(id)

    fun getChaptersFromProfile (id : Int) = chapterUseCase.getChapters.livedata(id,false)

    fun getProfilesListFrom(animeProfile: AnimeProfile) : LiveData<List<AnimeProfile>> =
        profileUseCase.getProfilePlayerRecommendations.livedata(animeProfile)

    fun updateAnimeProfile(animeProfile: AnimeProfile) = viewModelScope.launch (Dispatchers.IO) {
        objectUseCase.updateObject(animeProfile)
    }

}