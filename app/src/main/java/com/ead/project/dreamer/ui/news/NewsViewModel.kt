package com.ead.project.dreamer.ui.news

import androidx.lifecycle.*
import androidx.work.ExistingPeriodicWorkPolicy
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.NewsItem
import com.ead.project.dreamer.data.models.NewsItemWeb
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.domain.configurations.LaunchPeriodicTimeRequest
import com.ead.project.dreamer.domain.NewsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsManager: NewsManager,
    private val launchPeriodicTimeRequest: LaunchPeriodicTimeRequest,
    private val webProvider: WebProvider
): ViewModel() {

    private val newsItemWeb : MutableLiveData<NewsItemWeb?> = MutableLiveData()

    fun synchronizeNews() {
        launchPeriodicTimeRequest(
            LaunchPeriodicTimeRequest.NewsWorkerCode,
            30,
            TimeUnit.MINUTES,
            Constants.SYNC_NEWS,
            ExistingPeriodicWorkPolicy.REPLACE,
        )
    }

    fun getNewsItems() : LiveData<List<NewsItem>> = newsManager.getNews.livedata()

    fun getWebPageData(reference : String) : MutableLiveData<NewsItemWeb?> {
        viewModelScope.launch (Dispatchers.IO) {
            newsItemWeb.postValue(webProvider.getWebPageNews(reference))
        }
        return newsItemWeb
    }

}