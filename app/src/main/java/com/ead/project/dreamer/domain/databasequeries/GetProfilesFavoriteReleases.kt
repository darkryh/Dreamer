package com.ead.project.dreamer.domain.databasequeries

import com.ead.project.dreamer.data.AnimeRepository
import javax.inject.Inject

class GetProfilesFavoriteReleases @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend fun stringList () : List<String> =
        repository.getFavoriteProfileReleasesTitles()
}