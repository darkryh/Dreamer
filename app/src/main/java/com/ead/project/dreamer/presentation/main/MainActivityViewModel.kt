package com.ead.project.dreamer.presentation.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import com.ead.project.dreamer.app.AppInfo
import com.ead.project.dreamer.app.BypassInitializer
import com.ead.project.dreamer.app.data.player.casting.CastManager
import com.ead.project.dreamer.app.data.worker.Worker
import com.ead.project.dreamer.app.model.EadAccount
import com.ead.project.dreamer.app.model.GoogleBuild
import com.ead.project.dreamer.app.repository.FirebaseClient
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.domain.ApplicationUseCase
import com.ead.project.dreamer.domain.DirectoryUseCase
import com.ead.project.dreamer.domain.DownloadUseCase
import com.ead.project.dreamer.domain.ObjectUseCase
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.domain.UpdateUseCase
import com.ead.project.dreamer.domain.configurations.InstallWorkers
import com.ead.project.dreamer.domain.configurations.LaunchPeriodicTimeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val launchPeriodicTimeRequest: LaunchPeriodicTimeRequest,
    private val applicationUseCase: ApplicationUseCase,
    private val installWorkers: InstallWorkers,
    private val directoryUseCase: DirectoryUseCase,
    private val objectUseCase: ObjectUseCase,
    private val client: FirebaseClient,
    val downloadUseCase: DownloadUseCase,
    val updateUseCase: UpdateUseCase,
    val castManager: CastManager,
    preferenceUseCase: PreferenceUseCase,
    context: Context
) : ViewModel() {

    private val preferences = preferenceUseCase.preferences
    private val filesPreferences = preferenceUseCase.filesPreferences
    private val appBuildPreferences = preferenceUseCase.appBuildPreferences
    private val userPreferences = preferenceUseCase.userPreferences

    private val bypassInitializer by lazy { BypassInitializer(context) }

    init {
        fetchingPreferences()
        synchronizeScrapper()
        synchronizeHome()
        synchronizeNews()
        synchronizeNewContent()
        synchronizeDirectory()
        synchronizeReleases()
    }

    fun onResume() {
        castManager.onResume()
    }

    fun onPause() {
        castManager.onPause()
    }

    fun onDestroy() {
        castManager.onDestroy()
        bypassInitializer.onDestroy()
    }

    fun subscribeToAppTopicIf(isSubscribed : Boolean) {
        if(isSubscribed) {
            client.inAppMessage.subscribeToTopic(AppInfo.TOPIC)
        }
        else {
            client.inAppMessage.unsubscribeFromTopic(AppInfo.TOPIC)
        }
    }

    fun getAccount() : Flow<EadAccount?> = userPreferences.user

    fun getIsAppNotificationActivated() : Flow<Boolean> = preferences.getBooleanFlow(AppInfo.TOPIC,true)

    fun getStatusApp() = applicationUseCase.getAppStatusVersion.livedata()

    fun getDirectoryState() : Flow<Boolean> = directoryUseCase.getDirectoryState()

    fun updateVersion(version : Double) {
        appBuildPreferences.updateVersion(version)
    }

    fun getContractIfIsGoogleBuild() : Flow<GoogleBuild?> = appBuildPreferences.googleBuild

    private fun fetchingPreferences() {
        filesPreferences.fetchingPreferences()
    }

    private fun synchronizeDirectory () = installWorkers()

    private fun synchronizeHome() {
        launchPeriodicTimeRequest(
            LaunchPeriodicTimeRequest.HomeWorkerCode,
            15,
            TimeUnit.MINUTES,
            Worker.SYNC_HOME,
            ExistingPeriodicWorkPolicy.UPDATE
        )
    }

    private fun synchronizeNews() {
        launchPeriodicTimeRequest(
            LaunchPeriodicTimeRequest.NewsWorkerCode,
            30,
            TimeUnit.MINUTES,
            Worker.SYNC_NEWS,
            ExistingPeriodicWorkPolicy.UPDATE,
        )
    }

    private fun synchronizeScrapper() {
        launchPeriodicTimeRequest(
            LaunchPeriodicTimeRequest.ScrapperWorkerCode,
            3,
            TimeUnit.DAYS,
            Worker.SYNC_SCRAPPER,
            ExistingPeriodicWorkPolicy.KEEP
        )
    }

    private fun synchronizeNewContent() {
        launchPeriodicTimeRequest(
            LaunchPeriodicTimeRequest.NewContentWorkerCode,
            15,
            TimeUnit.MINUTES,
            Worker.SYNC_NEW_CONTENT,
            ExistingPeriodicWorkPolicy.UPDATE
        )
    }

    private fun synchronizeReleases() {
        launchPeriodicTimeRequest(
            LaunchPeriodicTimeRequest.UpdateReleasesWorkerCode,
            7,
            TimeUnit.DAYS,
            Worker.SYNC_RELEASES,
            ExistingPeriodicWorkPolicy.KEEP
        )
    }

    fun updateChapter(chapter: Chapter) = viewModelScope.launch (Dispatchers.IO) {
        objectUseCase.updateObject(chapter)
    }
}