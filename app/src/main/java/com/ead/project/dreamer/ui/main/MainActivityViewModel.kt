package com.ead.project.dreamer.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.worker.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val constraints: Constraints,
    private val repository: AnimeRepository,
) : ViewModel() {

    lateinit var directoryId : UUID

    fun getStatusApp() = repository.getAppStatus()!!

    fun directoryState() = DataStore.flowBoolean(Constants.FINAL_DIRECTORY)

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

            if (i == 1) directoryId = syncingChaptersRequest.id

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

    //fun directoryObserver(context : Context) = WorkManager.getInstance(context).getWorkInfoByIdLiveData(directoryId)

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

    fun synchronizeScrapper() {
        val syncingRequest =
            PeriodicWorkRequestBuilder<ScrapperWorker>(3, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            Constants.SYNC_SCRAPPER,
            ExistingPeriodicWorkPolicy.KEEP,
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

    fun getGuildMember(id : String) = repository.getGuildMember(id)!!

    fun updateChapter(chapter: Chapter) {
        viewModelScope.launch (Dispatchers.IO) {
            repository.updateChapter(chapter)
        }
    }

}