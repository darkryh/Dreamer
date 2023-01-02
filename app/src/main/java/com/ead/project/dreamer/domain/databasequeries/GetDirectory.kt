package com.ead.project.dreamer.domain.databasequeries

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.AnimeBase
import javax.inject.Inject

class GetDirectory @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend fun byId(id : Int) : AnimeBase =
        repository.getDirectoryById(id)

    fun checkIfTitleExist(title: String) = repository.checkIfAnimeBaseExist(title)

    fun livedata (title : String) : LiveData<AnimeBase?> =
        repository.getFlowAnimeBaseFromTitle(title).asLiveData()

}