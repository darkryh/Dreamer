package com.ead.project.dreamer.data.models

import android.content.Context
import com.ead.lib.moongetter.models.Server
import com.ead.lib.moongetter.models.Video

open class Server(
    context: Context,
    url : String,
    open val isDirect : Boolean = true,
) : Server(context, url) {

    val videoList : List<VideoModel> get() = _videList
    private val _videList : MutableList<VideoModel> = mutableListOf()

    fun add(videos: List<Video>) {
        _videList.addAll(
            videos.map {
                VideoModel(
                    quality = it.quality ?: "Default",
                    directLink = it.url
                )
            }
        )
    }
}