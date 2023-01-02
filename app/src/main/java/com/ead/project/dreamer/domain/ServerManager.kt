package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.servers.*
import javax.inject.Inject

class ServerManager @Inject constructor(
    val getServer: GetServer,
    val getServers: GetServers,
    val getEmbedServers: GetEmbedServers,
    val getEmbedServersMutable: GetEmbedServersMutable,
    val getSortedServers: GetSortedServers,
    val getServerScript: GetServerScript
)