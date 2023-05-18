package com.ead.project.dreamer.app.data.downloads

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.ead.project.dreamer.data.models.DownloadList
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

const val DOWNLOADS_ENQUEUE = "DOWNLOADS_ENQUEUE"
object DownloadSerializer : Serializer<DownloadList> {

    private val gson = Gson()

    override val defaultValue: DownloadList
        get() = DownloadList(emptyList())

    override suspend fun readFrom(input: InputStream): DownloadList {
        try {
            val reader = InputStreamReader(input, StandardCharsets.UTF_8)
            return gson.fromJson(
                reader, DownloadList::class.java
            )
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read protobuf", exception)
        }
    }

    override suspend fun writeTo(t: DownloadList, output: OutputStream) {
        val writer = OutputStreamWriter(output)
        gson.toJson(t, DownloadList::class.java,writer)
        withContext(Dispatchers.IO) {
            writer.flush()
        }
    }

}