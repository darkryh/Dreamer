package com.ead.project.dreamer.domain.databasequeries

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.NewsItem
import javax.inject.Inject

class GetNews @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend operator fun invoke() : List<NewsItem> = repository.getNewsItems()

    fun livedata() : LiveData<List<NewsItem>> =
        if (Constants.isGooglePolicyActivate()) { repository.getFlowNewsItemsCensured() }
        else { repository.getFlowNewsItems() }.asLiveData()
}