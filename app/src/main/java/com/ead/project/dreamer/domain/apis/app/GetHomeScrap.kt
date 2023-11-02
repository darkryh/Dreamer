package com.ead.project.dreamer.domain.apis.app

import com.ead.project.dreamer.app.model.scraper.ChapterHomeScrap
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GetHomeScrap @Inject constructor(
    private val repository: AnimeRepository,
    private val gson: Gson,
    preferenceUseCase: PreferenceUseCase
) {

    private val preferences = preferenceUseCase.preferences

    suspend operator fun invoke() : ChapterHomeScrap =
        get()?: fromApi().also { set(it) }

    suspend fun fromApi() : ChapterHomeScrap = repository.getChapterHomeScrap()

    private suspend fun get() : ChapterHomeScrap? = try {
        val chapterHomeScrapJson = preferences.getString(ChapterHomeScrap.INSTANCE)
        gson.fromJson(chapterHomeScrapJson, ChapterHomeScrap::class.java)
    }
    catch (e : Exception) {
        e.printStackTrace()
        null
    }

    private fun set(chapterHomeScrap: ChapterHomeScrap) = runBlocking {
        preferences.set(ChapterHomeScrap.INSTANCE,gson.toJson(chapterHomeScrap))
    }
}