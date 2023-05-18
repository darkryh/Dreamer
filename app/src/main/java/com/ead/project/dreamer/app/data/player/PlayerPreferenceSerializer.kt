package com.ead.project.dreamer.app.data.player

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.ead.project.dreamer.app.model.PlayerPreference
import com.ead.project.dreamer.app.model.Requester
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

const val PLAYER_PREFERENCE = "PLAYER_PREFERENCE"
object PlayerPreferenceSerializer : Serializer<PlayerPreference> {

    private val gson : Gson = Gson()

    override val defaultValue: PlayerPreference
        get() = PlayerPreference(
            isInExternalMode = false,
            isInPictureInPictureMode = true,
            requester = Requester.Deactivate,
            chapter = null,
            castingChapter = null
        )

    override suspend fun readFrom(input: InputStream): PlayerPreference {
        try {
            val reader = InputStreamReader(input, StandardCharsets.UTF_8)
            return gson.fromJson(
                reader, PlayerPreference::class.java
            )
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read protobuf", exception)
        }
    }

    override suspend fun writeTo(t: PlayerPreference, output: OutputStream) {
        val writer = OutputStreamWriter(output)
        gson.toJson(t, PlayerPreference::class.java,writer)
        withContext(Dispatchers.IO) {
            writer.flush()
        }
    }
}