package com.ead.project.dreamer.domain.servers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class GetEmbedServersMutable @Inject constructor(
    private val context: Context,
    private val getServerResultToArray: GetServerResultToArray,
    private val serverScript: ServerScript
) {
    private lateinit var embedServers : MutableLiveData<List<String>>

    operator fun invoke(timeoutTask : () -> Unit,chapter: Chapter) : LiveData<List<String>> {
        embedServers = MutableLiveData()
        serverEngine(timeoutTask, chapter)
        return embedServers
    }

    private val serverEngine = object  : ServerEngine(context,getServerResultToArray,serverScript) {
        override fun getServerList(it: String): List<String> {
            val result = super.getServerList(it)
            embedServers.postValue(result)
            return result
        }
    }

    fun onDestroy() = serverEngine.onDestroy()
}