package com.ead.project.dreamer.domain.databasequeries

import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.AnimeProfile
import javax.inject.Inject

class GetProfilesReleases @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend operator fun invoke(): List<AnimeProfile> =
        repository.getProfileReleases()
}