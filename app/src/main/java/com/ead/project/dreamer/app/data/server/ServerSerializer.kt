package com.ead.project.dreamer.app.data.server

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.ead.project.dreamer.app.model.ServerPreference
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object ServerSerializer : Serializer<ServerPreference> {

    private val gson : Gson = Gson()
    override val defaultValue: ServerPreference
        get() = ServerPreference(isAutomatic = true, isProcessed = false, isDownloading = false)

    override suspend fun readFrom(input: InputStream): ServerPreference {
        try {
            val reader = InputStreamReader(input, StandardCharsets.UTF_8)
            return gson.fromJson(
                reader, ServerPreference::class.java
            )
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read protobuf", exception)
        }
    }

    override suspend fun writeTo(t: ServerPreference, output: OutputStream) {
        val writer = OutputStreamWriter(output)
        gson.toJson(t, ServerPreference::class.java,writer)
        withContext(Dispatchers.IO) {
            writer.flush()
        }
    }
}