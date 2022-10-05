package com.ead.project.dreamer.ui.menuplayer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.utils.ServerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MenuPlayerViewModel @Inject constructor () : ViewModel() {

    private var serverList : MutableLiveData<List<Server>> = MutableLiveData()

    fun getServerList(embedList : MutableList<String>): MutableLiveData<List<Server>> {
        viewModelScope.launch (Dispatchers.IO) {
            serverList.postValue(ServerManager.getServersList(embedList))
        }
        return serverList
    }

    fun getServer(rawServer : String) : MutableLiveData<Server>  {
        val serverSelector : MutableLiveData<Server> = MutableLiveData()
        viewModelScope.launch (Dispatchers.IO) {
            serverSelector.postValue(ServerManager.getServer(rawServer))
        }
        return serverSelector
    }
}