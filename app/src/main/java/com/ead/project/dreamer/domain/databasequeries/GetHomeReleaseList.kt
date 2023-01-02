package com.ead.project.dreamer.domain.databasequeries

import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.ChapterHome
import javax.inject.Inject

class GetHomeReleaseList @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend operator fun invoke() : List<ChapterHome> = repository.getChapterHomeReleaseList()
}