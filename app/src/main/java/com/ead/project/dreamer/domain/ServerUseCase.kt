package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.servers.*
import javax.inject.Inject

class ServerUseCase @Inject constructor(
    val getServer: GetServer,
    val getServerUntilFindResource: GetServerUntilFindResource,
    val getSortedServers: GetSortedServers,
    val getEmbedServers: GetEmbedServers
)