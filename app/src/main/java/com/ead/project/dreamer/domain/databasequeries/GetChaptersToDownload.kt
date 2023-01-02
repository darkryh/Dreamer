package com.ead.project.dreamer.domain.databasequeries

import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class GetChaptersToDownload @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend operator fun invoke (id : Int) : List<Chapter> = repository.getNotDownloadedChaptersFromId(id)
}