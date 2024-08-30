package com.ead.project.dreamer.app.data.util.system

import android.content.Context
import com.ead.lib.moongetter.models.Server
import com.ead.lib.moongetter.server_sites.Filemoon
import com.ead.project.dreamer.data.models.server.DoodStream
import com.ead.project.dreamer.data.models.server.Mega
import com.ead.project.dreamer.data.models.server.MegaUp
import com.ead.project.dreamer.data.models.server.MixDrop
import com.ead.project.dreamer.data.models.server.Mp4Upload
import com.ead.project.dreamer.data.models.server.Uptobox
import com.ead.project.dreamer.data.models.server.Uqload
import com.ead.project.dreamer.data.models.server.VidGuard
import com.ead.project.dreamer.data.models.server.YourUpload

fun Server.toNormal(context: Context, url : String) : com.ead.project.dreamer.data.models.Server {
    return com.ead.project.dreamer.data.models.Server(
        context = context,
        url = url,
        isDirect = !embedClassList.contains(this::class.java)
    ).also {
        it.add(videos)
    }
}

val embedClassList : List<Class<out Server>> = listOf(
    DoodStream::class.java,
    Filemoon::class.java,
    Mega::class.java,
    MegaUp::class.java,
    MixDrop::class.java,
    Mp4Upload::class.java,
    Uptobox::class.java,
    Uqload::class.java,
    VidGuard::class.java,
    YourUpload::class.java
)