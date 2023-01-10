package com.ead.project.dreamer.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.domain.*
import com.ead.project.dreamer.domain.configurations.InstallWorkers
import com.ead.project.dreamer.domain.configurations.LaunchPeriodicTimeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val launchPeriodicTimeRequest: LaunchPeriodicTimeRequest,
    private val applicationManager: ApplicationManager,
    private val installWorkers: InstallWorkers,
    private val discordManager: DiscordManager,
    private val objectManager: ObjectManager
) : ViewModel() {

    fun getStatusApp() = applicationManager.getAppStatusVersion.livedata()

    fun directoryState() = DataStore.flowBoolean(Constants.FINAL_DIRECTORY).asLiveData()

    fun synchronizeDirectory () = installWorkers()

    fun synchronizeHome() {
        launchPeriodicTimeRequest(
            LaunchPeriodicTimeRequest.HomeWorkerCode,
            15,
            TimeUnit.MINUTES,
            Constants.SYNC_HOME,
            ExistingPeriodicWorkPolicy.REPLACE
        )
    }

    fun synchronizeScrapper() {
        launchPeriodicTimeRequest(
            LaunchPeriodicTimeRequest.ScrapperWorkerCode,
            3,
            TimeUnit.DAYS,
            Constants.SYNC_SCRAPPER,
            ExistingPeriodicWorkPolicy.KEEP
        )
    }

    fun synchronizeNewContent() {
        launchPeriodicTimeRequest(
            LaunchPeriodicTimeRequest.NewContentWorkerCode,
            15,
            TimeUnit.MINUTES,
            Constants.SYNC_NEW_CONTENT,
            ExistingPeriodicWorkPolicy.REPLACE
        )
    }

    fun synchronizeReleases() {
        launchPeriodicTimeRequest(
            LaunchPeriodicTimeRequest.UpdateReleasesWorkerCode,
            7,
            TimeUnit.DAYS,
            Constants.SYNC_RELEASES,
            ExistingPeriodicWorkPolicy.KEEP
        )
    }

    fun getGuildMember(id : String) = discordManager.getDiscordMember.livedata(id)

    fun updateChapter(chapter: Chapter) =
        viewModelScope.launch (Dispatchers.IO) { objectManager.updateObject(chapter) }

}