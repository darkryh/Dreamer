package com.ead.project.dreamer.domain.operations

import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.data.database.model.NewsItem
import javax.inject.Inject

class UpdateObject @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend operator fun <T : Any> invoke(mObject : T) {
        when(mObject) {
            is Chapter -> {
                repository.updateChapter(mObject)
                updateAnimeProfile(mObject)
            }
            is AnimeProfile -> repository.updateAnimeProfile(mObject)
            is List<*> -> manageGenericList(mObject)
        }
    }

    private suspend fun <T> manageGenericList(mObject : T) {
        val objectList = mObject as List<*>
        if (objectList.isNotEmpty())
            when(objectList.component1()) {
                is Chapter ->
                    repository.updateChapterList(objectList.filterIsInstance(Chapter::class.java))
                is ChapterHome ->
                    repository.updateHomeList(objectList.filterIsInstance(ChapterHome::class.java))
                is NewsItem ->
                    repository.updateNewsItemList(objectList.filterIsInstance(NewsItem::class.java))
            }
    }

    private suspend fun updateAnimeProfile(chapter: Chapter) {
        val animeProfile = repository.getAnimeProfile(chapter.idProfile)?:return
        repository.updateAnimeProfile(animeProfile.copy(
            lastChapterSeen = chapter
        ))
    }

}