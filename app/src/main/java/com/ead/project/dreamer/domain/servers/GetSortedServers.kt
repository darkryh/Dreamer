package com.ead.project.dreamer.domain.servers

import com.ead.project.dreamer.app.data.server.Server
import com.ead.project.dreamer.domain.PreferenceUseCase
import javax.inject.Inject

class GetSortedServers @Inject constructor(
    private val serverIdentifier: ServerIdentifier,
    preferenceUseCase: PreferenceUseCase
) {

    private val playerPreferences = preferenceUseCase.playerPreferences
    private var serversOrder : List<String> = emptyList()

    operator fun invoke(embedList : List<String>, isDownload : Boolean) : MutableList<String> {
        val servers : MutableList<Pair<Int,String>> = ArrayList()

        serversOrder = if (!isDownload) {
            if (!playerPreferences.isInExternalMode())
                Server.getSortedInternalServers()
            else
                Server.getSortedExternalServers()
        }
        else Server.getSortedDownloadServers()

        for (embedUrl in embedList) servers.add(Pair(getIndexed(embedUrl),embedUrl))
        
        return servers.sorted()
    }

    private fun getIndexed(data : String) : Int {
        serversOrder.forEachIndexed { index, server ->
            if (serverIdentifier(data) == server)
                return index
        }
        return 10000
    }
    
    private fun MutableList<Pair<Int,String>>.sorted() : MutableList<String> =
        this.apply { sortBy { it.first } }
            .map { it.second }
            .toMutableList()
}