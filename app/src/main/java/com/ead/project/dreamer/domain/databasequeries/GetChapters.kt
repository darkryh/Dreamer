package com.ead.project.dreamer.domain.databasequeries

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class GetChapters @Inject constructor(
    private val repository: AnimeRepository
) {

    fun livedata (id : Int,isDesc :Boolean = true) : LiveData<List<Chapter>> =
        if (isDesc) { repository.getFlowChaptersFromProfile(id) }
        else { repository.getFlowChaptersFromProfileAsc(id) }.asLiveData()

    fun fromNumber(id: Int,number : Int) : LiveData<List<Chapter>> =
        repository.getFlowChaptersFromNumber(id,number).asLiveData()
}