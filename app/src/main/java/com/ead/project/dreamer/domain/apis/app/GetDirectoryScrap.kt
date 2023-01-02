package com.ead.project.dreamer.domain.apis.app

import com.ead.project.dreamer.app.model.scrapping.AnimeBaseScrap
import com.ead.project.dreamer.data.AnimeRepository
import javax.inject.Inject

class GetDirectoryScrap @Inject constructor(
    private val repository: AnimeRepository
) {

    operator fun invoke() : AnimeBaseScrap =
        AnimeBaseScrap.get()?: repository.getAnimeBaseScrap().also { AnimeBaseScrap.set(it) }

    fun fromApi() : AnimeBaseScrap = repository.getAnimeBaseScrap()
}