package com.ead.project.dreamer.domain.databasequeries

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.domain.PreferenceUseCase
import javax.inject.Inject

class GetHomeList @Inject constructor(
    private val repository: AnimeRepository,
    preferenceUseCase: PreferenceUseCase
) {

    private val appBuildPreferences = preferenceUseCase.appBuildPreferences

    suspend operator fun invoke() : List<ChapterHome> = repository.getChaptersHome()

    fun livedata() : LiveData<List<ChapterHome>> =
        if (appBuildPreferences.isUnlockedVersion()) { repository.getFlowChapterHome() }
        else { repository.getFlowChapterHomeCensured() }.asLiveData()
}