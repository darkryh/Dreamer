package com.ead.project.dreamer.ui.news

import androidx.lifecycle.*
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ead.project.dreamer.app.model.scrapping.NewsItemWebScrap
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.NewsItemWeb
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.data.worker.NewsWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: AnimeRepository,
    private val workManager: WorkManager,
    private val constraints: Constraints,
    private val webProvider: WebProvider
): ViewModel() {

    private val newsItemWeb : MutableLiveData<NewsItemWeb?> = MutableLiveData()

    fun synchronizeNews() {
        val syncingRequest =
            PeriodicWorkRequestBuilder<NewsWorker>(30, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            Constants.SYNC_NEWS,
            ExistingPeriodicWorkPolicy.KEEP,
            syncingRequest)
    }

    fun getNewsItems() = repository.getFlowNewsItems().asLiveData()

    fun getWebPageData(reference : String) : MutableLiveData<NewsItemWeb?> {
        viewModelScope.launch (Dispatchers.IO) {
            val newsItemScrap : NewsItemWebScrap = NewsItemWebScrap.get()?: NewsItemWebScrap.getDataFromApi(repository)
            newsItemWeb.postValue(webProvider.getWebPageNews(reference,newsItemScrap))
        }
        return newsItemWeb
    }

}