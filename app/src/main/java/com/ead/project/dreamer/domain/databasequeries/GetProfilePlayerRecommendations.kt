package com.ead.project.dreamer.domain.databasequeries

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.database.model.AnimeProfile
import javax.inject.Inject

class GetProfilePlayerRecommendations @Inject constructor(
    private val repository: AnimeRepository
) {

    fun livedata(animeProfile: AnimeProfile) : LiveData<List<AnimeProfile>> =
        if (Constants.isGooglePolicyNotActivate())
            repository.getFlowRandomProfileListFrom(animeProfile.genres.random(), animeProfile)
        else {
            val genre = Tools.filterGenreByGooglePolicies(animeProfile.genres)
            repository.getFlowRandomProfileListCensuredFrom(genre, animeProfile)
        }.asLiveData()
}