package com.ead.project.dreamer.domain.apis.app

import com.ead.project.dreamer.app.model.scrapping.NewsItemWebScrap
import com.ead.project.dreamer.data.AnimeRepository
import javax.inject.Inject

class GetNewsItemWebScrap @Inject constructor(
    private val repository: AnimeRepository
) {

    operator fun invoke () : NewsItemWebScrap =
        NewsItemWebScrap.get()?: repository.getNewsItemWebScrap().also { NewsItemWebScrap.set(it) }

    fun fromApi() : NewsItemWebScrap = repository.getNewsItemWebScrap()
}