package com.ead.project.dreamer.app.data.files

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.ead.project.dreamer.app.model.FilePreference
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

const val FILES_PREFERENCE = "FILES_PREFERENCE"
object FilesSerializer : Serializer<FilePreference> {

    private val gson : Gson = Gson()

    override val defaultValue: FilePreference
        get() = FilePreference(
            mainFolderPath = Files.mainFile.absolutePath,
            seriesFolderPath = Files.seriesFile.absolutePath,
            isMainFolderCreated = false,
            isSeriesFolderCreated = false,
            isFirstTimeChecking = true
        )

    override suspend fun readFrom(input: InputStream): FilePreference {
        try {
            return gson.fromJson(input.reader().readText(), FilePreference::class.java)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read protobuf", exception)
        }
    }

    override suspend fun writeTo(t: FilePreference, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(gson.toJson(t, FilePreference::class.java).toByteArray())
        }
    }

}