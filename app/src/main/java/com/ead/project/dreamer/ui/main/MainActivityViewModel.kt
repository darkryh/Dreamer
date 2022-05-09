package com.ead.project.dreamer.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.worker.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val constraints: Constraints,
    private val repository: AnimeRepository
) : ViewModel() {

    fun getStatusApp() = repository.getAppStatus()!!

    fun synchronizeDirectory (workersQuantity : Int = 3) {

        val directoryRequest : MutableList<OneTimeWorkRequest> = ArrayList()

        for (i in 1 until workersQuantity + 1) {

            val data : Data = Data.Builder()
                .putInt(Constants.DIRECTORY_KEY,i)
                .build()

            val syncingChaptersRequest =
                OneTimeWorkRequestBuilder<DirectoryWorker>()
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build()

            directoryRequest.add(syncingChaptersRequest)

        }

        var continuation = workManager.beginUniqueWork(
            Constants.SYNC_DIRECTORY,
            ExistingWorkPolicy.REPLACE,
            directoryRequest)

        val syncingProfilingRequest =
            OneTimeWorkRequestBuilder<ProfileRepositoryWorker>()
                .setConstraints(constraints)
                .build()

        continuation = continuation.then(syncingProfilingRequest)

        continuation.enqueue()
    }

    fun synchronizeHome() {
        val syncingRequest =
            PeriodicWorkRequestBuilder<HomeWorker>(15, TimeUnit.MINUTES)
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

    fun synchronizeReleases() {
        val syncingReleasesRequest =
            PeriodicWorkRequestBuilder<UpdateReleasesWorker>(7, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            Constants.SYNC_RELEASES,
            ExistingPeriodicWorkPolicy.KEEP,
            syncingReleasesRequest)
    }

    fun synchronizeNotifications() {
        val syncingNotifications =
            PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            Constants.SYNC_SERIES_NOTIFICATIONS,
            ExistingPeriodicWorkPolicy.REPLACE,
            syncingNotifications)
    }


    fun getGuildMember(id : String) = repository.getGuildMember(id)!!

    fun updateChapter(chapter: Chapter) {
        viewModelScope.launch (Dispatchers.IO) {
            repository.updateChapter(chapter)
        }
    }
}