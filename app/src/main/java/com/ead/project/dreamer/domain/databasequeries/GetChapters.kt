package com.ead.project.dreamer.domain.databasequeries

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetChapters @Inject constructor(
    private val repository: AnimeRepository
) {

    fun livedata (id : Int,isDesc :Boolean = true) : LiveData<List<Chapter>> =
        if (isDesc) { repository.getFlowChaptersFromProfile(id) }
        else { repository.getFlowChaptersFromProfileAsc(id) }.asLiveData()

    fun livedata (id : Int,start: Int,end: Int) : LiveData<List<Chapter>> =
        repository.getFlowChaptersFromProfile(id).map {
        it.filter { filter -> filter.number >= start }
            .filter { filter2 -> filter2.number <= end } }.asLiveData()
}