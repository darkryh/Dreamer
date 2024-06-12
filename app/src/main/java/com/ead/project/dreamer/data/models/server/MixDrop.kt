package com.ead.project.dreamer.data.models.server

import android.content.Context
import com.ead.project.dreamer.data.models.EmbedServer

class MixDrop(context: Context, url : String) : EmbedServer(context, url) {
    override fun isAvailable(): Boolean { return !super.isAvailable() }
}