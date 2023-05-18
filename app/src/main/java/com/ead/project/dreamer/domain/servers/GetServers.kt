package com.ead.project.dreamer.domain.servers

import com.ead.project.dreamer.data.models.Server
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetServers @Inject constructor(
    private val getServer: GetServer
) {

    private val servers : MutableList<Server> = mutableListOf()

    operator fun invoke(serverUrls : List<String>) : List<Server> {
        servers.clear()
        for (url in serverUrls) {
            servers.add(getServer(url))
            if (Server.isProcessEnded()) {
                Server.endAutomaticResolver()
                break
            }
        }
        return servers
    }

    suspend fun fromCoroutine(serverUrls: List<String>) : List<Server> =
        withContext(Dispatchers.IO) { invoke(serverUrls) }
}