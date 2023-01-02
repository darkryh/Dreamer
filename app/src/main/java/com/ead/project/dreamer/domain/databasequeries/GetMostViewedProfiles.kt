package com.ead.project.dreamer.domain.databasequeries

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.AnimeProfile
import javax.inject.Inject

class GetMostViewedProfiles @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend operator fun invoke () : List<AnimeProfile> = repository.getMostViewedSeries()

    fun livedata() : LiveData<List<AnimeProfile>> = repository.getFlowMostViewedSeries().asLiveData()
}