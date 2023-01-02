package com.ead.project.dreamer.domain.apis.app

import com.ead.project.dreamer.app.model.scrapping.ChapterHomeScrap
import com.ead.project.dreamer.data.AnimeRepository
import javax.inject.Inject

class GetHomeScrap @Inject constructor(
    private val repository: AnimeRepository
) {

    operator fun invoke() : ChapterHomeScrap =
        ChapterHomeScrap.get()?: repository.getChapterHomeScrap().also { ChapterHomeScrap.set(it) }

    fun fromApi() : ChapterHomeScrap = repository.getChapterHomeScrap()
}