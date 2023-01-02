package com.ead.project.dreamer.domain.apis.app

import com.ead.project.dreamer.app.model.scrapping.NewsItemScrap
import com.ead.project.dreamer.data.AnimeRepository
import javax.inject.Inject

class GetNewsItemScrap @Inject constructor(
    private val repository: AnimeRepository
) {

    operator fun invoke () : NewsItemScrap =
        NewsItemScrap.get()?: repository.getNewsItemScrap().also { NewsItemScrap.set(it) }

    fun fromApi() : NewsItemScrap = repository.getNewsItemScrap()
}