package com.ead.project.dreamer.ui.menuserver

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuServerViewModel @Inject constructor (
    private val serverUseCase: ServerUseCase)
: ViewModel() {

    private var servers : MutableLiveData<List<Server>> = MutableLiveData()

    fun getEmbedServers(timeoutTask : () -> Unit, chapter: Chapter) : LiveData<List<String>> =
        serverUseCase.getEmbedServersMutable(timeoutTask,chapter)

    fun getServers(embedList : List<String>): LiveData<List<Server>> {
        viewModelScope.launch (Dispatchers.IO) {
            servers.postValue(serverUseCase.getServers(embedList))
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