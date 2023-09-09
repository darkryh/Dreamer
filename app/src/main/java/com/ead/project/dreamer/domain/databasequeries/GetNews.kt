package com.ead.project.dreamer.domain.databasequeries

import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.NewsItem
import com.ead.project.dreamer.domain.PreferenceUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNews @Inject constructor(
    private val repository: AnimeRepository,
    preferenceUseCase: PreferenceUseCase
) {

    private val appBuildPreferences = preferenceUseCase.appBuildPreferences

    suspend operator fun invoke() : List<NewsItem> = repository.getNewsItems()

    fun flow() : Flow<List<NewsItem>> =
        (if (appBuildPreferences.isLockedVersion()) { repository.getFlowNewsItemsCensured() }
        else { repository.getFlowNewsItems() })

    fun flowLimited() : Flow<List<NewsItem>> =
        (if (appBuildPreferences.isLockedVersion()) { repository.getFlowNewsItemsCensuredLimited() }
        else { repository.getFlowNewsItemsLimited() })
}