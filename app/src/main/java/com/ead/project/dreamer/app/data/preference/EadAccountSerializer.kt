package com.ead.project.dreamer.app.data.preference

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.ead.project.dreamer.app.model.EadAccount
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

const val EAD_ACCOUNT = "EAD_ACCOUNT"
object EadAccountSerializer : Serializer<EadAccount?> {

    private val gson = Gson()
    override val defaultValue: EadAccount?
        get() = null

    override suspend fun readFrom(input: InputStream): EadAccount? {
        try {
            val reader = InputStreamReader(input, StandardCharsets.UTF_8)
            return gson.fromJson(
                reader, EadAccount::class.java
            )
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read protobuf", exception)
        }
    }

    override suspend fun writeTo(t: EadAccount?, output: OutputStream) {
        val writer = OutputStreamWriter(output)
        gson.toJson(t, EadAccount::class.java,writer)
        withContext(Dispatchers.IO) {
            writer.flush()
        }
    }

}