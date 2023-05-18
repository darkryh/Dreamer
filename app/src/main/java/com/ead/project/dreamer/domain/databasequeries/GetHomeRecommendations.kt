package com.ead.project.dreamer.domain.databasequeries

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.domain.PreferenceUseCase
import javax.inject.Inject

class GetHomeRecommendations @Inject constructor(
    private val repository: AnimeRepository,
    preferenceUseCase: PreferenceUseCase
) {

    private val appBuildPreferences = preferenceUseCase.appBuildPreferences

    fun livedata () : LiveData<List<AnimeProfile>> =
        if (appBuildPreferences.isUnlockedVersion()) { repository.getFlowProfileRandomRecommendationsList() }
        else { repository.getFlowProfileRandomRecommendationsListCensured() }.asLiveData()
}