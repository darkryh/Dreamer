package com.ead.project.dreamer.domain.databasequeries

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class GetRecords @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend fun fromId(id: Int) : List<Chapter> = repository.getChaptersRecordsFromId(id)

    fun livedata() : LiveData<List<Chapter>> = repository.getFlowChaptersRecord().asLiveData()
}