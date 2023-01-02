package com.ead.project.dreamer.domain.apis.app

import com.ead.project.dreamer.app.model.scrapping.ChapterScrap
import com.ead.project.dreamer.data.AnimeRepository
import javax.inject.Inject

class GetChapterScrap @Inject constructor(
    private val repository: AnimeRepository
) {

    operator fun invoke() : ChapterScrap =
        ChapterScrap.get()?: repository.getChapterScrap().also { ChapterScrap.set(it) }

    fun fromApi() : ChapterScrap = repository.getChapterScrap()
}