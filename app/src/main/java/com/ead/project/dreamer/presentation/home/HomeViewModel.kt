package com.ead.project.dreamer.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.work.ExistingPeriodicWorkPolicy
import com.ead.project.dreamer.app.data.worker.Worker
import com.ead.project.dreamer.app.model.Publicity
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.data.database.model.NewsItem
import com.ead.project.dreamer.domain.ApplicationUseCase
import com.ead.project.dreamer.domain.HomeUseCase
import com.ead.project.dreamer.domain.NewsUseCase
import com.ead.project.dreamer.domain.configurations.LaunchPeriodicTimeRequest
import com.ead.project.dreamer.domain.downloads.LaunchDownload
import com.ead.project.dreamer.domain.servers.HandleChapter
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val applicationUseCase: ApplicationUseCase,
    private val homeUseCase: HomeUseCase,
    private val newsUseCase: NewsUseCase,
    private val launchPeriodicTimeRequest: LaunchPeriodicTimeRequest,
    val handleChapter: HandleChapter,
    val launchDownload: LaunchDownload
): ViewModel() {


    fun getChaptersHome() : LiveData<List<ChapterHome>> = homeUseCase.getHomeList.previewLivedata()

    fun getPublicity() : LiveData<List<Publicity>> = applicationUseCase.getApplicationAds.livedata()

    fun getRecommendations() : LiveData<List<AnimeProfile>> = homeUseCase.getHomeRecommendations.livedata()

    fun getLimitedNews() : LiveData<List<NewsItem>> = newsUseCase.getNews.flowLimited().asLiveData()

    fun synchronizeHome() {
        launchPeriodicTimeRequest(
            LaunchPeriodicTimeRequest.HomeWorkerCode,
            30,
            TimeUnit.MINUTES,
            Worker.SYNC_HOME,
            ExistingPeriodicWorkPolicy.UPDATE
        )
    }

    fun synchronizeNewContent() {
        launchPeriodicTimeRequest(
            LaunchPeriodicTimeRequest.NewContentWorkerCode,
            30,
            TimeUnit.MINUTES,
            Worker.SYNC_NEW_CONTENT,
            ExistingPeriodicWorkPolicy.UPDATE
        )
    }
}