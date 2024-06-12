package com.ead.project.dreamer.presentation.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ead.project.dreamer.app.data.player.casting.CastManager
import com.ead.project.dreamer.app.model.EadAccount
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.AdOrder
import com.ead.project.dreamer.domain.ChapterUseCase
import com.ead.project.dreamer.domain.ObjectUseCase
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.domain.ProfileUseCase
import com.ead.project.dreamer.domain.servers.HandleChapter
import com.google.android.gms.cast.framework.CastContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
    application: Application
): AndroidViewModel(application) {

    val preferences = preferenceUseCase.preferences
    val playerPreferences = preferenceUseCase.playerPreferences
    private val adPreferences = preferenceUseCase.adPreferences
    private val userPreferences = preferenceUseCase.userPreferences

    val getProfile = profileUseCase.getProfile

    private val adOrder by lazy {
        AdOrder(
            items = mutableListOf(),
            ads = emptyList()
        )
    }

    private val _nextChapter : MutableLiveData<Chapter?> = MutableLiveData(null)

    private val _recommendedProfiles : MutableLiveData<List<Any>> = MutableLiveData()
    val recommendedProfiles : LiveData<List<Any>> = _recommendedProfiles

    fun setRecommendedProfiles(list : List<Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            adOrder.setup(list,_recommendedProfiles)
        }
    }

    fun getAccount() : Flow<EadAccount?> = userPreferences.user

    fun addViewedTime() {
        adPreferences.addViewedTime()
    }

    fun updateChapter(chapter: Chapter) = viewModelScope.launch (Dispatchers.IO) {
        objectUseCase.updateObject(chapter)
    }
    fun getProfileData(id: Int) : LiveData<AnimeProfile?> = profileUseCase.getProfile.livedata(id)

    fun getChaptersFromProfile (id : Int) = chapterUseCase.getChapters.livedata(id,false)

    fun geNextChapterFrom(chapter: Chapter) : LiveData<Chapter?> {
        viewModelScope.launch(Dispatchers.IO) {
            val nextChapter = chapterUseCase.getChapter.fromTitleAndNumber(chapter.title,chapter.number + 1)
            _nextChapter.postValue(nextChapter)
        }
        return _nextChapter
    }

    fun getProfilesListFrom(animeProfile: AnimeProfile) : LiveData<List<AnimeProfile>> =
        profileUseCase.getProfilePlayerRecommendations.livedata(animeProfile)

    fun updateAnimeProfile(animeProfile: AnimeProfile) = viewModelScope.launch (Dispatchers.IO) {
        objectUseCase.updateObject(animeProfile)
    }

}