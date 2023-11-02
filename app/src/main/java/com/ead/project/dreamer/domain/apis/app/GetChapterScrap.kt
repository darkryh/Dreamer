package com.ead.project.dreamer.domain.apis.app

import com.ead.project.dreamer.app.model.scraper.ChapterScrap
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GetChapterScrap @Inject constructor(
    private val repository: AnimeRepository,
    private val gson: Gson,
    preferenceUseCase: PreferenceUseCase
) {

    private val preferences = preferenceUseCase.preferences
    suspend operator fun invoke() : ChapterScrap =
        get()?: fromApi().also { set(it) }

    suspend fun fromApi() : ChapterScrap = repository.getChapterScrap()

    private suspend fun get() : ChapterScrap? = try {
        val chapterScrapJson = preferences.getString(ChapterScrap.INSTANCE)
        gson.fromJson(chapterScrapJson, ChapterScrap::class.java)
    }
    catch (e : Exception) {
        e.printStackTrace()
        null
    }

    private fun set(chapterScrap: ChapterScrap) = runBlocking {
        preferences.set(ChapterScrap.INSTANCE,gson.toJson(chapterScrap))
    }
}