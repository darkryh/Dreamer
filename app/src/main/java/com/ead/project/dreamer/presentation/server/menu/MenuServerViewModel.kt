package com.ead.project.dreamer.presentation.server.menu

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ead.lib.moongetter.models.exceptions.InvalidServerException
import com.ead.project.dreamer.app.data.player.casting.CastManager
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.domain.DownloadUseCase
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.domain.ServerUseCase
import com.ead.project.dreamer.domain.servers.LaunchVideo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
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
    private val embedServer : MutableLiveData<List<String>> = MutableLiveData()

    fun getIfChapterIsCasting() : Chapter? {
        if (castManager.isConnectedToChromeCast) {
            return castManager.getUpdatedChapter()
        }
        return null
    }

    fun setDownloadMode(value : Boolean) {
        com.ead.project.dreamer.app.data.server.Server.setDownloading(value)
    }

    fun getEmbedServers(chapter: Chapter,context: Context) : LiveData<List<String>> {
        viewModelScope.launch(Dispatchers.IO) {
            embedServer.postValue(serverUseCase.getEmbedServers(chapter, context))
        }

        return embedServer
    }

    fun getServers(embeddedUrlServers : List<String>): LiveData<Server?> {
        val tempServer : MutableLiveData<Server?> = MutableLiveData()
        viewModelScope.launch (Dispatchers.IO) {
            tempServer.postValue(serverUseCase.getServerUntilFindResource(embeddedUrlServers))
        }
        return tempServer
    }

    fun getServer(embedUrl : String) : LiveData<Server?>  {
        val tempServer : MutableLiveData<Server?> = MutableLiveData()
        viewModelScope.launch (Dispatchers.IO) {
            try {
                tempServer.postValue(serverUseCase. getServer(embedUrl))
            }catch (e : InvalidServerException) {
                e.printStackTrace()
                tempServer.postValue(null)
            }
            catch (e : IOException) {
                e.printStackTrace()
                tempServer.postValue(null)
            }
        }
        return tempServer
    }

    fun getSortedServer(embedList: List<String>, isDownload : Boolean) =
        serverUseCase.getSortedServers(embedList,isDownload)

}