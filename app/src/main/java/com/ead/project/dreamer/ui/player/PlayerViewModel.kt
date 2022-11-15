package com.ead.project.dreamer.ui.player

import android.app.Application
import androidx.lifecycle.*
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: AnimeRepository, application: Application
): AndroidViewModel(application) {

    fun updateChapter(chapter: Chapter) {
        viewModelScope.launch (Dispatchers.IO) {
            repository.updateChapter(chapter)
        }
    }

    fun getProfile(id: Int) = repository.getFlowAnimeProfile(id).asLiveData()

    fun getChaptersFromProfile (id : Int) = repository.getFlowChaptersFromProfileAsc(id).asLiveData()

    fun getProfilesListFrom(genres: MutableList<String>,rating : Float,id: Int) : LiveData<List<AnimeProfile>>  {
        val random = Tools.checkGooglePolicies(genres)
        return repository.getFlowRandomProfileListFrom(random, rating,id).asLiveData()
    }

    fun updateAnimeProfile(animeProfile: AnimeProfile) {
        viewModelScope.launch (Dispatchers.IO) {
            repository.updateAnimeProfile(animeProfile)
        }
    }
}