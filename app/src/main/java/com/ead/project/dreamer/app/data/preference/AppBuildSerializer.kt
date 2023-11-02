package com.ead.project.dreamer.app.data.preference

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.ead.project.dreamer.app.AppInfo
import com.ead.project.dreamer.app.model.AppBuild
import com.ead.project.dreamer.data.models.Update
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

const val APP_BUILD = "APP_BUILD"
object AppBuildSerializer : Serializer<AppBuild> {

    private val gson = Gson()
    private val version = AppInfo.versionValue

    override val defaultValue: AppBuild
        get() =
            AppBuild(
                minVersion = version,
                update = Update(AppInfo.name, version),
                googleBuild = null,
                resumedVersionNotes = null,
                versionNotes = null,
                downloadReference = AppInfo.DEFAULT_DOWNLOAD_LINK_APP,
                currentVersionDeprecated = false,
                isUnlockedVersion = true,
                isDarkTheme = false
            )

    override suspend fun readFrom(input: InputStream): AppBuild {
        try {
            val reader = InputStreamReader(input, StandardCharsets.UTF_8)
            return gson.fromJson(
                reader, AppBuild::class.java
            )
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read protobuf", exception)
        }
    }

    override suspend fun writeTo(t: AppBuild, output: OutputStream) {
        val writer = OutputStreamWriter(output)
        gson.toJson(t, AppBuild::class.java,writer)
        withContext(Dispatchers.IO) {
            writer.flush()
        }
    }
}