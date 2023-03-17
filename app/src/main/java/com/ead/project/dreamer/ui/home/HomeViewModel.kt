package com.ead.project.dreamer.ui.home

import androidx.lifecycle.*
import androidx.work.*
import com.ead.project.dreamer.app.model.Publicity
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.domain.*
import com.ead.project.dreamer.domain.configurations.LaunchPeriodicTimeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val applicationUseCase: ApplicationUseCase,
    private val homeUseCase: HomeUseCase,
    private val launchPeriodicTimeRequest: LaunchPeriodicTimeRequest,
): ViewModel() {


    fun getChaptersHome() : LiveData<List<ChapterHome>> = homeUseCase.getHomeList.livedata()

    fun getPublicity() : LiveData<List<Publicity>> = applicationUseCase.getApplicationAds.livedata()

    fun getRecommendations() : LiveData<List<AnimeProfile>> = homeUseCase.getHomeRecommendations.livedata()

    fun synchronizeHome() {
        launchPeriodicTimeRequest(
            LaunchPeriodicTimeRequest.HomeWorkerCode,
            30,
            TimeUnit.MINUTES,
            Constants.SYNC_HOME,
            ExistingPeriodicWorkPolicy.UPDATE
        )
    }

    fun synchronizeNewContent() {
        launchPeriodicTimeRequest(
            LaunchPeriodicTimeRequest.NewContentWorkerCode,
            30,
            TimeUnit.MINUTES,
            Constants.SYNC_NEW_CONTENT,
            ExistingPeriodicWorkPolicy.UPDATE
        )
    }
}