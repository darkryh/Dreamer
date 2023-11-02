package com.ead.project.dreamer.domain.apis.app

import com.ead.project.dreamer.app.model.scraper.NewsItemScrap
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GetNewsItemScrap @Inject constructor(
    private val repository: AnimeRepository,
    private val gson: Gson,
    preferenceUseCase: PreferenceUseCase
) {

    private val preferences = preferenceUseCase.preferences

    suspend operator fun invoke () : NewsItemScrap =
        get()?: fromApi().also { set(it) }

    suspend fun fromApi() : NewsItemScrap = repository.getNewsItemScrap()

    private suspend fun get() : NewsItemScrap? = try {
        val newsItemScrapJson = preferences.getString(NewsItemScrap.INSTANCE)
        gson.fromJson(newsItemScrapJson, NewsItemScrap::class.java)
    }
    catch (e : Exception) {
        e.printStackTrace()
        null
    }

    private fun set(newsItemScrap: NewsItemScrap) = runBlocking {
        preferences.set(NewsItemScrap.INSTANCE,gson.toJson(newsItemScrap))
    }
}