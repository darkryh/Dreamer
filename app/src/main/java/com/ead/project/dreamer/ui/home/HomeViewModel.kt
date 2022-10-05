package com.ead.project.dreamer.ui.home

import androidx.lifecycle.*
import androidx.work.*
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.data.worker.HomeWorker
import com.ead.project.dreamer.data.worker.NewContentWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AnimeRepository,
    private val workManager: WorkManager,
    private val constraints: Constraints
): ViewModel() {


    fun getChaptersHome() : LiveData<List<ChapterHome>> = repository.getFlowChapterHome().asLiveData()

    fun getPublicity() = repository.getPublicityApp()!!

    fun getRecommendations() : LiveData<List<AnimeProfile>> =
        repository.getFlowProfileRandomRecommendationsList().asLiveData()

    fun synchronizeHome() {

        val syncingRequest =
            PeriodicWorkRequestBuilder<HomeWorker>(30, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            Constants.SYNC_HOME,
            ExistingPeriodicWorkPolicy.REPLACE,
            syncingRequest)
    }

    fun synchronizeNewContent() {

        val syncingRequest =
            PeriodicWorkRequestBuilder<NewContentWorker>(30, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            Constants.SYNC_NEW_CONTENT,
            ExistingPeriodicWorkPolicy.REPLACE,
            syncingRequest)
    }
}