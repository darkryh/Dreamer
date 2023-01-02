package com.ead.project.dreamer.domain.operations

import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.*
import javax.inject.Inject

class InsertObject @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend operator fun <T : Any> invoke(mObject : T) {
        when(mObject) {
            is AnimeProfile -> repository.insertProfile(mObject)
            is List<*> -> manageGenericList(mObject)
        }
    }

    private suspend fun <T> manageGenericList(mObject : T) {
        val objectList = mObject as List<*>
        if (objectList.isNotEmpty())
            when(objectList.component1()) {
                is AnimeBase ->
                    repository.insertDirectoryList(objectList.filterIsInstance(AnimeBase::class.java))
                is Chapter ->
                    repository.insertChapterList(objectList.filterIsInstance(Chapter::class.java))
                is NewsItem ->
                    repository.insertNewsItemList(objectList.filterIsInstance(NewsItem::class.java))
                is ChapterHome ->
                    repository.insertHomeList(objectList.filterIsInstance(ChapterHome::class.java))
            }
    }
}