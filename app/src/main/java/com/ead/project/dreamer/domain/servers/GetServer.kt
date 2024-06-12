package com.ead.project.dreamer.domain.servers

import android.content.Context
import com.ead.lib.moongetter.MoonGetter
import com.ead.project.dreamer.app.data.server.Server
import com.ead.project.dreamer.app.data.util.system.toNormal
import com.ead.project.dreamer.data.models.server_properties.ONE_FICHIER_API_TOKEN
import javax.inject.Inject

class GetServer @Inject constructor(
    private val context: Context
) {

    suspend operator fun invoke(url : String) : com.ead.project.dreamer.data.models.Server? {
        val server = MoonGetter
            .initialize(context)
            .connect(url)
            .setCustomServers(Server.serverIntegrationList)
            .set1FichierToken(ONE_FICHIER_API_TOKEN)
            .get()

        return server?.toNormal(
            context,
            url
        )
    }
}