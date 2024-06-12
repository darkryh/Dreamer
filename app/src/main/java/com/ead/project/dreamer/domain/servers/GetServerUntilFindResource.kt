package com.ead.project.dreamer.domain.servers

import android.content.Context
import com.ead.lib.moongetter.MoonGetter
import com.ead.project.dreamer.app.data.util.system.toNormal
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.server_properties.ONE_FICHIER_API_TOKEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetServerUntilFindResource @Inject constructor(
    private val context: Context
) {

    suspend operator fun invoke(serverUrls : List<String>) : Server? {
        return MoonGetter
            .initialize(context)
            .connect(serverUrls)
            .setCustomServers(com.ead.project.dreamer.app.data.server.Server.serverIntegrationList)
            .set1FichierToken(ONE_FICHIER_API_TOKEN)
            .getUntilFindResource()
            ?.toNormal(
                context,
                "Example"
            )
    }

    suspend fun fromCoroutine(serverUrls: List<String>) : List<Server> =
        withContext(Dispatchers.IO) { emptyList() }
}