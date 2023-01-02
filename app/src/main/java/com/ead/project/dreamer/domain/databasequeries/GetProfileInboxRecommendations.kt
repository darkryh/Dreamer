package com.ead.project.dreamer.domain.databasequeries

import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.AnimeProfile
import javax.inject.Inject

class GetProfileInboxRecommendations @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend operator fun invoke (stringList: List<String>) : List<AnimeProfile> =
        repository.getRecommendations(stringList)
}