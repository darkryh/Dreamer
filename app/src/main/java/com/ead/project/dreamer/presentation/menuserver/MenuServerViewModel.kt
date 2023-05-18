package com.ead.project.dreamer.presentation.menuserver

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.app.data.player.casting.CastManager
import com.ead.project.dreamer.domain.*
import com.ead.project.dreamer.domain.servers.LaunchVideo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuServerViewModel @Inject constructor (
    private val serverUseCase: ServerUseCase,
    val downloadUseCase: DownloadUseCase,
    val castManager: CastManager,
    val launchVideo: LaunchVideo,
    preferenceUseCase: PreferenceUseCase
) : ViewModel() {

    val playerPreferences = preferenceUseCase.playerPreferences

    private var servers : MutableLiveData<List<Server>> = MutableLiveData()

    fun fetchingCastingPreferences() {
        if (castManager.isConnectedToChromeCast) {
            castManager.updatedChapter()
        }
    }

    fun setDownloadMode(value : Boolean) {
        com.ead.project.dreamer.app.data.server.Server.setDownloading(value)
    }

    fun getEmbedServers(timeoutTask : () -> Unit, chapter: Chapter) : LiveData<List<String>> =
        serverUseCase.getEmbedServersMutable(timeoutTask,chapter)

    fun getServers(embeddedUrlServers : List<String>): LiveData<List<Server>> {
        viewModelScope.launch (Dispatchers.IO) {
            servers.postValue(serverUseCase.getServers(embeddedUrlServers))
        }
        return servers
    }

    fun getServer(embedUrl : String) : LiveData<Server>  {
        val tempServer : MutableLiveData<Server> = MutableLiveData()
        viewModelScope.launch (Dispatchers.IO) {
            tempServer.postValue(serverUseCase.getServer(embedUrl))
        }
        return tempServer
    }

    fun getSortedServer(embedList: List<String>, isDownload : Boolean) =
        serverUseCase.getSortedServers(embedList,isDownload)

    fun onDestroy() { serverUseCase.getEmbedServersMutable.onDestroy() }
}