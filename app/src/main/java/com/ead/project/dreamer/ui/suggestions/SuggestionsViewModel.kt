package com.ead.project.dreamer.ui.suggestions

import androidx.lifecycle.*
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.utils.Categorizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuggestionsViewModel @Inject constructor(
    private val repository: AnimeRepository
): ViewModel() {

    private val recommendationList : MutableLiveData<List<AnimeProfile>> = MutableLiveData()

    fun getMostViewedSeries() : LiveData<List<AnimeProfile>> = repository.getFlowMostViewedSeries().asLiveData()


    fun getRecommendations() : MutableLiveData<List<AnimeProfile>> {
        viewModelScope.launch (Dispatchers.IO) {
            val mostViewedSeries = repository.getMostViewedSeries()
            val topProfilesGenres = Categorizer.configProfiles(mostViewedSeries)
            recommendationList.postValue(repository.getRecommendations(topProfilesGenres))
        }
        return recommendationList
    }
}