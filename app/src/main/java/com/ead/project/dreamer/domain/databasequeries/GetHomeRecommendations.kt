package com.ead.project.dreamer.domain.databasequeries

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.AnimeProfile
import javax.inject.Inject

class GetHomeRecommendations @Inject constructor(
    private val repository: AnimeRepository
) {

    fun livedata () : LiveData<List<AnimeProfile>> =
        if (Constants.isGooglePolicyNotActivate()) { repository.getFlowProfileRandomRecommendationsList() }
        else { repository.getFlowProfileRandomRecommendationsListCensured() }.asLiveData()
}