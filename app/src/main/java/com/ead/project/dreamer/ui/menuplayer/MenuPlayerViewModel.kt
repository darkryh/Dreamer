package com.ead.project.dreamer.ui.menuplayer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.ServerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MenuPlayerViewModel @Inject constructor (
    embedList : MutableList<String>
) : ViewModel() {

    private var serverFactory : ServerFactory = ServerFactory(embedList)
    private var serverList : MutableLiveData<MutableList<Server>> = MutableLiveData()

    fun getServerList (): MutableLiveData<MutableList<Server>> {
        viewModelScope.launch (Dispatchers.IO) {
            serverList.postValue(serverFactory.getServers())
        }
        return serverList
    }
}