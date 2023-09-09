package com.ead.project.dreamer.app.data.server

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.ead.project.dreamer.app.model.AutomaticServerPreference
import com.ead.project.dreamer.data.utils.ServerOrder
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object AutomaticServerSerializer : Serializer<AutomaticServerPreference> {

    private val gson : Gson = Gson()
    override val defaultValue: AutomaticServerPreference
        get() =
            AutomaticServerPreference(
                internalServerList = ServerOrder.internalServers,
                externalServerList = ServerOrder.externalServers,
                downloadServerList = ServerOrder.downloadServers
            )

    override suspend fun readFrom(input: InputStream): AutomaticServerPreference {
        try {
            val reader = InputStreamReader(input, StandardCharsets.UTF_8)
            return gson.fromJson(
                reader, AutomaticServerPreference::class.java
            )
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read protobuf", exception)
        }
    }

    override suspend fun writeTo(t: AutomaticServerPreference, output: OutputStream) {
        val writer = OutputStreamWriter(output)
        gson.toJson(t, AutomaticServerPreference::class.java,writer)
        withContext(Dispatchers.IO) {
            writer.flush()
        }
    }
}