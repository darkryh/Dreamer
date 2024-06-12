package com.ead.project.dreamer.domain.servers

import android.content.Context
import com.ead.lib.monoschinos.MonosChinos
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class GetEmbedServers @Inject constructor() {

    private val animeSeoRegex = "ver/([^/]+)".toRegex()
    private val reducer = "url=([^&]+)".toRegex()
    suspend operator fun invoke(chapter: Chapter,context: Context) : List<String>  {
        val seo = animeSeoRegex.find(chapter.reference)?.groupValues?.get(1) ?: "null"

        val player = MonosChinos
            .builder(context)
            .playerPage(seo)
            .get() ?: return emptyList()

        val embedServers : MutableList<String> = mutableListOf()
        embedServers.addAll(player.options)
        embedServers.addAll(player.downloads)

        return embedServers.map {
            reducer.find(it)?.groupValues?.get(1) ?: it
        }
    }
}