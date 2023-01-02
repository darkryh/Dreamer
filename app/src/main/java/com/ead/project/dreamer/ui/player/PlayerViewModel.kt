package com.ead.project.dreamer.ui.player

import android.app.Application
import androidx.lifecycle.*
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val profileManager: ProfileManager,
    private val chapterManager: ChapterManager,
    private val objectManager: ObjectManager,
    application: Application
): AndroidViewModel(application) {

    fun updateChapter(chapter: Chapter) =
        viewModelScope.launch (Dispatchers.IO) { objectManager.updateObject(chapter) }

    fun getProfileData(id: Int) : LiveData<AnimeProfile?> = profileManager.getProfile.livedata(id)

    fun getChaptersFromProfile (id : Int) = chapterManager.getChapters.livedata(id,false)

    fun getProfilesListFrom(animeProfile: AnimeProfile) : LiveData<List<AnimeProfile>> =
        profileManager.getProfilePlayerRecommendations.livedata(animeProfile)

    fun updateAnimeProfile(animeProfile: AnimeProfile) =
        viewModelScope.launch (Dispatchers.IO) { objectManager.updateObject(animeProfile) }

}