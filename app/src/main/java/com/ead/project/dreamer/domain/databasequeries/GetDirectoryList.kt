package com.ead.project.dreamer.domain.databasequeries

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.domain.PreferenceUseCase
import javax.inject.Inject

class GetDirectoryList @Inject constructor(
    private val repository: AnimeRepository,
    preferenceUseCase: PreferenceUseCase
) {

    private val appBuildPreferences = preferenceUseCase.appBuildPreferences

    suspend operator fun invoke() : List<AnimeBase> = repository.getDirectory()

    fun livedata (title : String,isSynchronized : Boolean) : LiveData<List<AnimeBase>> =
        if (appBuildPreferences.isUnlockedVersion()) {
            if (isSynchronized) repository.getFlowAnimeBaseFullList(title)
            else repository.getFlowAnimeBaseList(title)
        } else {
            if (isSynchronized) repository.getFlowAnimeBaseFullListCensured(title)
            else repository.getFlowAnimeBaseListCensured(title)
        }.asLiveData()
}