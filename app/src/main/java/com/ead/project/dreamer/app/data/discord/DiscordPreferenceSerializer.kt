package com.ead.project.dreamer.app.data.discord

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.ead.project.dreamer.data.models.discord.DiscordPreference
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

const val DISCORD_USER = "DISCORD_USER"
object DiscordUserSerializer : Serializer<DiscordPreference> {

    private val gson = Gson()
    override val defaultValue: DiscordPreference
        get() = DiscordPreference(null,null,null,null)

    override suspend fun readFrom(input: InputStream): DiscordPreference {
        try {
            val reader = InputStreamReader(input, StandardCharsets.UTF_8)
            return gson.fromJson(
                reader, DiscordPreference::class.java
            )
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read protobuf", exception)
        }
    }

    override suspend fun writeTo(t: DiscordPreference, output: OutputStream) {
        val writer = OutputStreamWriter(output)
        gson.toJson(t, DiscordPreference::class.java,writer)
        withContext(Dispatchers.IO) {
            writer.flush()
        }
    }
}