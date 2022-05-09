package com.ead.project.dreamer.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.AnimeProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: AnimeRepository
): ViewModel() {

    fun getLikedDirectory() : LiveData<List<AnimeProfile>> = repository.getFlowLikedDirectory().asLiveData()

    fun getFilterDirectory(state : String?= null,genre : String?= null) : LiveData<List<AnimeProfile>>   {
        if (state != null  && genre != null) {
            return repository.getFlowLikedDirectory().map {
                it.filter { filter ->
                    filter.rawGenres.contains(genre)

                }.filter { filter2 ->
                    filter2.state == state
                }
            }.asLiveData()
        }
        else {
            if (genre != null){
                return repository.getFlowLikedDirectory().map {
                    it.filter { filter ->
                        filter.rawGenres.contains(genre)
                    }
                }.asLiveData()
            }
            else {
                if (state != null){
                    return repository.getFlowLikedDirectory().map {
                        it.filter { filter ->
                            filter.state == state
                        }
                    }.asLiveData()
                }
            }
        }
        return repository.getFlowLikedDirectory().asLiveData()
    }

}