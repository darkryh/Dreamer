package com.ead.project.dreamer.domain.databasequeries

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.AnimeProfile
import javax.inject.Inject

class GetProfile @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend operator fun invoke(id : Int) : AnimeProfile? = repository.getAnimeProfile(id)

    fun livedata(id : Int) : LiveData<AnimeProfile?> = repository.getFlowAnimeProfile(id).asLiveData()
}