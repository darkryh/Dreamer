package com.ead.project.dreamer.domain.apis.app

import com.ead.project.dreamer.app.model.scraper.AnimeProfileScrap
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GetProfileScrap @Inject constructor(
    private val repository: AnimeRepository,
    private val gson: Gson,
    preferenceUseCase: PreferenceUseCase
) {

    private val preferences = preferenceUseCase.preferences

    suspend operator fun invoke() : AnimeProfileScrap =
        get()?: fromApi().also { set(it) }

    suspend fun fromApi() : AnimeProfileScrap = repository.getAnimeProfileScrap()

    suspend fun get() : AnimeProfileScrap? = try {
        val animeProfileScrapJson = preferences.getString(AnimeProfileScrap.INSTANCE)
        gson.fromJson(animeProfileScrapJson, AnimeProfileScrap::class.java)
    } catch (e : Exception) {
        e.printStackTrace()
        null
    }

    private fun set(animeProfileScrap: AnimeProfileScrap) = runBlocking {
        preferences.set(AnimeProfileScrap.INSTANCE,gson.toJson(animeProfileScrap))
    }
}