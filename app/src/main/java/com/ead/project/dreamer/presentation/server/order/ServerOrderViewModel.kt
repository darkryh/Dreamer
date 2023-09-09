package com.ead.project.dreamer.presentation.server.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.app.data.preference.AppBuildPreferences
import com.ead.project.dreamer.app.data.server.Server
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServerOrderViewModel @Inject constructor(
    val appBuildPreferences : AppBuildPreferences
) : ViewModel() {

    fun getServers(option : Int) : LiveData<List<String>> {
        return when(option) {
            ServerOrderFragment.INTERNAL_SERVERS -> getSortedInternalServers()
            ServerOrderFragment.EXTERNAL_SERVERS -> getSortedExternalServers()
            ServerOrderFragment.DOWNLOAD_SERVERS -> getSortedDownloadServers()
            else -> getSortedInternalServers()
        }
    }

    fun setServers(option: Int,servers : List<String>) {
        when(option) {
            ServerOrderFragment.INTERNAL_SERVERS -> setSortedInternalServers(servers)
            ServerOrderFragment.EXTERNAL_SERVERS -> setSortedExternalServers(servers)
            ServerOrderFragment.DOWNLOAD_SERVERS -> setSortedDownloadServers(servers)
            else -> setSortedInternalServers(servers)
        }
    }

    private fun setSortedInternalServers(servers : List<String>) =
        Server.setSortedInternalServers(servers)
    private fun setSortedExternalServers(servers : List<String>) =
        Server.setSortedExternalServers(servers)
    private fun setSortedDownloadServers(servers : List<String>) =
        Server.setSortedDownloadServers(servers)

    private fun getSortedInternalServers() : LiveData<List<String>> = Server.getSortedInternalServersLiveData()
    private fun getSortedExternalServers() : LiveData<List<String>> = Server.getSortedExternalServersLiveData()
    private fun getSortedDownloadServers() : LiveData<List<String>> = Server.getSortedDownloadServersLiveData()

}