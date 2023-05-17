package com.ead.project.dreamer.app.data.ads

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.ead.project.dreamer.app.model.AdPreference
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

const val AD_PREFERENCE = "AD_PREFERENCES"
object AdPreferencesSerializer : Serializer<AdPreference> {

    private val gson = Gson()

    override val defaultValue: AdPreference
        get() = AdPreference(viewedTimes = 0)

    override suspend fun readFrom(input: InputStream): AdPreference {
        try {
            val reader = InputStreamReader(input, StandardCharsets.UTF_8)
            return gson.fromJson(
                reader, AdPreference::class.java
            )
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read protobuf", exception)
        }
    }

    override suspend fun writeTo(t: AdPreference, output: OutputStream) {
        val writer = OutputStreamWriter(output)
        gson.toJson(t, AdPreference::class.java,writer)
        withContext(Dispatchers.IO) {
            writer.flush()
        }
    }

}