package com.ead.project.dreamer.domain.servers

import android.content.Context
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class GetEmbedServers @Inject constructor(
    private val context: Context,
    private val getServerResultToArray: GetServerResultToArray,
    private val serverScript: ServerScript,
) {

    private lateinit var chapter: Chapter

    operator fun invoke(timeoutTask : () -> Unit,chapter: Chapter)  {
        this.chapter = chapter
        serverEngine(timeoutTask, chapter)
    }

    private val serverEngine = object  : ServerEngine(context,getServerResultToArray,serverScript) {
        override fun getServerList(it: String): List<String> {
            val result = super.getServerList(it)
            return result
        }
    }

}