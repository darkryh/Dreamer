package com.ead.project.dreamer.domain.databasequeries

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class GetChapter @Inject constructor(
    private val repository: AnimeRepository
) {

    fun firstChapterLiveData(id: Int) : LiveData<Chapter?> =
        repository.getFlowFirstChapterFromProfileId(id).asLiveData()

    suspend fun fromId(id : Int) : Chapter? =
        repository.getChapterFromId(id)

    suspend fun fromTitleAndNumber(title : String, number: Int) : Chapter? =
        repository.getChapterFromTitleAndNumber(title, number)

    fun livedata(chapter: Chapter) : LiveData<Chapter?> =
        repository.getFlowChapterFromTitleAndNumber(chapter.title,chapter.number).asLiveData()
}