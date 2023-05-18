package com.ead.project.dreamer.domain.apis.app

import com.ead.project.dreamer.app.model.scraper.NewsItemWebScrap
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GetNewsItemWebScrap @Inject constructor(
    private val repository: AnimeRepository,
    private val gson: Gson,
    preferenceUseCase: PreferenceUseCase
) {

    private val preferences = preferenceUseCase.preferences

    operator fun invoke () : NewsItemWebScrap =
        get()?: fromApi().also { set(it) }

    fun fromApi() : NewsItemWebScrap = repository.getNewsItemWebScrap()

    private fun get() : NewsItemWebScrap? = try {
        val newsItemWebScrapJson = runBlocking { preferences.getString(NewsItemWebScrap.INSTANCE) }
        gson.fromJson(newsItemWebScrapJson, NewsItemWebScrap::class.java)
    }
    catch (e : Exception) {
        e.printStackTrace()
        null
    }

    private fun set(newsItemWebScrap: NewsItemWebScrap) = runBlocking {
        preferences.set(NewsItemWebScrap.INSTANCE,gson.toJson(newsItemWebScrap))
    }
}