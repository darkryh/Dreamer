package com.ead.project.dreamer.domain.databasequeries

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.AnimeProfile
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLikedProfiles @Inject constructor(
    private val repository: AnimeRepository
) {

    fun livedata() : LiveData<List<AnimeProfile>> = repository.getFlowLikedDirectory().asLiveData()

    fun livedata(state : String?= null,genre : String?= null) : LiveData<List<AnimeProfile>> {
        if (state != null  && genre != null) {
            return repository.getFlowLikedDirectory().map {
                it.filter { filter -> filter.rawGenres.contains(genre) }
                    .filter { filter2 -> filter2.state == state }
            }.asLiveData()
        }
        else {
            if (genre != null)
                return repository.getFlowLikedDirectory().map {
                    it.filter { filter -> filter.rawGenres.contains(genre) } }.asLiveData()
            else
                if (state != null)
                    return repository.getFlowLikedDirectory().map {
                        it.filter { filter -> filter.state == state } }.asLiveData()

        }
        return repository.getFlowLikedDirectory().asLiveData()
    }
}