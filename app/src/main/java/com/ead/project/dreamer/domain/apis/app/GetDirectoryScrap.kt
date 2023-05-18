package com.ead.project.dreamer.domain.apis.app

import com.ead.project.dreamer.app.model.scraper.AnimeBaseScrap
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GetDirectoryScrap @Inject constructor(
    private val repository: AnimeRepository,
    private val gson: Gson,
    preferenceUseCase: PreferenceUseCase
) {

    private val preferences = preferenceUseCase.preferences

    operator fun invoke() : AnimeBaseScrap =
        get() ?: fromApi().also { set(it) }

    fun fromApi() : AnimeBaseScrap = repository.getAnimeBaseScrap()

    private fun get() : AnimeBaseScrap? = try {
        val animeBaseScrapJson = runBlocking { preferences.getString(AnimeBaseScrap.INSTANCE) }
        gson.fromJson(animeBaseScrapJson, AnimeBaseScrap::class.java)
    }
    catch (e : Exception) {
        e.printStackTrace()
        null
    }

    private fun set(animeBaseScrap: AnimeBaseScrap) = runBlocking {
        preferences.set(AnimeBaseScrap.INSTANCE,gson.toJson(animeBaseScrap))
    }
}