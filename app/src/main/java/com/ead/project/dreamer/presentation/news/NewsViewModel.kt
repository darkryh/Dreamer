package com.ead.project.dreamer.presentation.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import com.ead.project.dreamer.app.data.worker.Worker
import com.ead.project.dreamer.data.database.model.NewsItem
import com.ead.project.dreamer.data.models.NewsItemWeb
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.data.utils.AdManager
import com.ead.project.dreamer.domain.NewsUseCase
import com.ead.project.dreamer.domain.configurations.LaunchPeriodicTimeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsUseCase: NewsUseCase,
    private val launchPeriodicTimeRequest: LaunchPeriodicTimeRequest,
    private val webProvider: WebProvider,
    val adManager: AdManager
): ViewModel() {

    private val newsItemWeb : MutableLiveData<NewsItemWeb?> = MutableLiveData()

    fun synchronizeNews() {
        launchPeriodicTimeRequest(
            LaunchPeriodicTimeRequest.NewsWorkerCode,
            30,
            TimeUnit.MINUTES,
            Worker.SYNC_NEWS,
            ExistingPeriodicWorkPolicy.UPDATE,
        )
    }

    fun getNewsItems() : LiveData<List<NewsItem>> = newsUseCase.getNews.flow().asLiveData()

    fun getWebPageData(reference : String) : MutableLiveData<NewsItemWeb?> {
        viewModelScope.launch (Dispatchers.IO) {
            newsItemWeb.postValue(webProvider.getWebPageNews(reference))
        }
        return newsItemWeb
    }
}