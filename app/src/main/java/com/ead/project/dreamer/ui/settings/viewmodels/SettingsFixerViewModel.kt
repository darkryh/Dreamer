package com.ead.project.dreamer.ui.settings.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.domain.ChapterManager
import com.ead.project.dreamer.domain.HomeManager
import com.ead.project.dreamer.domain.configurations.LaunchOneTimeRequest
import com.ead.project.dreamer.domain.ProfileManager
import com.ead.project.dreamer.domain.ServerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SettingsFixerViewModel @Inject constructor(
    private val launchOneTimeRequest: LaunchOneTimeRequest,
    private val homeManager: HomeManager,
    private val chapterManager: ChapterManager,
    private val profileManager: ProfileManager,
    private val serverManager: ServerManager
) : ViewModel() {

    fun getConnectionState(url: String) : MutableLiveData<Int> {
        val state : MutableLiveData<Int> = MutableLiveData(0)
        viewModelScope.launch (Dispatchers.IO) {
            state.postValue(getConnection(url))
        }
        return state
    }

    fun synchronizeScrapper() {
        launchOneTimeRequest(
            LaunchOneTimeRequest.ScrapperWorkerCode,
            Constants.SYNC_SCRAPPER,
            ExistingWorkPolicy.REPLACE,
        )
    }

    fun isDataFromDatabaseOK() : Boolean  = runBlocking {
        try {
            val chaptersToFix = chapterManager.getChaptersToFix()
            val profilesToFix = profileManager.getProfilesToFix()
            Log.d("testing", "isDataFromDatabaseOK: chapters = $chaptersToFix")
            Log.d("testing", "isDataFromDatabaseOK: profiles = $profilesToFix")
            homeManager.getHomeList().first().isWorking()
                    && chaptersToFix.isEmpty()
                    && profilesToFix.isEmpty()
        } catch (e : Exception) { false }
    }

    fun getEmbedServers(timeoutTask : () -> Unit, chapter: Chapter) : LiveData<List<String>> =
        serverManager.getEmbedServersMutable(timeoutTask,chapter)

    private fun getConnection(url : String) : Int = Tools.isConnectionAvailableInt(url)
}