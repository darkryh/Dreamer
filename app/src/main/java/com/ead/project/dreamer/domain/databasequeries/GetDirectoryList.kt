package com.ead.project.dreamer.domain.databasequeries

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.AnimeBase
import javax.inject.Inject

class GetDirectoryList @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend operator fun invoke() : List<AnimeBase> = repository.getDirectory()

    fun livedata (title : String,isSynchronized : Boolean) : LiveData<List<AnimeBase>> =
        if (Constants.isGooglePolicyNotActivate()) {
            if (isSynchronized) repository.getFlowAnimeBaseFullList(title)
            else repository.getFlowAnimeBaseList(title)
        } else {
            if (isSynchronized) repository.getFlowAnimeBaseFullListCensured(title)
            else repository.getFlowAnimeBaseListCensured(title)
        }.asLiveData()
}