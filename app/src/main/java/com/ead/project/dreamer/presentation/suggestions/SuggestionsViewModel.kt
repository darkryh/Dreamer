package com.ead.project.dreamer.presentation.suggestions

import androidx.lifecycle.*
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.utils.Categorizer
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.domain.ProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuggestionsViewModel @Inject constructor(
    private val profileUseCase: ProfileUseCase,
    val preferenceUseCase: PreferenceUseCase
): ViewModel() {

    private val recommendationList : MutableLiveData<List<AnimeProfile>> = MutableLiveData()

    fun getMostViewedSeries() : LiveData<List<AnimeProfile>> = profileUseCase.getMostViewedProfiles.previewLivedData()

    fun getRecommendations() : MutableLiveData<List<AnimeProfile>> {
        viewModelScope.launch (Dispatchers.IO) {
            val mostViewedSeries = profileUseCase.getMostViewedProfiles()
            val topProfilesGenres = Categorizer.configProfiles(mostViewedSeries)
            val recommendations = profileUseCase.getProfileInboxRecommendations(topProfilesGenres)
            recommendationList.postValue(recommendations)
        }
        return recommendationList
    }
}