package com.ead.project.dreamer.domain.apis.app

import com.ead.project.dreamer.app.model.scrapping.AnimeProfileScrap
import com.ead.project.dreamer.data.AnimeRepository
import javax.inject.Inject

class GetProfileScrap @Inject constructor(
    private val repository: AnimeRepository
) {

    operator fun invoke() : AnimeProfileScrap =
        AnimeProfileScrap.get()?: repository.getAnimeProfileScrap().also { AnimeProfileScrap.set(it) }

    fun fromApi() : AnimeProfileScrap = repository.getAnimeProfileScrap()
}