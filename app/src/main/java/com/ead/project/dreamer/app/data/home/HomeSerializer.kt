package com.ead.project.dreamer.app.data.home

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.ead.project.dreamer.app.model.HomePreference
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

const val HOME_PREFERENCES = "HOME_PREFERENCES"
object HomeSerializer : Serializer<HomePreference> {

    private val gson : Gson = Gson()

    override val defaultValue: HomePreference
        get() = HomePreference(
            list = emptyList(),
            notifyingIndex = HomePreferences.NOTIFICATION_DEFAULT
        )

    override suspend fun readFrom(input: InputStream): HomePreference {
        try {
            val reader = InputStreamReader(input, StandardCharsets.UTF_8)
            return gson.fromJson(
                reader, HomePreference::class.java
            )
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read protobuf", exception)
        }
    }

    override suspend fun writeTo(t: HomePreference, output: OutputStream) {
        val writer = OutputStreamWriter(output)
        gson.toJson(t, HomePreference::class.java,writer)
        withContext(Dispatchers.IO) {
            writer.flush()
        }
    }
}